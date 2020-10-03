package ma.vi.base.crypt;

import ma.vi.base.string.Strings;

import java.util.Map;
import java.util.Random;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

/**
 * Utility functions to generate simple passwords, to obfuscate
 * and unobfuscate them.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class Obfuscator {
  public static String password() {
    return password(5);
  }

  public static String password(int length) {
    return Strings.random(length > 0 ? length : 5, PASSWORD_CHARS);
  }

  /**
   * Each character is shifted by its position in the text (+3) with character at even
   * position shifted to the right and those at odd position shifted to the left. The
   * obfuscated password is also interspersed with random characters. These minimal
   * transformations are designed to protect against basic statistical attacks.
   */
  public static String obfuscate(String unobfuscated) {
    StringBuilder obfuscated = new StringBuilder();
    for (int i = 0; i < unobfuscated.length(); i++) {
      char c = unobfuscated.charAt(i);
      obfuscated.append(PASSWORD_CHARS[random.nextInt(PASSWORD_CHARS.length)]);
      if (PASSWORD_CHARS_MAP.containsKey(c)) {
        int repPos = Math.floorMod(PASSWORD_CHARS_MAP.get(c) + shift(i), PASSWORD_CHARS.length);
        obfuscated.append(PASSWORD_CHARS[repPos]);
      } else {
        obfuscated.append(c);
      }
    }
    return obfuscated.toString();
  }

  public static String unobfuscate(String obfuscated) {
    StringBuilder unobfuscated = new StringBuilder();
    for (int i = 1; i < obfuscated.length(); i += 2) {
      char c = obfuscated.charAt(i);
      if (PASSWORD_CHARS_MAP.containsKey(c)) {
        int repPos = Math.floorMod(PASSWORD_CHARS_MAP.get(c) - shift(i / 2), PASSWORD_CHARS.length);
        unobfuscated.append(PASSWORD_CHARS[repPos]);
      } else {
        unobfuscated.append(c);
      }
    }
    return unobfuscated.toString();
  }

  private static int shift(int i) {
    return (i + 3) * (i % 2 == 0 ? 1 : -1);
  }

  private static final char[] PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

  private static final Map<Character, Integer> PASSWORD_CHARS_MAP =
      range(0, PASSWORD_CHARS.length).boxed()
                                     .collect(toMap(i -> PASSWORD_CHARS[i], identity()));

  private static final Random random = new Random();

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: Obfuscator [-u] <text>");
      System.out.println("Obfuscates the text or if -u option is present, unobfuscates it");
    } else {
      boolean obfuscate = args.length == 1 || !args[0].trim().toLowerCase().equals("-u");
      String text = args[args.length - 1];
      System.out.println(obfuscate ? obfuscate(text) : unobfuscate(text));
    }
  }
}