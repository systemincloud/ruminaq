package org.ruminaq.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 
 * @author Brian Gerstle, Marek Jagielski
 *
 * @param <V>
 * @param <E>
 */
public class Result<V, E extends Throwable> {

  @FunctionalInterface
  public interface CheckedSupplier<V, E extends Throwable> {
    V get() throws E;
  }

  private final V value;

  private final E error;

  private Result(V value, E error) {
    this.value = value;
    this.error = error;
  }

  public static <V, E extends Throwable> Result<V, E> failure(E error) {
    return new Result<>(null, Objects.requireNonNull(error));
  }

  public static <V, E extends Throwable> Result<V, E> success(V value) {
    return new Result<>(Objects.requireNonNull(value), null);
  }

  public static <V, E extends Throwable> Result<V, E> attempt(
      CheckedSupplier<? extends V, ? extends E> p) {
    try {
      return Result.success(p.get());
    } catch (Throwable e) {
      @SuppressWarnings("unchecked")
      E err = (E) e;
      return Result.failure(err);
    }
  }

  public <T> Result<T, E> map(Function<? super V, ? extends T> mapper) {
    return Optional.ofNullable(error).map(e -> Result.<T, E>failure(e))
        .orElseGet(() -> Result.success(mapper.apply(value)));
  }
  
  public V orElse(V orValue) {
    return Optional.ofNullable(value).orElse(orValue);
  }
  
  public V orElseThrow() throws E {
    return Optional.ofNullable(value).orElseThrow(() -> error);
  }
  
}
