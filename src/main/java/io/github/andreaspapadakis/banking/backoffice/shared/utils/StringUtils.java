package io.github.andreaspapadakis.banking.backoffice.shared.utils;

import org.jspecify.annotations.Nullable;

public final class StringUtils {
  private StringUtils() {}

  public static boolean isNullOrBlank(@Nullable String str) {
    return str == null || str.isBlank();
  }
}
