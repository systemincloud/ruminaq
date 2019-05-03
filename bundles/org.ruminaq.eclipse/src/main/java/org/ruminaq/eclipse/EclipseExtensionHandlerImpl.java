/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.javatuples.Triplet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.eclipse.api.EclipseExtension;
import org.ruminaq.eclipse.api.EclipseExtensionHandler;

@Component(immediate = true)
public class EclipseExtensionHandlerImpl implements EclipseExtensionHandler {

  private Collection<EclipseExtension> extensions;

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  protected void bind(EclipseExtension extension) {
    if (extensions == null) {
      extensions = new ArrayList<>();
    }
    extensions.add(extension);
  }

  protected void unbind(EclipseExtension extension) {
    extensions.remove(extension);
  }

  @Override
  public boolean createProjectWizardPerformFinish(IJavaProject javaProject) {
    return extensions.stream()
        .allMatch(ext -> ext.createProjectWizardPerformFinish(javaProject));
  }

  @Override
  public List<Triplet<String, String, String>> getMavenDependencies() {
    return extensions.stream().map(EclipseExtension::getMavenDependencies)
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public List<IClasspathEntry> createClasspathEntries(
      IJavaProject javaProject) {
    return extensions.stream()
        .map(ext -> ext.createClasspathEntries(javaProject))
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public void initEditor() {
    extensions.forEach(EclipseExtension::initEditor);
  }

}
