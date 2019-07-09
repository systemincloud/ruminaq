/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui.features;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.util.EclipseUtil;

public class DoubleClickFeature extends AbstractCustomFeature {

  private IType type;

  public DoubleClickFeature(IFeatureProvider fp) {
    super(fp);
  }

    @Override public boolean canExecute    (ICustomContext context) { return true; }
  @Override public boolean hasDoneChanges()                       { return false; }

  @Override
  public void execute(ICustomContext context) {
    JavaTask bo = null;
    for(Object o : Graphiti.getLinkService().getAllBusinessObjectsForLinkedPictogramElement(context.getInnerPictogramElement()))
      if(o instanceof JavaTask) { bo = (JavaTask) o; break; }
    if(bo == null) return;
    if(bo.getImplementationClass().equals("")) return;

    IJavaProject project = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(EclipseUtil.getProjectNameFromDiagram(getDiagram())));
    IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { project });
    SearchPattern pattern = SearchPattern.createPattern(bo.getImplementationClass(), IJavaSearchConstants.TYPE, IJavaSearchConstants.TYPE, SearchPattern.R_FULL_MATCH | SearchPattern.R_CASE_SENSITIVE);
    SearchRequestor requestor = new SearchRequestor() {
      @Override public void acceptSearchMatch(SearchMatch sm) throws CoreException {
        type = (IType) sm.getElement();
      }
    };
    SearchEngine searchEngine = new SearchEngine();
    try { searchEngine.search(pattern, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()}, scope, requestor, null);
    } catch (CoreException e) {  }

    if(type == null) return;

    Display.getCurrent().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
          IResource r = type.getResource();
          IDE.openEditor(page, (IFile) r, true);
        } catch (PartInitException e) {
        }
      }
    });
  }
}
