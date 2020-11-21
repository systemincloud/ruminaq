/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.Collator;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
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

  public static void openFileInDefaultEditor(IFile file) {
    Result.attempt(() -> IDE.openEditor(
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
        file, true));
  }

  public static IProject getWorkspaceProjectFromEObject(EObject eobject) {
    URI uri = getModelPathFromEObject(eobject);

    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    IProject project = null;

    // try to get project from whole uri resource
    IResource resource = workspaceRoot.findMember(uri.toString());
    if (resource != null) {
      project = resource.getProject();
    }

    // another try,by first segment with project name
    if (project == null && uri.segmentCount() > 0) {
      String projectName = uri.segment(0);
      IResource projectResource = workspaceRoot.findMember(projectName);
      if (projectResource != null) {
        project = projectResource.getProject();
      }
    }

    return project;
  }

  public static URI getModelPathFromEObject(EObject eobject) {
    URI uri = EcoreUtil.getURI(eobject);
    uri = uri.trimFragment();
    if (uri.isPlatform()) {
      uri = URI.createURI(uri.toPlatformString(true));
    }
    return uri;
  }

  public static URI removeFristSegments(URI uri, int nb) {
    nb++;
    String[] segs = uri.segments();
    String tmp = "";
    int i = 0;
    for (String s : segs) {
      i++;
      if (i < nb)
        continue;
      if (i > nb)
        tmp += "/";
      tmp += s;
    }
    return URI.createURI(tmp);
  }

  public static String getProjectNameFromDiagram(Diagram diagram) {
    return URI.decode(diagram.eResource().getURI().segment(1));
  }

  public static String getProjectNameFromPe(PictogramElement pe) {
    return URI.decode(pe.eResource().getURI().segment(1));
  }

  public static void setButtonDimensionHint(Button button) {
    Assert.isNotNull(button);
    Object gd = button.getLayoutData();
    if (gd instanceof GridData) {
      ((GridData) gd).widthHint = getButtonWidthHint(button);
      ((GridData) gd).horizontalAlignment = GridData.FILL;
    }
  }

  public static int getButtonWidthHint(Button button) {
    button.setFont(JFaceResources.getDialogFont());
    PixelConverter converter = new PixelConverter(button);
    int widthHint = converter
        .convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
    return Math.max(widthHint,
        button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
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

  /**
   * 
   * @param project
   * @param path
   * @return
   */
  public static Try<CoreException> createFolderWithParents(IProject project,
      String path) {
    return Optional.ofNullable(path).map(p -> p.split("/")).map(Stream::of)
        .orElseGet(Stream::empty)
        .reduce(
            new SimpleEntry<>(Result.<String, CoreException>success(""), ""),
            (parentPath, segment) -> {
              String currentPath = parentPath.getValue() + "/" + segment;
              return Optional.of(parentPath).filter(p -> !p.getKey().isFailed())
                  .map(p -> {
                    Result<String, CoreException> r = Optional.of(currentPath)
                        .map(project::getFolder)
                        .filter(Predicate.not(IFolder::exists))
                        .map(f -> Result.attempt(() -> {
                          f.create(true, true, new NullProgressMonitor());
                          return currentPath;
                        })).orElse(Result.success(""));
                    return new SimpleEntry<Result<String, CoreException>, String>(
                        r, currentPath);
                  }).orElseGet(() -> {
                    parentPath.setValue(currentPath);
                    return parentPath;
                  });
            }, (a, b) -> Optional.of(a).filter(r -> r.getKey().isFailed())
                .orElse(b))
        .getKey();
  }

  public static void createFileInFolder(IProject project, String path,
      String name) throws CoreException {
    IFile file = project.getFile(path + "/" + name);
    if (!file.exists()) {
      file.create(new ByteArrayInputStream(new byte[0]), true,
          new NullProgressMonitor());
    }
  }

  public static Try<CoreException> deleteProjectDirectoryIfExists(
      IProject project, String directoryPath) {
    return Optional.of(project.getFolder(directoryPath))
        .filter(Predicate.not(IFolder::exists))
        .map(f -> Try.check(() -> f.delete(true, new NullProgressMonitor())))
        .orElseGet(Try::success);
  }

  public static IProject getProjectFromSelection(Object obj) {
    String projectName = null;
    if (obj instanceof IProject) {
      projectName = ((IProject) obj).getName();
    } else if (obj instanceof IJavaProject) {
      projectName = ((IJavaProject) obj).getElementName();
    } else if (obj instanceof IResource) {
      projectName = ((IResource) obj).getProject().getName();
    } else if (obj instanceof IPackageFragment) {
      projectName = ((IPackageFragment) obj).getJavaProject().getElementName();
    } else if (obj instanceof IPackageFragmentRoot) {
      projectName = ((IPackageFragmentRoot) obj).getJavaProject()
          .getElementName();
    }
    return Optional.ofNullable(projectName)
        .map(pn -> ResourcesPlugin.getWorkspace().getRoot().getProject(pn))
        .orElse(null);
  }

  public static IProject getProject(Diagram diagram) {
    return ResourcesPlugin.getWorkspace().getRoot()
        .getProject(getProjectNameFromDiagram(diagram));
  }

  public void sortTable(Table table, int sortIndex) {
    TableItem[] items = table.getItems();
    Collator collator = Collator.getInstance(Locale.getDefault());
    for (int i = 1; i < items.length; i++) {
      String value1 = items[i].getText(sortIndex);
      for (int j = 0; j < i; j++) {
        String value2 = items[j].getText(sortIndex);
        if (collator.compare(value1, value2) < 0) {
          String[] values = { items[i].getText(0) };
          items[i].dispose();
          TableItem item = new TableItem(table, SWT.NONE, j);
          item.setText(values);
          items = table.getItems();
          break;
        }
      }
    }
  }

  /**
   * Remove selected rows from given swt Table.
   *
   * @param table swt Table
   */
  public static void removeSelectedRows(Table table) {
    IntStream.of(table.getSelectionIndices()).boxed()
        .sorted(Collections.reverseOrder()).forEach(table::remove);
  }
}
