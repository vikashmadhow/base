package ma.vi.base.collections;

import ma.vi.base.cache.CachedRandomAccessFile;
import ma.vi.base.tuple.T1;
import ma.vi.base.tuple.T2;

import java.io.*;
import java.util.*;

/**
 * A disk-based ordered collections of objects.
 * This collection does not support null values.
 *
 * @author vikash.madhow@gmail.com
 */
public class DiskBasedCollection<E extends Serializable> extends AbstractCollection<E>
                                                      implements Serializable, AutoCloseable {
  /**
   * Creates a new disk-based collection with the specified comparator.
   * All temporary files are created in the supplied temporary directory.
   * If this null, the default temporary directory is used.
   */
  public DiskBasedCollection(Comparator<E> comparator, String tempDirectory) {
    if (comparator == null) {
      throw new IllegalArgumentException("comparator parameter is null.");
    }
    this.comparator = comparator;

    try {
      // get temporary directory
      File tmpDir = new File(tempDirectory == null ? System.getProperty("java.io.tmpdir") : tempDirectory);

      // index file
      indexFile = File.createTempFile("dbc", ".idx", tmpDir);
      indexFile.deleteOnExit();
      index = new CachedRandomAccessFile<>(new RandomAccessFile(indexFile, "rw"), CACHE_SIZE);

      // contents file
      contentsFile = File.createTempFile("dbc", ".dat", tmpDir);
      contentsFile.deleteOnExit();
      contents = new CachedRandomAccessFile<>(new RandomAccessFile(contentsFile, "rw"), CACHE_SIZE);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  public DiskBasedCollection(Comparator<E> comparator) {
    this(comparator, null);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean add(E object) {
    if (object == null) {
      throw new IllegalArgumentException("parameter object is null.");
    }
    try {
      insert(size == 0 ? -1 : 0, object);
      size++;
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    return true;
  }

  /**
   * Insert the object given the position of the root. If this is the first
   * object being inserted, the root position will be -1.
   */
  private long insert(long rootIndexPosition, E object) throws IOException {
    if (rootIndexPosition == -1) {
      long objectPosition = contents.append(object);
      IndexEntry entry = new IndexEntry(objectPosition, -1, -1, 1);
      return index.append(entry);
    } else {
      IndexEntry root = indexAt(rootIndexPosition);

      // determine whether to write in left or right node based on whether the
      // object is less or greater than the object at the root, respectively
      E rootObject = contents.read(root.objectPosition);
      int comparison = comparator.compare(object, rootObject);
      int nodeIndex = comparison < 0 ? LEFT : RIGHT;

      // insert
      long oldPosition = root.nodes[nodeIndex];
      root.nodes[nodeIndex] = insert(root.nodes[nodeIndex], object);
      if (root.nodes[nodeIndex] != oldPosition) {
        index.write(root, root.indexPosition);
      }

      root = skew(root);
      root = split(root);
      return root.indexPosition;
    }
  }

  /**
   * AA tree skew operation for self-balancing which right-rotates all nodes having
   * the same level as its left child. This is repeated for the right children of the
   * node all the way down to a leaf.
   */
  private IndexEntry skew(IndexEntry root) throws IOException {
    long pivotPosition = root.nodes[LEFT];
    if (pivotPosition != -1) {
      IndexEntry pivot = indexAt(pivotPosition);
      if (pivot.level == root.level) {
        // swap position of root and pivot
        long position = root.indexPosition;
        root.indexPosition = pivot.indexPosition;
        pivot.indexPosition = position;

        // rotate right
        root.nodes[LEFT] = pivot.nodes[RIGHT];
        pivot.nodes[RIGHT] = root.indexPosition;

        index.write(pivot, pivot.indexPosition);
        index.write(root, root.indexPosition);

        root = pivot;
      }
    }

    long rightNodePosition = root.nodes[RIGHT];
    if (rightNodePosition != -1) {
      IndexEntry rightNode = indexAt(rightNodePosition);
      root.nodes[RIGHT] = skew(rightNode).indexPosition;
      if (root.nodes[RIGHT] != rightNodePosition) {
        index.write(root, root.indexPosition);
      }
    }
    return root;
  }

  /**
   * AA tree split operation for self-balancing which left rotates all nodes having
   * the same level as its right child and grandchild and increases the level of the
   * new root. This is repeated for the right children all the way down.
   */
  private IndexEntry split(IndexEntry root) throws IOException {
    long pivotPosition = root.nodes[RIGHT];
    if (pivotPosition != -1) {
      IndexEntry pivot = indexAt(pivotPosition);
      if (pivot.level == root.level) {
        long grandChildPosition = pivot.nodes[RIGHT];
        if (grandChildPosition != -1) {
          IndexEntry grandChild = indexAt(grandChildPosition);
          if (grandChild.level == pivot.level) {
            // swap position of root and pivot
            long position = root.indexPosition;
            root.indexPosition = pivot.indexPosition;
            pivot.indexPosition = position;

            // rotate left and increase level of new root
            root.nodes[RIGHT] = pivot.nodes[LEFT];
            pivot.nodes[LEFT] = root.indexPosition;
            pivot.level++;                              // pivot is the new root

            index.write(pivot, pivot.indexPosition);
            index.write(root, root.indexPosition);

            root = pivot;
            long rightNodePosition = root.nodes[RIGHT];
            if (rightNodePosition != -1) {
              IndexEntry rightNode = indexAt(rightNodePosition);
              root.nodes[RIGHT] = split(rightNode).indexPosition;
              if (root.nodes[RIGHT] != rightNodePosition) {
                index.write(root, root.indexPosition);
              }
            }
          }
        }
      }
    }
    return root;
  }

  @Override
  public boolean contains(Object object) {
    return indexEntryForObject((E)object) != null;
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        if (toVisit == null) {
          toVisit = new LinkedList<>();
          if (size > 0) {
            toVisit.add(T2.of(0L, Boolean.FALSE));
          }
        }
        return !toVisit.isEmpty();
      }

      @Override
      public E next() {
        if (!hasNext()) {
          throw new NoSuchElementException("No more elements");
        }
        try {
          while (!toVisit.isEmpty()) {
            T2<Long, Boolean> visiting = toVisit.getLast();
            IndexEntry indexEntry = indexAt(visiting.a);
            if (visiting.b == Boolean.FALSE) {
              // LEFT side has not been visited; push left node
              visiting.b = Boolean.TRUE;
              if (indexEntry.nodes[LEFT] >= 0) {
                // push left node
                toVisit.addLast(T2.of(indexEntry.nodes[LEFT], Boolean.FALSE));
              }
            } else {
              // RIGHT side has not been visited; remove before we visit right
              // side so that we do not encounter this node again
              toVisit.removeLast();
              if (indexEntry.nodes[RIGHT] >= 0) {
                // push right node
                toVisit.addLast(T2.of(indexEntry.nodes[RIGHT], Boolean.FALSE));
              }

              // before visiting right return this value
              return lastObjectReturned = contents.read(indexEntry.objectPosition);
            }
          }
          throw new NoSuchElementException("No more elements");
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }
      }

      @Override
      public void remove() {
        if (lastObjectReturned == null) {
          throw new IllegalStateException("No object has been returned from this iterator to remove.");
        }
        DiskBasedCollection.this.remove(lastObjectReturned);
      }

      /** Index positions to visit and whether their left side has been visited or not. */
      private Deque<T2<Long, Boolean>> toVisit;

      /** Last object returned: used for removal. */
      private E lastObjectReturned;
    };
  }

  @Override
  public boolean remove(Object object) {
    if (object == null) {
      throw new IllegalArgumentException("object parameter is null.");
    }
    try {
      T1<Boolean> removed = T1.of(Boolean.FALSE);
      internalRemove(null, (E)object, removed);
      return removed.a;
    } catch (IOException ioe) {
      throw new RuntimeException("Could not delete an object", ioe);
    }
  }

  private long internalRemove(IndexEntry root, E data, T1<Boolean> removed) throws IOException {
    if (root != null) {
      E object = contents.read(root.objectPosition);
      if (data == null ? data == object : data.equals(object)) {
        removed.a = Boolean.TRUE;
        if (root.nodes[LEFT] != -1 && root.nodes[RIGHT] != -1) {
          // load left node of root
          IndexEntry rootLeftNode = indexAt(root.nodes[LEFT]);

          // The heir of the root is the bottom-most right-node
          // starting from the left-node of the root
          IndexEntry heir = rootLeftNode;
          while (heir.nodes[RIGHT] != -1) {
            heir = indexAt(root.nodes[RIGHT]);
          }

          // makes the root index point to the heir's object
          root.objectPosition = heir.objectPosition;

          object = contents.read(root.objectPosition);
          root.nodes[LEFT] = internalRemove(rootLeftNode, object, removed);
        } else if (root.nodes[LEFT] != -1) {
          // left child only: set root to that child
          root = indexAt(root.nodes[LEFT]);
        } else if (root.nodes[RIGHT] != -1) {
          // right child only: set root to that child
          root = indexAt(root.nodes[RIGHT]);
        } else {
          // no child
          root = null;
        }
      } else {
        int comparison = comparator.compare(data, object);
        int dir = comparison < 0 ? LEFT : RIGHT;

        IndexEntry childNode = indexAt(root.nodes[dir]);
        root.nodes[dir] = internalRemove(childNode, data, removed);
      }
    }

    if (root != null) {
      IndexEntry leftChild = indexAt(root.nodes[LEFT]);
      IndexEntry rightChild = indexAt(root.nodes[RIGHT]);

      if ((leftChild != null && leftChild.level < root.level - 1) ||
          (rightChild != null && rightChild.level < root.level - 1)) {
        if (rightChild != null && rightChild.level > --root.level) {
          rightChild.level = root.level;
          index.write(rightChild, rightChild.indexPosition);
        }

        root = skew(root);
        root = split(root);
      }
      return root.indexPosition;
    } else {
      return -1;
    }
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean removed = false;
    for (Object object: c) {
      removed |= remove((E)object);
    }
    return removed;
  }

  @Override
  public synchronized void clear() {
    size = 0;
    index.clear();
    contents.clear();
  }

  /**
   * Closes and deletes the index and contents files.
   */
  @Override
  public void close() {
    index.shutdown();
    contents.shutdown();

    indexFile.delete();
    contentsFile.delete();
  }

//  @Override
//  protected void finalize() throws Throwable {
//    super.finalize();
//    close();
//  }

  /**
   * Returns the index entry for the first object in the collection matching the
   * passed object together with its parent, if any. Otherwise returns null.
   */
  private T2<IndexEntry, IndexEntry> indexEntryForObject(E searchObject) {
    if (searchObject == null) {
      throw new IllegalArgumentException("object parameter is null.");
    }
    if (size == 0) {
      return null;
    } else {
      try {
        IndexEntry ancestor = null;
        long indexPos = 0;
        while (indexPos >= 0) {
          IndexEntry entry = indexAt(indexPos);
          E readObject = contents.read(entry.objectPosition);

          int comparison = comparator.compare(searchObject, readObject);
          if (comparison == 0) {
            // found
            IndexEntry found = new IndexEntry(entry);
            found.indexPosition = indexPos;
            return T2.of(found, ancestor);
          } else {
            ancestor = new IndexEntry(entry);
            ancestor.indexPosition = indexPos;

            if (comparison > 0) {
              // search object is greater than object on file; move to right node
              indexPos = entry.nodes[RIGHT];
            } else {
              // search object is less than object on file; move to left node
              indexPos = entry.nodes[LEFT];
            }
          }
        }
        return null;
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    }
  }

  /**
   * Reads the index at the specified position or return null if the position
   * is negative.
   */
  private IndexEntry indexAt(long position) throws IOException {
    if (position < 0) {
      return null;
    } else {
      IndexEntry entry = index.read(position);
      entry.indexPosition = position;
      return entry;
    }
  }

  /**
   * An index entry.
   */
  private static class IndexEntry implements Externalizable {
    /**
     * No-args constructor for deserialization.
     */
    public IndexEntry() {
    }

    public IndexEntry(IndexEntry other) {
      // copy
      if (other != null) {
        this.objectPosition = other.objectPosition;
        this.nodes[LEFT] = other.nodes[LEFT];
        this.nodes[RIGHT] = other.nodes[RIGHT];
        this.level = other.level;
      }
    }

    public IndexEntry(long objectPosition, long leftNode, long rightNode, int level) {
      this.objectPosition = objectPosition;
      this.nodes[LEFT] = leftNode;
      this.nodes[RIGHT] = rightNode;
      this.level = level;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      objectPosition = in.readLong();
      nodes[0] = in.readLong();
      nodes[1] = in.readLong();
      level = in.readInt();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      out.writeLong(objectPosition);
      out.writeLong(nodes[0]);
      out.writeLong(nodes[1]);
      out.writeInt(level);
    }

    /**
     * position of record in contents file.
     */
    public long objectPosition;

    /**
     * Level of the node in the AA tree: used for self-balancing.
     */
    public int level;

    /**
     * The left and right children nodes. The left and right nodes are in
     * subscripts 0 and 1, respectively.
     */
    public final long[] nodes = new long[2];

    /**
     * The position of the index entry in the index file. This is not saved in the index entry.
     */
    public transient long indexPosition;
  }

  /**
   * Position of the left child node in the nodes array.
   */
  public static transient final int LEFT = 0;

  /**
   * Position of the right child node in the nodes array.
   */
  public static transient final int RIGHT = 1;

  /**
   * Comparator for comparing elements of this collection.
   */
  private final Comparator<E> comparator;

  /**
   * The index file.
   */
  private final File indexFile;

  /**
   * The random access file holding the index of this collection.
   */
  private final CachedRandomAccessFile<IndexEntry> index;

  /**
   * The contents file.
   */
  private final File contentsFile;

  /**
   * The random access file holding the contents of this collection.
   */
  private final CachedRandomAccessFile<E> contents;

  /**
   * The size of the collection.
   */
  private int size;

  /**
   * The maximum cache size. When the cache size reached 75% of this size,
   * a low-priority cleaning thread starts to actively clean entries with
   * the lowest usage count. If the cache reached the maximum size, 25% of
   * its least used entries are removed.
   */
  private static final int CACHE_SIZE = 2000;
}