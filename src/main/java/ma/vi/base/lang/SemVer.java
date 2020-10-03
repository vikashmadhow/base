/*
 * Copyright (c) 2016 Vikash Madhow
 */

package ma.vi.base.lang;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Integer.parseInt;

/**
 * <a href="http://semver.org/">Semantic Versioning</a> class with support
 * for pre-release extension. This class is immutable with all operations
 * requiring mutation returning a new instance.
 *
 * @author Vikash Madhow (vikash.madhow@gmail.com)
 */
public class SemVer implements Comparable<SemVer> {
  public SemVer(long major, long minor, long patch) {
    this(major, minor, patch, null);
  }

  public SemVer(long major, long minor, long patch, String preRelease) {
    checkArgument(major >= 0 && minor >= 0 && patch >= 0,
        "Major, minor and patch version cannot be negative");
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.preRelease = preRelease;
  }

  public static SemVer version(String version) {
    Matcher matcher = SEMVER.matcher(version);
    checkArgument(matcher.find(), version + " is not a valid semantic version");

    int major = parseInt(matcher.group(1).trim());
    int minor = parseInt(matcher.group(2).trim());
    int patch = parseInt(matcher.group(3).trim());

    String preRelease = matcher.group(5);
    return new SemVer(major, minor, patch, preRelease);
  }

  public static boolean valid(String version) {
    return SEMVER.matcher(version).matches();
  }

  public SemVer incMajor() {
    return new SemVer(major + 1, minor, patch, preRelease);
  }

  public SemVer incMinor() {
    return new SemVer(major, minor + 1, patch, preRelease);
  }

  public SemVer incPatch() {
    return new SemVer(major, minor, patch + 1, preRelease);
  }

  public SemVer major() {
    return new SemVer(major + 1, 0, 0);
  }

  public SemVer minor() {
    return new SemVer(major, minor + 1, 0);
  }

  public SemVer patch() {
    return new SemVer(major, minor, patch + 1);
  }

  @Override
  public int compareTo(SemVer other) {
    if (major != other.major) {
      return Long.compare(major, other.major);
    } else if (minor != other.minor) {
      return Long.compare(minor, other.minor);
    } else if (patch != other.patch) {
      return Long.compare(patch, other.patch);
    } else if (preRelease == null &&
        other.preRelease == null) {
      return 0;
    } else if (preRelease == null) {
      return 1;
    } else if (other.preRelease == null) {
      return -1;
    } else {
      return preRelease.compareTo(other.preRelease);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SemVer semVer = (SemVer) o;

    if (major != semVer.major) return false;
    if (minor != semVer.minor) return false;
    if (patch != semVer.patch) return false;
    return Objects.equals(preRelease, semVer.preRelease);
  }

  @Override
  public int hashCode() {
    int result = (int) (major ^ (major >>> 32));
    result = 31 * result + (int) (minor ^ (minor >>> 32));
    result = 31 * result + (int) (patch ^ (patch >>> 32));
    result = 31 * result + (preRelease != null ? preRelease.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return major + "." + minor + "." + patch + (preRelease == null ? "" : '-' + preRelease);
  }

  public final long major;
  public final long minor;
  public final long patch;
  public final String preRelease;

  public static final Pattern SEMVER = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(-(\\S+))?");
}