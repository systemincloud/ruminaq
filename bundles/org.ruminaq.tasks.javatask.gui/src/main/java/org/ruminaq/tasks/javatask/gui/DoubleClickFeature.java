/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.jdt.core.IJavaElement;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.FeaturePredicate;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.Result;

/**
 * JavaTask DoubleClickFeature.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(DoubleClickFeature.Filter.class)
public class DoubleClickFeature extends AbstractCustomFeature {

  private IType type;

  public static class Filter implements FeaturePredicate<IContext> {
    @Override
    public boolean test(IContext context) {
      return toModel(context).isPresent();
    }
  }

  public DoubleClickFeature(IFeatureProvider fp) {
    super(fp);
  }

  private static Optional<JavaTask> toModel(IContext context) {
    return Optional.of(context).filter(IDoubleClickContext.class::isInstance)
        .map(IDoubleClickContext.class::cast)
        .map(IDoubleClickContext::getPictogramElements).map(Stream::of)
        .orElseGet(Stream::empty).findFirst()
        .filter(RuminaqShape.class::isInstance).map(RuminaqShape.class::cast)
        .map(RuminaqShape::getModelObject).filter(JavaTask.class::isInstance)
        .map(JavaTask.class::cast);
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    return true;
  }

  @Override
  public boolean hasDoneChanges() {
    return false;
  }

  /**
   * Open Java Eclipse Editor if the implementation
   * java class exists.
   */
  @Override
  public void execute(ICustomContext context) {
    SearchParticipant[] participants = new SearchParticipant[] {
        SearchEngine.getDefaultSearchParticipant() };
    IJavaSearchScope scope = SearchEngine
        .createJavaSearchScope(new IJavaElement[] {
            JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(
                EclipseUtil.getProjectNameFromDiagram(getDiagram()))) });
    toModel(context).map(JavaTask::getImplementationPath)
        .filter(Predicate.not(""::equals))
        .map(c -> SearchPattern.createPattern(c, IJavaSearchConstants.TYPE,
            IJavaSearchConstants.TYPE,
            SearchPattern.R_FULL_MATCH | SearchPattern.R_CASE_SENSITIVE))
        .map(p -> Result.attempt(() -> {
          new SearchEngine().search(p, participants, scope,
              new SearchRequestor() {
                @Override
                public void acceptSearchMatch(SearchMatch sm) {
                  type = Optional.of(sm).map(SearchMatch::getElement)
                      .filter(IType.class::isInstance).map(IType.class::cast)
                      .orElse(null);
                }
              }, null);
          return type;
        }).orElse(null)).filter(Objects::nonNull).map(IType::getResource)
        .filter(
            IFile.class::isInstance)
        .map(
            IFile.class::cast)
        .ifPresent(
            f -> Display.getCurrent()
                .asyncExec(
                    () -> Result
                        .attempt(
                            () -> IDE.openEditor(
                                PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getActivePage(),
                                f, true))));
  }
}
