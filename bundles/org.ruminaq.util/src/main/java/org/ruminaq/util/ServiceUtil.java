package org.ruminaq.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
        return st.waitForService(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static <T> Collection<T> getServices(Class<?> bundleClazz,
      Class<T> clazz) {
    return getServices(bundleClazz, clazz, new ServiceFilterArgs() {
      @Override
      public List<?> getArgs() {
        return Collections.emptyList();
      }
    });
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
        new ServiceFilterArgs() {
          @Override
          public List<?> getArgs() {
            return Collections.emptyList();
          }
        });
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
                .groupingBy(e -> e.getKey().getBundle().getSymbolicName()))
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
        .map((Class<? extends Predicate<ServiceFilterArgs>> f) -> {
          try {
            return f.getConstructor();
          } catch (NoSuchMethodException | SecurityException e) {
            return null;
          }
        }).<Predicate<ServiceFilterArgs>>map(
            (Constructor<? extends Predicate<ServiceFilterArgs>> c) -> {
              try {
                return c.newInstance();
              } catch (InstantiationException | IllegalAccessException
                  | IllegalArgumentException | InvocationTargetException e) {
                return null;
              }
            })
        .orElse((ServiceFilterArgs args) -> true).test(filterArgs);
  }
}
