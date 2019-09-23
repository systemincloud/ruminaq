/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {

  public static Class<?> findClass(Object object, String simpleName) {
    ClassLoader cl = object.getClass().getClassLoader();
    String packageName = object.getClass().getPackage().getName();
    int index;
    while ((index = packageName.lastIndexOf(".")) != -1) {
      String className = packageName + "." + simpleName;
      try {
        return Class.forName(className, true, cl);
      } catch (ClassNotFoundException e) {
      }
      packageName = packageName.substring(0, index);
    }
    return null;

  }

  @SuppressWarnings("unchecked")
  public static <T> T getAnnotationParameter(Annotation annotation,
      String parameterName, Class<T> type) {
    try {
      Method m = annotation.getClass().getMethod(parameterName);
      Object o = m.invoke(annotation);
      if (o.getClass().getName().equals(type.getName()))
        return (T) o;
      else
        throw new RuntimeException("Wrong parameter type. Expected: "
            + type.getName() + " Actual: " + o.getClass().getName());
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(
          "The specified annotation defines no parameter '" + parameterName
              + "'.",
          e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to get '" + parameterName + "' from "
          + annotation.getClass().getName(), e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Unable to get '" + parameterName + "' from "
          + annotation.getClass().getName(), e);
    }
  }

}
