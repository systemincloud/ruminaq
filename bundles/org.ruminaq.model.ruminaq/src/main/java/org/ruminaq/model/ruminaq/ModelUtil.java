package org.ruminaq.model.ruminaq;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

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
    if (name.endsWith("Impl"))
      name = name.substring(0, name.length() - 4);
    if (spaces) {
      String[] names = name.split("(?=\\p{Lu})");
      if ("".equals(names[0])) {
        String[] tmp = new String[names.length - 1];
        System.arraycopy(names, 1, tmp, 0, names.length - 1);
        names = tmp;
      }
      name = StringUtils.join(names, ' ');
    }
    return name;
  }

  public static boolean areEquals(List<DataType> left, List<DataType> right) {
    if (left.size() != right.size())
      return false;
    if (left.size() == 0)
      return true;

    loop: for (DataType l : left) {
      for (DataType r : right)
        if (l.getClass().equals(r.getClass()))
          continue loop;
      return false;
    }

    loop: for (DataType r : right) {
      for (DataType l : left)
        if (r.getClass().equals(l.getClass()))
          continue loop;
      return false;
    }

    return true;
  }
}
