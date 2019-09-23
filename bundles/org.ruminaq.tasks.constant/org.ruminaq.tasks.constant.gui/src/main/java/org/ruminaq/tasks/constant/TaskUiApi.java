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
package org.ruminaq.tasks.constant;

import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.api.ITaskUiApi;

@Component
public class TaskUiApi implements ITaskUiApi {

  private String symbolicName;
  private Version version;

  @Activate
  void activate(Map<String, Object> properties) {
    Bundle b = FrameworkUtil.getBundle(getClass());
    symbolicName = b.getSymbolicName();
    version = b.getVersion();
  }

  @Override
  public String getSymbolicName() {
    return symbolicName;
  }

  @Override
  public Version getVersion() {
    return version;
  }

  @Override
  public IPropertySection createPropertySection(Composite parent,
      PictogramElement pe, TransactionalEditingDomain ed,
      IDiagramTypeProvider dtp) {
    return new PropertySection(parent, pe, ed, dtp);
  }
}
