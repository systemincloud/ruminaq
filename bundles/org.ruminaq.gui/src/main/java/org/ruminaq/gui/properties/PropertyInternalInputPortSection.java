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
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.MainTask;
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
  private static final int QUEUE_TXT_WIDTH = 25;

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

  private class LabelComposite {
    protected Label lbl;
    protected Composite cmp;

    private LabelComposite() {
      lbl = toolkit.createLabel(root, "", SWT.NONE);
      cmp = toolkit.createComposite(root, SWT.NONE);
      cmp.setLayout(new GridLayout(TWO_ELEMENTS, false));
    }
  }

  private abstract class AbstractDefault extends LabelComposite {

    protected Button btnDefault;

    protected void initComponents() {
      btnDefault.setText("set to default");
    }

    protected void initActions() {
      btnDefault.addSelectionListener(
          (WidgetSelectedSelectionListener) se -> ModelUtil.runModelChange(
              () -> modelFrom(getSelectedPictogramElement())
                  .ifPresent(this::btnDefaultAction),
              getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              btnDefaultActionMessage()));
    }

    protected abstract void btnDefaultAction(InternalInputPort iip);

    protected abstract String btnDefaultActionMessage();
  }

  private abstract class AbstractCheckboxDefault extends AbstractDefault {
    protected Button btnCheck;

    private AbstractCheckboxDefault() {
      btnCheck = new Button(cmp, SWT.CHECK);
      btnDefault = new Button(cmp, SWT.PUSH);
    }

    @Override
    protected void initActions() {
      super.initActions();
      btnCheck.addSelectionListener(
          (WidgetSelectedSelectionListener) se -> ModelUtil.runModelChange(
              () -> modelFrom(getSelectedPictogramElement())
                  .ifPresent(this::btnCheckAction),
              getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              btnCheckActionMessage()));
    }

    protected abstract void btnCheckAction(InternalInputPort iip);

    protected abstract String btnCheckActionMessage();
  }

  private final class PreventLost extends AbstractCheckboxDefault {

    @Override
    protected void btnCheckAction(InternalInputPort iip) {
      iip.setPreventLost(btnCheck.getSelection());
      iip.setPreventLostDefault(
          btnCheck.getSelection() == diagramDefaultPreventLost());
      refresh(iip);
    }

    @Override
    protected String btnCheckActionMessage() {
      return "Change prevent lost";
    }

    @Override
    protected void btnDefaultAction(InternalInputPort iip) {
      iip.setPreventLost(diagramDefaultPreventLost());
      iip.setPreventLostDefault(true);
      refresh(iip);
    }

    @Override
    protected String btnDefaultActionMessage() {
      return "Set prevent lost to default";
    }

    @Override
    protected void initComponents() {
      super.initComponents();
      lbl.setText("Prevent data lost:");
    }

    private void refresh(InternalInputPort ip) {
      if (ip.isPreventLostDefault()) {
        btnCheck.setSelection(diagramDefaultPreventLost());
      } else {
        btnCheck.setSelection(ip.isPreventLost());
      }
      btnDefault.setEnabled(!ip.isPreventLostDefault());
    }

    private boolean diagramDefaultPreventLost() {
      return Optional.of(getDiagram()).filter(RuminaqDiagram.class::isInstance)
          .map(RuminaqDiagram.class::cast).map(RuminaqDiagram::getMainTask)
          .map(MainTask::isPreventLosts).orElse(Boolean.FALSE);
    }
  }

  private final class QueueSize extends AbstractDefault {

    private Text txtQueueSize;

    private QueueSize() {
      txtQueueSize = toolkit.createText(cmp, "", SWT.BORDER);
      GridData lytQuequeSize = new GridData(SWT.LEFT, SWT.CENTER, false, false,
          1, 1);
      lytQuequeSize.minimumWidth = QUEUE_TXT_WIDTH;
      lytQuequeSize.widthHint = QUEUE_TXT_WIDTH;
      txtQueueSize.setLayoutData(lytQuequeSize);
      btnDefault = new Button(cmp, SWT.PUSH);
    }

    @Override
    protected void initActions() {
      txtQueueSize.addFocusListener(new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent event) {
          modelFrom(getSelectedPictogramElement())
              .ifPresent((InternalInputPort iip) -> {
                if (validateQueueSize(txtQueueSize.getText())) {
                  ModelUtil.runModelChange(() -> {
                    iip.setQueueSize(txtQueueSize.getText());
                    refresh(iip);
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
    }

    @Override
    protected void initComponents() {
      super.initComponents();
      lbl.setText("Data queue size");
    }

    private void refresh(InternalInputPort ip) {
      txtQueueSize.setText(ip.getQueueSize());
      boolean quequeVisible = !((ip.isAsynchronous()
          || !ip.getTask().isAtomic()) && ip.getTask() instanceof EmbeddedTask);
      lbl.setVisible(quequeVisible);
      cmp.setVisible(quequeVisible);
      btnDefault
          .setEnabled(!ip.getDefaultQueueSize().equals(ip.getQueueSize()));

    }

    @Override
    protected void btnDefaultAction(InternalInputPort iip) {
      iip.setQueueSize(iip.getDefaultQueueSize());
      refresh(iip);
    }

    @Override
    protected String btnDefaultActionMessage() {
      return "Set queue size to default";
    }
  }

  private final class HoldLast extends AbstractCheckboxDefault {

    @Override
    protected void btnCheckAction(InternalInputPort iip) {
      iip.setHoldLast(btnCheck.getSelection());
      refresh(iip);
    }

    @Override
    protected String btnCheckActionMessage() {
      return "Change hold last";
    }

    @Override
    protected void btnDefaultAction(InternalInputPort iip) {
      iip.setHoldLast(iip.isDefaultHoldLast());
      refresh(iip);
    }

    @Override
    protected String btnDefaultActionMessage() {
      return "Set hold last to default";
    }

    @Override
    protected void initComponents() {
      super.initComponents();
      lbl.setText("Hold last data");
    }

    private void refresh(InternalInputPort ip) {
      btnCheck.setSelection(ip.isHoldLast());
      boolean holdVisible = !(ip.isAsynchronous() || !ip.getTask().isAtomic());
      lbl.setVisible(holdVisible);
      cmp.setVisible(holdVisible);
      btnDefault.setEnabled(ip.isDefaultHoldLast() != ip.isHoldLast());

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

  private static boolean validateQueueSize(String value) {
    return (NumericUtil.isOneDimPositiveInteger(value)
        && Integer.parseInt(value) != 0) || GlobalUtil.isGlobalVariable(value)
        || AbstractCreateUserDefinedTaskPage.INF.equals(value);
  }

  /**
   * Layout.
   *
   * <pre>
   * Name:              myName
   * Type of data:      dataType
   * Asynchronous:      true|false
   * Group:             None|1|2..
   *                        __________________
   * Prevent data lost: [x] | set to default |
   *                        ~~~~~~~~~~~~~~~~~~
   * Ignore lossy cast: [x]
   *                    _____ __________________
   * Data queue size:   |   | | set to default |
   *                    ~~~~~ ~~~~~~~~~~~~~~~~~~
   *                        __________________
   * Hold last data:    [x] | set to default |
   *                        ~~~~~~~~~~~~~~~~~~
   * </pre>
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
          String group = switch (ip.getGroup()) {
            case -1 -> "None";
            default -> Integer.toString(ip.getGroup());
          };
          lblGroupValue.setText(group);
          preventLost.refresh(ip);
          btnIgnoreLossyCast.setSelection(ip.isIgnoreLossyCast());
          queueSize.refresh(ip);
          holdLast.refresh(ip);
          lblDataType.getParent().layout();
        });
  }
}
