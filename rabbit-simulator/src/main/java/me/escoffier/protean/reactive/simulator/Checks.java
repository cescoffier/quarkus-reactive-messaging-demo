package me.escoffier.protean.reactive.simulator;

import java.util.Objects;
import java.util.function.Predicate;

public class Checks {

  private Checks() {
    // Avoid direct instantiation
  }

  public static <T> T checkArgument(T arg, Predicate<T> check) {
    Objects.requireNonNull(arg, "The argument must not be `null`");
    if (! check.test(arg)) {
      throw new IllegalArgumentException("Invalid argument " + arg);
    }
    return arg;
  }

}
