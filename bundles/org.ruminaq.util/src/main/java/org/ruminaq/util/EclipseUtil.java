/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.util;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.FrameworkUtil;

public final class EclipseUtil {

  private EclipseUtil() {
  }

  public static Optional<InputStream> resourceFromBundle(Class<?> bundleClass,
      String path) {
    return Optional.of(FrameworkUtil.getBundle(bundleClass))
        .map(b -> b.getEntry(path))
        .map(url -> Result.attempt(url::openConnection))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(urlConn -> Result.attempt(urlConn::getInputStream))
        .map(r -> r.orElse(null)).filter(Objects::nonNull);
  }

  public static void setEnabledRecursive(final Composite composite,
      final boolean enabled, List<Control> notChanged) {
    if (composite == null)
      return;

    for (Control c : composite.getChildren()) {
      if (c instanceof Composite)
        setEnabledRecursive((Composite) c, enabled, notChanged);
      else {
        if (c.isEnabled() == enabled)
          notChanged.add(c);
        c.setEnabled(enabled);
      }
    }
    if (composite.isEnabled() == enabled)
      notChanged.add(composite);
    composite.setEnabled(enabled);
  }

  public static void closeAllViews(final Class<? extends ViewPart> viewType) {
    final IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
        .getWorkbenchWindows();

    // for all workbench windows
    for (int w = 0; w < windows.length; w++) {
      final IWorkbenchPage[] pages = windows[w].getPages();

      // for all workbench pages
      // of a given workbench window
      for (int p = 0; p < pages.length; p++) {
        final IWorkbenchPage page = pages[p];
        final IViewReference[] viewRefs = page.getViewReferences();

        // for all view references
        // of a given workbench page
        // of a given workbench window
        for (int v = 0; v < viewRefs.length; v++) {
          final IViewReference viewRef = viewRefs[v];
          final IWorkbenchPart viewPart = viewRef.getPart(false);
          final Class<?> partType = (viewPart != null) ? viewPart.getClass()
              : null;

          if (viewType == null || viewType.equals(partType))
            page.hideView(viewRef);
        }
      }
    }
  }

  public static IResource emfResourceToIResource(Resource resource) {
    URI eUri = resource.getURI();
    if (eUri.isPlatformResource()) {
      String platformString = eUri.toPlatformString(true);
      return ResourcesPlugin.getWorkspace().getRoot()
          .findMember(platformString);
    }
    return null;
  }

  public static Try<CoreException> deleteProjectDirectoryIfExists(
      IProject project, String directoryPath) {
    return Optional.of(project.getFolder(directoryPath))
        .filter(Predicate.not(IFolder::exists))
        .map(f -> Try.check(() -> f.delete(true, new NullProgressMonitor())))
        .orElseGet(Try::success);
  }
}
