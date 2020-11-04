/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.model.ruminaq;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 *
 * @author Marek Jagielski
 */
public class ModelUtil {

  public static void runModelChange(final Runnable runnable,
      final TransactionalEditingDomain editingDomain, final String label) {

    editingDomain.getCommandStack()
        .execute(new RecordingCommand(editingDomain, label) {
          protected void doExecute() {
            runnable.run();
          }
        });
  }

  public static String getName(Class<? extends EObject> clazz) {
    return getName(clazz, true);
  }

  public static String getName(Class<? extends EObject> clazz, boolean spaces) {
    String name = clazz.getSimpleName();
    if (name.endsWith("Impl")) {
      name = name.substring(0, name.length() - 4);
    }
    if (spaces) {
      String[] names = name.split("(?=\\p{Lu})");
      if ("".equals(names[0])) {
        String[] tmp = new String[names.length - 1];
        System.arraycopy(names, 1, tmp, 0, names.length - 1);
        names = tmp;
      }
      name = Stream.of(names).collect(Collectors.joining(" "));
    }
    return name;
  }

  public static boolean areEquals(Collection<?> left,
      Collection<?> right) {
    if (left.size() != right.size()) {
      return false;
    }

    if (left.size() == 0) {
      return true;
    }

    if (left.stream().map(Object::getClass).anyMatch(
        l -> right.stream().map(Object::getClass).noneMatch(l::equals))) {
      return false;
    }

    if (right.stream().map(Object::getClass).anyMatch(
        r -> left.stream().map(Object::getClass).noneMatch(r::equals))) {
      return false;
    }

    return true;
  }
}
