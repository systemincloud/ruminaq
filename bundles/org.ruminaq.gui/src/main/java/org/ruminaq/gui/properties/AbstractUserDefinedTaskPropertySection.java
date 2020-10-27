/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.eclipse.wizards.task.CreateUserDefinedTaskListener;
import org.ruminaq.gui.Messages;
import org.ruminaq.gui.features.update.AbstractUpdateUserDefinedTaskFeature;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.UserDefinedTask;

/**
 * 
 * @author Marek Jagielski
 */
public abstract class AbstractUserDefinedTaskPropertySection
    extends GFPropertySection implements CreateUserDefinedTaskListener {

  protected static final int FOUR_COLUMNS = 4;

  protected CLabel lblImplementation;
  protected Text txtImplementation;
  protected Button btnSelect;
  protected Button btnCreate;

  protected static <T> Optional<T> toModelObject(PictogramElement pe,
      Class<T> type) {
    return Optional.ofNullable(pe).filter(RuminaqShape.class::isInstance)
        .map(RuminaqShape.class::cast).map(RuminaqShape::getModelObject)
        .filter(type::isInstance).map(type::cast);
  }

  protected <T> Optional<T> selectedModelObject(Class<T> type) {
    return toModelObject(getSelectedPictogramElement(), type);
  }

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
    initActions();
  }

  protected void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    Composite root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(FOUR_COLUMNS, false));

    lblImplementation = new CLabel(root, SWT.NONE);
    txtImplementation = new Text(root, SWT.BORDER);
    txtImplementation
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnSelect = new Button(root, SWT.NONE);
    btnCreate = new Button(root, SWT.NONE);
  }

  protected void initComponents() {
    lblImplementation.setText("Implementation:");
    btnSelect.setText("Select");
    btnCreate.setText("Create");
  }

  protected void initActions() {
    txtImplementation.addTraverseListener((TraverseEvent event) -> {
      if (event.detail == SWT.TRAVERSE_RETURN) {
        save();
      }
    });
    btnSelect.addSelectionListener(selectSelectionListener());
    btnCreate.addSelectionListener(createSelectionListener());
  }

  protected abstract SelectionListener selectSelectionListener();

  protected abstract SelectionListener createSelectionListener();

  @Override
  public void refresh() {
    selectedModelObject(UserDefinedTask.class).ifPresent(
        udt -> txtImplementation.setText(udt.getImplementationPath()));
  }

  protected void save() {
    UpdateContext context = new UpdateContext(getSelectedPictogramElement());
    if (Optional
        .ofNullable(getDiagramTypeProvider().getFeatureProvider()
            .getUpdateFeature(context))
        .filter(AbstractUpdateUserDefinedTaskFeature.class::isInstance)
        .map(AbstractUpdateUserDefinedTaskFeature.class::cast)
        .filter(uudt -> uudt.load(txtImplementation.getText())).isPresent()) {
      ModelUtil.runModelChange(() -> {
        selectedModelObject(UserDefinedTask.class).ifPresent(
            udt -> udt.setImplementationPath(txtImplementation.getText()));
        getDiagramTypeProvider().getFeatureProvider().updateIfPossible(context);
      }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
          Messages.userTaskDefinedPropertySectionSetCommand);
    } else {
      MessageDialog.openError(txtImplementation.getShell(), "Can't edit value",
          "Class not found or incorrect.");
    }
  }

  @Override
  public void setImplementation(String resource) {
    txtImplementation.setText(resource);
    ModelUtil.runModelChange(() -> {
      selectedModelObject(UserDefinedTask.class)
          .ifPresent(udt -> udt.setImplementationPath(resource));
      getDiagramTypeProvider().getFeatureProvider()
          .updateIfPossible(new UpdateContext(getSelectedPictogramElement()));
    }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        Messages.userTaskDefinedPropertySectionSetCommand);
  }
}
