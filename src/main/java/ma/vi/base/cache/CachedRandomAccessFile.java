/*
 * Copyright (c) 2018 Vikash Madhow
 */

package ma.vi.base.cache;

import java.io.*;
import java.nio.channels.Channels;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A cache for a random access file containing serialized objects.
 *
 * @param <E> The type of records that the random access file will store.
 * @author vikash.madhow@gmail.com
 */
public class CachedRandomAccessFile<E extends Serializable> implements AutoCloseable {
  /**
   * Construct for the specified random access file and cache size.
   *
   * @param file      The RandomAccessFile to cache reads and writes. This file must be open for all modes that this
   *                  cached version will be used (read and/or write).
   * @param cacheSize A hint to use as the cache size in number of records. This must be a positive integer. During
   *                  operation, this size may be exceeded temporarily (until the cache is cleaned).
   */
  public CachedRandomAccessFile(RandomAccessFile file, int cacheSize) {
    if (file == null) {
      throw new IllegalArgumentException("file parameter is null.");
    }

    // create index file
    this.file = file;
    this.cacheSize = cacheSize;
    this.unwrittenRecords = new LinkedBlockingQueue<>();

    // starts the low-priority writer
    this.writer = new Writer();
    writer.start();
  }

  /**
   * Reads the record stored at the specified position in the random access file.
   */
  public E read(long position) throws IOException {
    try {
      // look in cache first
      Record<E> record;
      record = cache.get(position);
      if (record == null) {
        E value;
        synchronized (file) {
          file.seek(position);
          value = (E) new ObjectInputStream(new DataInputStream(Channels.newInputStream(file.getChannel()))).readObject();
        }
        record = new Record<>(value, position);

        // put in cache but without overwriting existing record already there
        // since the cache record might have been written and will be more up-to-date.
        cache(record, false);
      } else {
        // increase usage count
        record.usageCount++;
      }
      return record.value;
    } catch (ClassNotFoundException cnfe) {
      throw new IOException(cnfe);
    }
  }

  /**
   * Writes a record at the specified position.
   */
  public void write(E value, long position) throws IOException {
    // put in write queue
    Record<E> record = new Record<>(value, position);
    record.unwritten = true;

    boolean written = unwrittenRecords.offer(record);
    if (!written) {
      writer.forceWrite();
      do {
        try {
          unwrittenRecords.put(record);
          written = true;
        } catch (InterruptedException ie) {
          // ignore
        }
      }
      while (!written);
    }

    long recordEnd = position + record.bytes().length;
    synchronized (this) {
      fileLength = Math.max(fileLength, recordEnd);
    }

    // cache record overwriting existing record if any
    cache(record, true);
  }

  /**
   * Appends the record.
   */
  public synchronized long append(E record) throws IOException {
    long position = fileLength;
    write(record, position);
    return position;
  }

  /**
   * Cache the record, making room in the cache if necessary.
   */
  private void cache(Record<E> record, boolean overwrite) {
    if (cache.size() >= cacheSize * CLEAN_CACHE_PERCENTAGE / 100) {
      writer.cleanCache();
    }
    synchronized (cache) {
      // put in cache only if overwrite is true or no record were
      // at that position in the cache previously.
      if (overwrite || !cache.containsKey(record.position)) {
        cache.put(record.position, record);
      }
    }
  }

  /**
   * Clears the file.
   */
  public synchronized void clear() {
    try {
      synchronized (cache) {
        // clear cache
        cache.clear();
      }

      // clear unwritten records
      unwrittenRecords.clear();

      // clear file
      file.setLength(0);
      fileLength = 0;
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  @Override
  public void close() {
    shutdown();
  }

  /**
   * Shutdowns the writer thread.
   */
  public void shutdown() {
    shutdown = true;
  }

//    @Override protected void finalize() {
//        shutdown();
//    }

  /**
   * Background low-priority thread for writing records to disk.
   */
  private class Writer extends Thread {
    public Writer() {
      super("Parallel writer for cached random access file");
      setPriority(MIN_PRIORITY);
    }

    @Override
    public void run() {
      List<Record<E>> recordsToWrite = new LinkedList<>();
      while (!shutdown) {
        try {
          // Wait on queue for a record to write
          Record<E> toWrite = unwrittenRecords.take();

          // delay and check if there are more
          sleep(DELAY_BEFORE_WRITE);
          recordsToWrite.clear();
          recordsToWrite.add(toWrite);
          while ((toWrite = unwrittenRecords.poll()) != null) {
            recordsToWrite.add(toWrite);
          }
          write(recordsToWrite);
        } catch (InterruptedException ie) {
          interrupt();
          shutdown = true;
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }

        // start cleaning cache if we are using 3/4 or more of it
        if (cache.size() >= cacheSize * CLEAN_CACHE_PERCENTAGE / 100) {
          cleanCache();
        }
      }
    }

    /**
     * Write all unwritten records to make space for new records.
     */
    public void forceWrite() throws IOException {
      write(unwrittenRecords.size());
    }

    /**
     * Coalesced and write up to the specified number of records.
     */
    private void write(int recordsCountToWrite) throws IOException {
      List<Record<E>> records = new ArrayList<>();
      unwrittenRecords.drainTo(records, recordsCountToWrite);
      write(records);
    }

    private void write(List<Record<E>> records) throws IOException {
      if (!records.isEmpty()) {
        List<Record<E>> recordsToWrite = records.size() > 1 ? coalesce(records) : records;
        for (Record<E> record : recordsToWrite) {
          write(record);
        }

        // flag records as written
        for (Record<E> record : records) {
          record.unwritten = false;
        }
      }
    }

    /**
     * Writes the record to file.
     */
    private void write(Record<E> record) {
      try {
        synchronized (file) {
          file.seek(record.position);
          file.write(record.bytes());
        }
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    }

    /**
     * Called to remove least used records in cache.
     */
    public void cleanCache() {
      // remove all records with usage count < minUsageCountToKeep
      int oldCacheSize = cache.size();
      for (Record<E> record : cache.values()) {
        if (!record.unwritten && record.usageCount < minUsageCountToKeep) {
          synchronized (cache) {
            // double-check is required since changes might have occurred
            // during lock acquisition
            if (!record.unwritten && record.usageCount < minUsageCountToKeep) {
              cache.remove(record.position);
            }
          }
        }
      }

      if (cache.size() > oldCacheSize * PERCENTAGE_DECREASE_IN_CACHE_SIZE_AFTER_CLEANING / 100) {
        // if the cache size is still greater than 90% of the old cache size,
        // be more aggressive next time by removing records with even higher usage counts....
        minUsageCountToKeep++;
      } else if (minUsageCountToKeep > 2) {
        // otherwise be less aggressive next time.
        minUsageCountToKeep--;
      }
    }

    /**
     * Coalesce the bytes of consecutive records to optimize writes.
     */
    private List<Record<E>> coalesce(List<Record<E>> records) throws IOException {
      List<Record<E>> coalesced = new LinkedList<>();
      records.sort(Comparator.comparingLong(Record::position));

      Record<E> lastRecord = null;
      for (Record<E> record : records) {
        if (lastRecord == null) {
          lastRecord = record;
          coalesced.add(record);
        } else {
          if (lastRecord.position + lastRecord.bytes().length == record.position) {
            // this record follow the last: coalesce
            lastRecord.append(record.bytes());
          } else {
            coalesced.add(record);
            lastRecord = record;
          }
        }
      }
      return coalesced;
    }

    /**
     * The minimum usage count of records to keep in the cache. The value of this variable is increased if the cache is
     * not being cleaned sufficiently.
     */
    private int minUsageCountToKeep = 2;
  }

  /**
   * A record with its position in the file.
   */
  private static class Record<E> {
    public Record(E value, long position) {
      this.value = value;
      this.position = position;
    }

    /**
     * Returns the serialized version of the value of this record. This method keep the result of the serialization and
     * it is therefore safe to call multiple times.
     */
    public synchronized byte[] bytes() throws IOException {
      if (bytes == null) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(value);
        bytes = out.toByteArray();
      }
      return bytes;
    }

    /**
     * Append the supplied bytes to the serialized value of this record.
     */
    public void append(byte[] other) throws IOException {
      int length = bytes().length;
      bytes = Arrays.copyOf(bytes, length + other.length);
      System.arraycopy(other, 0, bytes, length, other.length);
    }

    public long position() {
      return position;
    }

    public E value() {
      return value;
    }

    /**
     * The record.
     */
    public final E value;

    /**
     * The position of the record in the file.
     */
    public long position;

    /**
     * The usage count for clearing entries when the cache is full.
     */
    public int usageCount = 1;

    /**
     * unwritten records must not be removed from the cache.
     */
    public boolean unwritten;

    /**
     * The serialized representation of the record value.
     */
    private byte[] bytes;
  }

  /**
   * The random access file holding the index of this collection.
   */
  public final RandomAccessFile file;

  /**
   * The maximum cache size.
   */
  public final int cacheSize;

  /**
   * The writer thread.
   */
  private final Writer writer;

  /**
   * True when this object is being shutdown. The writer thread is stopped when this becomes true.
   */
  private volatile boolean shutdown;

  /**
   * The index cache mapping the position of the index entry in the index file to the index entry. Writes are made to
   * this cache before being replicated to the disk by the writer thread.
   */
  private final Map<Long, Record<E>> cache = new ConcurrentHashMap<>();

  /**
   * The list of unwritten records.
   */
  private final BlockingQueue<Record<E>> unwrittenRecords;

  /**
   * The file length including records waiting to be written.
   */
  private long fileLength;

  /**
   * Time in ms to wait before writing records to disk. In that time new records may be added to the queue and they are
   * all coalesced and written with the minimum number of writes possible. A higher number may help performance but will
   * consume more memory since the queue will grow more.
   */
  private static final long DELAY_BEFORE_WRITE = 2000;

  /**
   * Percentage of the total cache size after which cache cleaning starts. The higher the value, the more optimal use of
   * the cache is made; however, the cache will then surely grow much more than the hinted cache size.
   */
  private static final int CLEAN_CACHE_PERCENTAGE = 75;

  /**
   * The percentage decrease in size expected after cache cleaning. If this is set to a higher number, the cache will be
   * cleaned more aggressively.
   */
  private static final int PERCENTAGE_DECREASE_IN_CACHE_SIZE_AFTER_CLEANING = 10;
}