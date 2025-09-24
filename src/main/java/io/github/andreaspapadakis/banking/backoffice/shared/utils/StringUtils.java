package io.github.andreaspapadakis.banking.backoffice.shared.utils;

public final class StringUtils {
  private StringUtils() {}

  public static boolean isNullOrBlank(String str) {
    return str == null || str.isBlank();
  }
}
