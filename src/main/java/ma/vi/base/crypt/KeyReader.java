package ma.vi.base.crypt;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public interface KeyReader {
  PrivateKey privateKey();
  PublicKey publicKey();
}
