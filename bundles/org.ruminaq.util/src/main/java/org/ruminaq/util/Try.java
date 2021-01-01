/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Helper class to wrap Errors in streams.
 *
 * @author Brian Gerstle, Marek Jagielski
 *
 * @param <E>
 */
public class Try<E extends Throwable> {

  @FunctionalInterface
  public interface CheckedConsumer<E extends Throwable> {
    void accept() throws E;
  }

  protected final E error;

  protected Try(E error) {
    this.error = error;
  }

  public static <E extends Throwable> Try<E> crash(E error) {
    return new Try<>(Objects.requireNonNull(error));
  }

  public static <E extends Throwable> Try<E> success() {
    return new Try<>(null);
  }

  public static <E extends Throwable> Try<E> check(
      CheckedConsumer<? extends E> p) {
    try {
      p.accept();
    } catch (Throwable e) {
      @SuppressWarnings("unchecked")
      E err = (E) e;
      return Try.crash(err);
    }

    return Try.success();
  }

  public void reThrow() throws E {
    if (Optional.ofNullable(error).isPresent()) {
      throw error;
    }
  }

  public boolean isFailed() {
    return Optional.ofNullable(error).isPresent();
  }

  public void ifSuccessed(Runnable runnable) {
    if (!isFailed()) {
      runnable.run();
    }
  }

  public E getError() {
    return error;
  }

  public <W extends Throwable> Try<W> wrapError(Function<E, W> errorWrap) {
    if (isFailed()) {
      return Try.crash(errorWrap.apply(error));
    }
    return Try.success();
  }
}
