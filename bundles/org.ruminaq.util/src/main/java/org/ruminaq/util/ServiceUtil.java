package org.ruminaq.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class ServiceUtil {

  public static <T> T getService(Class<T> clazz) {
    Bundle bundle = FrameworkUtil.getBundle(ServiceUtil.class);
    if (bundle != null) {
      ServiceTracker<T, T> st = new ServiceTracker<>(bundle.getBundleContext(), clazz, null);
      st.open();
      try {
        return st.waitForService(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static <T> Collection<T> getServices(Class<?> bundleClazz, Class<T> clazz) {
    Bundle bundle = FrameworkUtil.getBundle(bundleClazz);
    if (bundle != null) {
      ServiceTracker<T, T> st = new ServiceTracker<>(bundle.getBundleContext(), clazz, null);
      st.open();
      List<T> ret = new LinkedList<T>();
      ret.addAll(Stream.of(st.getServiceReferences())
          .<T>map(st::getService)
          .collect(Collectors.toList()));
      return ret;
    }
    return Collections.emptyList();
  }

  public static <T> Collection<T> getServicesAtLatestVersion(Class<?> bundleClazz, Class<T> clazz) {
    Bundle bundle = FrameworkUtil.getBundle(bundleClazz);
    if (bundle != null) {
      ServiceTracker<T, T> st = new ServiceTracker<>(bundle.getBundleContext(), clazz, null);
      st.open();
      List<T> ret = new LinkedList<>();
      ret.addAll(Stream.of(st.getServiceReferences())
          .<SimpleEntry<ServiceReference<T>, T>>map(r -> new SimpleEntry<ServiceReference<T>, T>(r, st.getService(r)))
          .collect(Collectors.groupingBy(e -> e.getKey().getBundle().getSymbolicName()))
          .entrySet()
          .stream()
          .map(e -> e.getValue()
              .stream()
              .max(Comparator.comparing(r -> r.getKey().getBundle().getVersion()))
              .get()
              .getValue())
          .collect(Collectors.toList()));
      return ret;
    }
    return Collections.emptyList();
  }
}
