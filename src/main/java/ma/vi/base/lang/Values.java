package ma.vi.base.lang;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Values {
  /**
   * Return the first non-null object in the parameter list.
   */
  public static <T> T coalesce(T... objs) {
    for (var obj: objs)
      if (obj != null)
        return obj;
    return null;
  }
}
