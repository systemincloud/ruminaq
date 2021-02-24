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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.eclipse.SelectionNotDefaultListener;
import org.ruminaq.eclipse.wizards.task.AbstractCreateUserDefinedTaskPage;
import org.ruminaq.gui.model.diagram.InputPortShape;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.util.GlobalUtil;
import org.ruminaq.util.NumericUtil;

/**
 * PropertySection for InputPort.
 *
 * @author Marek Jagielski
 */
public class PropertyInputPortSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private Button btnAsync;
  private Button btnHoldLast;
  private Label lblQueueSize;
  private Text txtQueueSize;
  private CLabel lblGroup;
  private Spinner spnGroup;

  private static Optional<InputPortShape> shapeFrom(PictogramElement pe) {
    return Optional.ofNullable(pe).filter(InputPortShape.class::isInstance)
        .map(InputPortShape.class::cast);
  }

  private static Optional<InputPort> modelFrom(PictogramElement pe) {
    return shapeFrom(pe).map(InputPortShape::getModelObject)
        .filter(InputPort.class::isInstance).map(InputPort.class::cast);
  }

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
    initActions();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    Composite root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(2, false));

    btnAsync = new Button(root, SWT.CHECK);
    btnHoldLast = new Button(root, SWT.CHECK);
    lblQueueSize = new Label(root, SWT.NONE);
    txtQueueSize = new Text(root, SWT.BORDER);
    GridData lytQueue = new GridData();
    lytQueue.minimumWidth = 40;
    lytQueue.widthHint = 40;
    txtQueueSize.setLayoutData(lytQueue);

    lblGroup = new CLabel(root, SWT.NONE);
    spnGroup = new Spinner(root, SWT.BORDER);
    GridData lytGroup = new GridData();
    lytGroup.minimumWidth = 80;
    lytGroup.widthHint = 80;
    spnGroup.setLayoutData(lytGroup);
  }

  private void initComponents() {
    btnAsync.setText("Asynchronous");
    btnHoldLast.setText("Hold last");
    lblQueueSize.setText("Queue size:");
    lblGroup.setText("Group:");
    spnGroup.setMinimum(-1);
    spnGroup.setMaximum(Integer.MAX_VALUE);
  }

  private void initActions() {
    btnAsync.addSelectionListener(
        (SelectionNotDefaultListener) (SelectionEvent se) -> ModelUtil
            .runModelChange(() -> modelFrom(getSelectedPictogramElement())
                .ifPresent((InputPort p) -> {
                  p.setAsynchronous(btnAsync.getSelection());
                  UpdateContext context = new UpdateContext(
                      getSelectedPictogramElement());
                  getDiagramTypeProvider().getFeatureProvider()
                      .updateIfPossible(context);
                  btnHoldLast.setEnabled(!btnAsync.getSelection());
                  spnGroup.setEnabled(!btnAsync.getSelection());
                }),
                getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                "Change InputPort async"));
    btnHoldLast.addSelectionListener(
        (SelectionNotDefaultListener) (SelectionEvent se) -> ModelUtil
            .runModelChange(
                () -> modelFrom(getSelectedPictogramElement())
                    .ifPresent(p -> p.setHoldLast(btnHoldLast.getSelection())),
                getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                "Change InputPort hold"));
    txtQueueSize.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        modelFrom(getSelectedPictogramElement()).ifPresent((InputPort ip) -> {
          if (validateQueueSize()) {
            ModelUtil.runModelChange(
                () -> ip.setQueueSize(txtQueueSize.getText()),
                getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                "Change queque size");
          } else {
            MessageDialog.openError(txtQueueSize.getShell(), "Can't edit value",
                "Don't understant value");
            txtQueueSize.setText(ip.getQueueSize());
          }
        });
      }
    });
    txtQueueSize.addTraverseListener((TraverseEvent event) -> {
      if (event.detail == SWT.TRAVERSE_RETURN) {
        btnAsync.setFocus();
      }
    });
    spnGroup.addSelectionListener(
        (SelectionNotDefaultListener) (SelectionEvent se) -> {
          ModelUtil.runModelChange(
              () -> modelFrom(getSelectedPictogramElement())
                  .ifPresent(p -> p.setGroup(spnGroup.getSelection())),
              getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              "Model Update");
          refresh();
        });
  }

  private boolean validateQueueSize() {
    return (NumericUtil.isOneDimPositiveInteger(txtQueueSize.getText())
        && Integer.parseInt(txtQueueSize.getText()) != 0)
        || GlobalUtil.isGlobalVariable(txtQueueSize.getText())
        || AbstractCreateUserDefinedTaskPage.INF.equals(txtQueueSize.getText());
  }

  @Override
  public void refresh() {
    modelFrom(getSelectedPictogramElement()).ifPresent((InputPort p) -> {
      btnAsync.setSelection(p.isAsynchronous());
      btnHoldLast.setSelection(p.isHoldLast());
      txtQueueSize.setText(p.getQueueSize());

      spnGroup.setSelection(p.getGroup());

      btnHoldLast.setEnabled(!btnAsync.getSelection());
      spnGroup.setEnabled(!btnAsync.getSelection());
    });
  }
}
