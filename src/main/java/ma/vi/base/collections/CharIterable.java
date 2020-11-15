package ma.vi.base.collections;

import java.util.Iterator;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class CharIterable implements Iterable<Character> {
  public CharIterable(CharSequence seq) {
    this.seq = seq;
  }

  public static CharIterable of(CharSequence seq) {
    return new CharIterable(seq);
  }

  @Override
  public Iterator<Character> iterator() {
    return new CharIterator(seq);
  }

  private final CharSequence seq;
}
