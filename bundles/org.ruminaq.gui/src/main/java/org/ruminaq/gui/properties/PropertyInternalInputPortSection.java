/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.eclipse.wizards.task.AbstractCreateUserDefinedTaskPage;
import org.ruminaq.gui.model.diagram.InternalPortShape;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.util.GlobalUtil;
import org.ruminaq.util.NumericUtil;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * PropertySection for InternalInputPort.
 *
 * @author Marek Jagielski
 */
public class PropertyInternalInputPortSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private static final int TWO_ELEMENTS = 2;

  private FormToolkit toolkit;
  private Composite root;

  private Label lblId;
  private Label lblIdValue;

  private Label lblDataType;
  private Label lblDataTypeValue;

  private Label lblAsynchronous;
  private Label lblAsynchronousValue;

  private Label lblGroup;
  private Label lblGroupValue;

  private PreventLost preventLost;

  private Label lblIgnoreLossyCast;
  private Button btnIgnoreLossyCast;

  private QueueSize queueSize;
  private HoldLast holdLast;

  private class CheckboxDefault {
    protected Composite cmp;
  }

  private class PreventLost extends CheckboxDefault {

    private Label lblPreventLost;
    private Button btnPreventLost;
    private Button btnPreventLostDefault;

    private PreventLost() {
      lblPreventLost = toolkit.createLabel(root, "", SWT.NONE);
      cmp = toolkit.createComposite(root, SWT.NONE);
      cmp.setLayout(new GridLayout(TWO_ELEMENTS, false));
      btnPreventLostDefault = new Button(cmp, SWT.CHECK);
      btnPreventLost = new Button(cmp, SWT.CHECK);
    }

    private void initActions() {
      btnPreventLostDefault.addSelectionListener(
          (WidgetSelectedSelectionListener) se -> ModelUtil
              .runModelChange(() -> modelFrom(getSelectedPictogramElement())
                  .ifPresent((InternalInputPort iip) -> {
                    iip.setPreventLostDefault(
                        btnPreventLostDefault.getSelection());
                    btnPreventLost
                        .setEnabled(!btnPreventLostDefault.getSelection());
                    if (btnPreventLostDefault.getSelection()) {
                      iip.setPreventLost(iip.isPreventLostDefault());
                      btnPreventLost.setSelection(iip.isPreventLostDefault());
                    }
                  }),
                  getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                  "Model Update"));
      btnPreventLost.addSelectionListener(
          (WidgetSelectedSelectionListener) se -> ModelUtil.runModelChange(
              () -> modelFrom(getSelectedPictogramElement()).ifPresent(
                  iip -> iip.setPreventLost(btnPreventLost.getSelection())),
              getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              "Model Update"));
    }

    private void initComponents() {
      lblPreventLost.setText("Prevent data lost:");
      btnPreventLostDefault.setText("default");
      btnPreventLost.setText("custom");
    }

    private void refresh(InternalInputPort ip) {
      if (ip.isPreventLostDefault()) {
        btnPreventLostDefault.setSelection(true);
        btnPreventLost.setEnabled(false);
      } else {
        btnPreventLostDefault.setSelection(false);
        btnPreventLost.setSelection(ip.isPreventLost());
        btnPreventLost.setEnabled(true);
      }
    }
  }

  private class QueueSize extends CheckboxDefault {

    private Label lblQueueSize;
    private Text txtQueueSize;
    private Button btnQueueSizeDefault;

    private QueueSize() {
      lblQueueSize = toolkit.createLabel(root, "", SWT.NONE);
      cmp = toolkit.createComposite(root, SWT.NONE);
      cmp.setLayout(new GridLayout(TWO_ELEMENTS, false));
      txtQueueSize = toolkit.createText(cmp, "", SWT.BORDER);
      GridData lytQuequeSize = new GridData(SWT.LEFT, SWT.CENTER, false, false,
          1, 1);
      lytQuequeSize.minimumWidth = 25;
      lytQuequeSize.widthHint = 25;
      txtQueueSize.setLayoutData(lytQuequeSize);
      btnQueueSizeDefault = new Button(cmp, SWT.PUSH);
    }

    private void initActions() {
      txtQueueSize.addFocusListener(new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent event) {
          boolean parse = (NumericUtil
              .isOneDimPositiveInteger(txtQueueSize.getText())
              && Integer.parseInt(txtQueueSize.getText()) != 0)
              || GlobalUtil.isGlobalVariable(txtQueueSize.getText())
              || AbstractCreateUserDefinedTaskPage.INF
                  .equals(txtQueueSize.getText());
          modelFrom(getSelectedPictogramElement())
              .ifPresent((InternalInputPort iip) -> {
                if (parse) {
                  ModelUtil.runModelChange(() -> {
                    iip.setQueueSize(txtQueueSize.getText());
                    btnQueueSizeDefault.setEnabled(
                        !iip.getDefaultQueueSize().equals(iip.getQueueSize()));
                  }, getDiagramContainer().getDiagramBehavior()
                      .getEditingDomain(), "Change queque size");
                } else {
                  MessageDialog.openError(txtQueueSize.getShell(),
                      "Can't edit value", "Don't understant value");
                  txtQueueSize.setText(iip.getQueueSize());
                }
              });
        }
      });
      txtQueueSize.addTraverseListener((TraverseEvent event) -> {
        if (event.detail == SWT.TRAVERSE_RETURN) {
          btnIgnoreLossyCast.setFocus();
        }
      });
      btnQueueSizeDefault.addSelectionListener(
          (WidgetSelectedSelectionListener) se -> modelFrom(
              getSelectedPictogramElement())
                  .ifPresent(iip -> ModelUtil.runModelChange(() -> {
                    iip.setQueueSize(iip.getDefaultQueueSize());
                    PropertyInternalInputPortSection.this.refresh();
                  }, getDiagramContainer().getDiagramBehavior()
                      .getEditingDomain(), "Change console type")));
    }

    private void initComponents() {
      lblQueueSize.setText("Data queue size");
      btnQueueSizeDefault.setText("set to default");
    }

    private void refresh(InternalInputPort ip) {
      txtQueueSize.setText(ip.getQueueSize());
      boolean quequeVisible = !((ip.isAsynchronous()
          || !ip.getTask().isAtomic()) && ip.getTask() instanceof EmbeddedTask);
      lblQueueSize.setVisible(quequeVisible);
      cmp.setVisible(quequeVisible);
      if (quequeVisible) {
        btnQueueSizeDefault
            .setEnabled(!ip.getDefaultQueueSize().equals(ip.getQueueSize()));
      }
    }
  }

  private class HoldLast extends CheckboxDefault {

    private Label lblHoldLast;
    private Button btnHoldLast;
    private Button btnHoldLastDefault;

    private HoldLast() {
      lblHoldLast = toolkit.createLabel(root, "", SWT.NONE);
      cmp = toolkit.createComposite(root, SWT.NONE);
      cmp.setLayout(new GridLayout(TWO_ELEMENTS, false));
      btnHoldLast = new Button(cmp, SWT.CHECK);
      btnHoldLastDefault = new Button(cmp, SWT.PUSH);
    }

    private void initActions() {
      btnHoldLast.addSelectionListener(
          (WidgetSelectedSelectionListener) se -> ModelUtil
              .runModelChange(() -> modelFrom(getSelectedPictogramElement())
                  .ifPresent((InternalInputPort iip) -> {
                    iip.setHoldLast(btnHoldLast.getSelection());
                    btnHoldLastDefault.setEnabled(
                        iip.isDefaultHoldLast() != iip.isHoldLast());
                  }),
                  getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                  "Change hold last"));
      btnHoldLastDefault.addSelectionListener(
          (WidgetSelectedSelectionListener) se -> modelFrom(
              getSelectedPictogramElement())
                  .ifPresent(iip -> ModelUtil.runModelChange(() -> {
                    iip.setHoldLast(iip.isDefaultHoldLast());
                    PropertyInternalInputPortSection.this.refresh();
                  }, getDiagramContainer().getDiagramBehavior()
                      .getEditingDomain(), "Set hold last to default")));
    }

    private void initComponents() {
      lblHoldLast.setText("Hold last data");
      btnHoldLastDefault.setText("set to default");
    }

    private void refresh(InternalInputPort ip) {
      btnHoldLast.setSelection(ip.isHoldLast());
      boolean holdVisible = !(ip.isAsynchronous() || !ip.getTask().isAtomic());
      lblHoldLast.setVisible(holdVisible);
      cmp.setVisible(holdVisible);
      if (holdVisible) {
        btnHoldLastDefault
            .setEnabled(ip.isDefaultHoldLast() != ip.isHoldLast());
      }
    }

  }

  private static Optional<InternalPortShape> shapeFrom(PictogramElement pe) {
    return Optional.ofNullable(pe).filter(InternalPortShape.class::isInstance)
        .map(InternalPortShape.class::cast);
  }

  private static Optional<InternalInputPort> modelFrom(PictogramElement pe) {
    return shapeFrom(pe).map(InternalPortShape::getModelObject)
        .filter(InternalInputPort.class::isInstance)
        .map(InternalInputPort.class::cast);
  }

  /**
   * Layout.
   *
   * <p>
   * Name: <myName>
   * Type of data: <dataType>
   * Asynchronous: <true|false>
   * Group: <None|1|2..>
   * __________________
   * Prevent data lost: [x] | set to default |
   * ~~~~~~~~~~~~~~~~~~
   * Ignore lossy cast: [x]
   * _____ __________________
   * Data queue size: | | | set to default |
   * ~~~~~ ~~~~~~~~~~~~~~~~~~
   * __________________
   * Hold last data: [x] | set to default |
   * ~~~~~~~~~~~~~~~~~~
   * </p>
   */
  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initActions();
    initComponents();
    addStyles();
  }

  private void initLayout(Composite parent) {
    toolkit = new FormToolkit(parent.getDisplay());
    root = toolkit.createComposite(parent, SWT.WRAP);
    root.setLayout(new GridLayout(TWO_ELEMENTS, false));

    lblId = toolkit.createLabel(root, "", SWT.NONE);
    lblIdValue = toolkit.createLabel(root, "", SWT.NONE);

    lblDataType = toolkit.createLabel(root, "", SWT.NONE);
    lblDataTypeValue = toolkit.createLabel(root, "", SWT.NONE);

    lblAsynchronous = toolkit.createLabel(root, "", SWT.NONE);
    lblAsynchronousValue = toolkit.createLabel(root, "", SWT.NONE);

    lblGroup = toolkit.createLabel(root, "", SWT.NONE);
    lblGroupValue = toolkit.createLabel(root, "", SWT.NONE);

    preventLost = new PreventLost();

    lblIgnoreLossyCast = toolkit.createLabel(root, "", SWT.NONE);
    btnIgnoreLossyCast = new Button(root, SWT.CHECK);

    queueSize = new QueueSize();
    holdLast = new HoldLast();
  }

  private void initActions() {
    preventLost.initActions();
    btnIgnoreLossyCast.addSelectionListener(
        (WidgetSelectedSelectionListener) se -> ModelUtil.runModelChange(
            () -> modelFrom(getSelectedPictogramElement()).ifPresent(iip -> iip
                .setIgnoreLossyCast(btnIgnoreLossyCast.getSelection())),
            getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change console type"));

    queueSize.initActions();
    holdLast.initActions();
  }

  private void initComponents() {
    lblId.setText("Name:");
    lblDataType.setText("Type of data:");
    lblAsynchronous.setText("Asynchronus:");
    lblGroup.setText("Group:");
    preventLost.initComponents();
    lblIgnoreLossyCast.setText("Ignore lossy cast");
    queueSize.initComponents();
    holdLast.initComponents();
  }

  private void addStyles() {
    lblDataTypeValue.setFont(JFaceResources.getFontRegistry().getItalic(""));
    lblAsynchronousValue
        .setFont(JFaceResources.getFontRegistry().getItalic(""));
  }

  @Override
  public void refresh() {
    modelFrom(getSelectedPictogramElement())
        .ifPresent((InternalInputPort ip) -> {
          lblIdValue.setText(ip.getId());
          lblDataTypeValue.setText(ip.getDataType().stream()
              .map(DataType::getClass).map(c -> ModelUtil.getName(c, false))
              .collect(Collectors.joining(", ")));

          lblAsynchronousValue.setText(Boolean.toString(ip.isAsynchronous()));
          lblGroupValue.setText(switch (ip.getGroup()) {
            case -1 -> "None";
            default -> Integer.toString(ip.getGroup());
          });
          preventLost.refresh(ip);
          btnIgnoreLossyCast.setSelection(ip.isIgnoreLossyCast());
          queueSize.refresh(ip);
          holdLast.refresh(ip);
          lblDataType.getParent().layout();
        });
  }
}
