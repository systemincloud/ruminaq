/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public final class ServiceUtil {

  private static final String SERVICE_RANKING_PROPERTY = "service.ranking";

  private ServiceUtil() {
  }

  public static <T> T getService(Class<T> clazz) {
    Bundle bundle = FrameworkUtil.getBundle(ServiceUtil.class);
    if (bundle != null) {
      ServiceTracker<T, T> st = new ServiceTracker<>(bundle.getBundleContext(),
          clazz, null);
      st.open();
      try {
        return st.waitForService(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static <T> Collection<T> getServices(Class<?> bundleClazz,
      Class<T> clazz) {
    return getServices(bundleClazz, clazz, () -> Collections.emptyList());
  }

  public static <T> Collection<T> getServices(Class<?> bundleClazz,
      Class<T> clazz, ServiceFilterArgs filterArgs) {
    Bundle bundle = FrameworkUtil.getBundle(bundleClazz);
    if (bundle != null) {
      ServiceTracker<T, T> st = new ServiceTracker<>(bundle.getBundleContext(),
          clazz, null);
      st.open();
      ServiceReference<T>[] srs = st.getServiceReferences();
      if (srs != null) {
        return Stream.of(srs)
            .sorted(((s1, s2) -> Integer.compare(
                (Integer) s2.getProperty(SERVICE_RANKING_PROPERTY),
                (Integer) s1.getProperty(SERVICE_RANKING_PROPERTY))))
            .<T>map(st::getService).filter(filter(filterArgs))
            .collect(Collectors.<T>toList());
      }
    }
    return Collections.emptyList();
  }

  public static <T> Collection<T> getServicesAtLatestVersion(
      Class<?> bundleClazz, Class<T> clazz) {
    return getServicesAtLatestVersion(bundleClazz, clazz,
        () -> Collections.emptyList());
  }

  public static <T> Collection<T> getServicesAtLatestVersion(
      Class<?> bundleClazz, Class<T> clazz, ServiceFilterArgs filterArgs) {
    Bundle bundle = FrameworkUtil.getBundle(bundleClazz);
    if (bundle != null) {
      ServiceTracker<T, T> st = new ServiceTracker<>(bundle.getBundleContext(),
          clazz, null);
      st.open();
      ServiceReference<T>[] srs = st.getServiceReferences();
      if (srs != null) {
        return Stream.of(srs).<SimpleEntry<ServiceReference<T>, T>>map(
            r -> new SimpleEntry<ServiceReference<T>, T>(r, st.getService(r)))
            .collect(Collectors
                .groupingBy(e -> e.getKey().getBundle().getSymbolicName()
                    + e.getValue().getClass().getSimpleName()))
            .entrySet().stream()
            .map(e -> e.getValue().stream()
                .max(Comparator
                    .comparing(r -> r.getKey().getBundle().getVersion()))
                .get())
            .sorted(
                (s1, s2) -> Integer.compare(
                    Optional
                        .ofNullable((Integer) s2.getKey()
                            .getProperty(SERVICE_RANKING_PROPERTY))
                        .orElse(Integer.MAX_VALUE),
                    Optional
                        .ofNullable((Integer) s1.getKey()
                            .getProperty(SERVICE_RANKING_PROPERTY))
                        .orElse(Integer.MAX_VALUE)))
            .map(SimpleEntry::getValue).filter(filter(filterArgs))
            .collect(Collectors.toList());
      }
    }

    return Collections.emptyList();
  }

  private static <T> Predicate<T> filter(ServiceFilterArgs filterArgs) {
    return (T s) -> Optional
        .ofNullable(s.getClass().getAnnotation(ServiceFilter.class))
        .map(ServiceFilter::value)
        .map(f -> Result.attempt(f::getDeclaredConstructor))
        .map(r -> r.peek(v -> v.setAccessible(true)))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .map(f -> Result.attempt(f::newInstance))
        .flatMap(r -> Optional.ofNullable(r.orElse(null)))
        .map(f -> f.test(filterArgs)).orElse(Boolean.TRUE);
  }
}
