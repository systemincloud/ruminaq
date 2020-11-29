/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Helper class to wrap Errors in streams.
 *
 * @author Brian Gerstle, Marek Jagielski
 *
 * @param <V>
 * @param <E>
 */
public class Result<V, E extends Throwable> extends Try<E> {

  @FunctionalInterface
  public interface CheckedSupplier<V, E extends Throwable> {
    V get() throws E;
  }

  private final V value;

  private Result(V value, E error) {
    super(error);
    this.value = value;
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

  public V orElseGet(Supplier<V> orValue) {
    return Optional.ofNullable(value).orElseGet(orValue);
  }

  public V orElseThrow() throws E {
    return Optional.ofNullable(value).orElseThrow(() -> error);
  }

  public boolean isFailed() {
    return Optional.ofNullable(error).isPresent();
  }

  public E getError() {
    return error;
  }

  public <W extends Throwable> Result<V, W> wrapError(
      Function<E, W> errorWrap) {
    if (isFailed()) {
      return Result.failure(errorWrap.apply(error));
    }
    return Result.success(value);
  }
}
