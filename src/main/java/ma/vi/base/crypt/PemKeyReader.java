package ma.vi.base.crypt;

import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class PemKeyReader implements KeyReader {
  public PemKeyReader(Reader reader, String algorithm) {
    try (PemReader pemReader = new PemReader(reader)) {
      pemContent = pemReader.readPemObject().getContent();
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    this.algorithm = algorithm;
  }

  @Override
  public PublicKey publicKey() {
    try {
      KeyFactory kf = KeyFactory.getInstance(algorithm);
      EncodedKeySpec keySpec = new X509EncodedKeySpec(pemContent);
      return kf.generatePublic(keySpec);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Could not reconstruct the public key, the given algorithm could not be found.", e);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException("Could not reconstruct the public key.", e);
    }
  }

  @Override
  public PrivateKey privateKey() {
    try {
      KeyFactory kf = KeyFactory.getInstance(algorithm);
      EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemContent);
      return kf.generatePrivate(keySpec);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Could not reconstruct the private key, the given algorithm could not be found.", e);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException("Could not reconstruct the private key.", e);
    }
  }

  private final byte[] pemContent;
  private final String algorithm;
}