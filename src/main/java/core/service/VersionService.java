package core.service;

import java.util.regex.Pattern;

public class VersionService {

  // see https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string
  private static final String SEMVER_PATTERN_TEMPLATE = "^%s(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";

  private static final Pattern moduleSemVerPattern = Pattern.compile(String.format(SEMVER_PATTERN_TEMPLATE, "(v?)"));
  private static final Pattern providerSemVerPattern = Pattern.compile(String.format(SEMVER_PATTERN_TEMPLATE, "v"));

  public static boolean isValidModuleVersion(String version) {
    return moduleSemVerPattern.matcher(version).matches();
  }

  public static boolean isValidProviderVersion(String version) {
    return providerSemVerPattern.matcher(version).matches();
  }
}
