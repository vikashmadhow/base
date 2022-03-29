package ma.vi.base.crypt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Hash {
  @Test
  public void sha256() {
    assertEquals("1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014", Hashing.sha256("test1"));
    assertEquals("60303ae22b998861bce3b28f33eec1be758a213c86c93c076dbe9f558c11c752", Hashing.sha256("test2"));
  }
}
