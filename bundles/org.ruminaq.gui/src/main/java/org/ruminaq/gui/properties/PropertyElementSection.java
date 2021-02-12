/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import java.util.function.Predicate;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.gui.features.directediting.DirectEditLabelFeature;
import org.ruminaq.gui.model.diagram.LabelShape;
import org.ruminaq.gui.model.diagram.LabeledRuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.ModelUtil;

/**
 *
 * @author Marek Jagielski
 */
public class PropertyElementSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private String created;
  private Composite parent;

  private CLabel lblId;
  private Text txtId;

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);
    this.parent = parent;
  }

  private void create(Composite parent) {
    initLayout(parent);
    initActions();
    initComponents();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    Composite composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout(2, false));

    lblId = new CLabel(composite, SWT.NONE);

    txtId = new Text(composite, SWT.BORDER);
    txtId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
  }

  private void initActions() {
    txtId.addTraverseListener((TraverseEvent event) -> {
      if (event.detail == SWT.TRAVERSE_RETURN) {
        if (!validate(txtId)) {
          return;
        }

        ModelUtil.runModelChange(() -> {
          Object bo = Graphiti.getLinkService()
              .getBusinessObjectForLinkedPictogramElement(
                  getSelectedPictogramElement());
          if (bo == null)
            return;
          String id = txtId.getText();
          if (id != null) {
            if (bo instanceof BaseElement) {
              BaseElement element = (BaseElement) bo;
              element.setId(id);

              for (EObject o : getSelectedPictogramElement().getLink()
                  .getBusinessObjects()) {
                if (o instanceof ContainerShape
                    && LabelShape.class.isInstance(o)) {
                  UpdateContext context = new UpdateContext((ContainerShape) o);
                  getDiagramTypeProvider().getFeatureProvider()
                      .updateIfPossible(context);
                  break;
                }
              }
            }
          }
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Model Update");
      }
    });
  }

  private boolean validate(Text txt) {
    String id = txt.getText();
    Shell shell = txt.getShell();
    if (id.length() < 1) {
      MessageDialog.openError(shell, "Can not edit value",
          "Please enter any text as element id.");
      return false;
    } else if (id.contains("\n")) {
      MessageDialog.openError(shell, "Can not edit value",
          "Line breakes are not allowed in class names.");
      return false;
    } else if (DirectEditLabelFeature.hasId(getDiagram(),
        getSelectedPictogramElement(), id)) {
      MessageDialog.openError(shell, "Can not edit value",
          "Model has already id " + id + ".");
      return false;
    }

    return true;
  }

  private void initComponents() {
    lblId.setText("Id:");
  }

  @Override
  public void refresh() {
    Optional<LabeledRuminaqShape> shape = Optional
        .of(getSelectedPictogramElement())
        .filter(LabeledRuminaqShape.class::isInstance)
        .map(LabeledRuminaqShape.class::cast);
    Optional<String> id = shape.map(LabeledRuminaqShape::getModelObject)
        .map(BaseElement::getId);
    id = id.filter(Predicate.not(i -> i.equals(created)))
        .flatMap((String i) -> {
          for (Control control : parent.getChildren())
            control.dispose();
          create(parent);
          parent.layout();
          return Optional.of(getSelectedPictogramElement())
              .filter(LabeledRuminaqShape.class::isInstance)
              .map(LabeledRuminaqShape.class::cast)
              .map(LabeledRuminaqShape::getModelObject).map(BaseElement::getId);

        });
    id.ifPresent(i -> this.created = i);
    id.ifPresent(txtId::setText);
  }
}
