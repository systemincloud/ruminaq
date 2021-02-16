/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
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
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.ModelUtil;

/**
 * Base configuration of RuminaqShapes.
 *
 * @author Marek Jagielski
 */
public class PropertyElementSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  protected static final int TWO_COLUMNS = 2;

  private static final String VALIDATION_WINDOW_NAME = "Can not edit value";

  private String created;
  private Composite parent;

  private CLabel lblId;
  private Text txtId;

  private static Optional<RuminaqShape> shapeFrom(PictogramElement pe) {
    return Optional.ofNullable(pe).filter(RuminaqShape.class::isInstance)
        .map(RuminaqShape.class::cast);
  }

  private static Optional<BaseElement> modelFrom(PictogramElement pe) {
    return shapeFrom(pe).map(RuminaqShape::getModelObject);
  }

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
    composite.setLayout(new GridLayout(TWO_COLUMNS, false));

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
        ModelUtil.runModelChange(() -> modelFrom(getSelectedPictogramElement())
            .ifPresent(bo -> Optional.ofNullable(txtId.getText())
                .ifPresent((String id) -> {
                  bo.setId(id);
                  refreshLabel();
                })),
            getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Model Update");
      }
    });
  }

  private void refreshLabel() {
    shapeFrom(getSelectedPictogramElement())
        .filter(LabeledRuminaqShape.class::isInstance)
        .map(LabeledRuminaqShape.class::cast).map(LabeledRuminaqShape::getLabel)
        .ifPresent((LabelShape ls) -> {
          UpdateContext context = new UpdateContext(ls);
          getDiagramTypeProvider().getFeatureProvider()
              .updateIfPossible(context);
        });
  }

  private boolean validate(Text txt) {
    String id = txt.getText();
    Shell shell = txt.getShell();
    if (id.length() < 1) {
      MessageDialog.openError(shell, VALIDATION_WINDOW_NAME,
          "Please enter any text as element id.");
      return false;
    } else if (id.contains("\n")) {
      MessageDialog.openError(shell, VALIDATION_WINDOW_NAME,
          "Line breakes are not allowed in class names.");
      return false;
    } else if (DirectEditLabelFeature.hasId(getDiagram(),
        getSelectedPictogramElement(), id)) {
      MessageDialog.openError(shell, VALIDATION_WINDOW_NAME,
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
          Stream.of(parent.getChildren()).forEach(Control::dispose);
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
