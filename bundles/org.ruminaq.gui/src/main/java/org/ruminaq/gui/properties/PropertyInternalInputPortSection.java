/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.eclipse.wizards.task.AbstractCreateUserDefinedTaskPage;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.util.GlobalUtil;
import org.ruminaq.util.NumericUtil;
import org.ruminaq.util.WidgetSelectedSelectionListener;

public class PropertyInternalInputPortSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private Composite root;
  private Label lblId;
  private Label lblIdValue;
  private Label lblTypeOfData;
  private Label dataTypeValue;
  private Label lblAsynchronus;
  private Label lblGroup;
  private Label lblGroupValue;
  private Label lblSyncConn;
  private Composite cmpPreventLost;
  private Button btnPreventLostDefault;
  private Button btnPreventLost;
  private Label lblAsynchronousValue;
  private Label lblIgnoreLossyCast;
  private Button btnIgnoreLossyCast;
  private Composite cmpQueueSize;
  private Label lblQueueSize;
  private Text txtQueueSize;
  private Button btnDefaultQueueSize;
  private Composite cmpHoldLast;
  private Label lblHoldLast;
  private Button btnHoldLast;
  private Button btnDefaultHoldLast;

  /**
   * @wbp.parser.entryPoint
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
    FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    root = toolkit.createComposite(parent, SWT.WRAP);
    root.setLayout(new GridLayout(2, false));

    lblId = toolkit.createLabel(root, "", SWT.NONE);
    lblIdValue = toolkit.createLabel(root, "", SWT.NONE);

    lblTypeOfData = toolkit.createLabel(root, "", SWT.NONE);
    dataTypeValue = toolkit.createLabel(root, "", SWT.NONE);

    lblAsynchronus = toolkit.createLabel(root, "", SWT.NONE);
    lblAsynchronousValue = toolkit.createLabel(root, "", SWT.NONE);

    lblGroup = toolkit.createLabel(root, "", SWT.NONE);
    lblGroupValue = toolkit.createLabel(root, "", SWT.NONE);

    lblSyncConn = toolkit.createLabel(root, "", SWT.NONE);
    cmpPreventLost = toolkit.createComposite(root, SWT.NONE);
    cmpPreventLost.setLayout(new GridLayout(2, false));
    btnPreventLostDefault = new Button(cmpPreventLost, SWT.CHECK);
    btnPreventLost = new Button(cmpPreventLost, SWT.CHECK);

    lblIgnoreLossyCast = toolkit.createLabel(root, "", SWT.NONE);
    btnIgnoreLossyCast = new Button(root, SWT.CHECK);

    lblQueueSize = toolkit.createLabel(root, "", SWT.NONE);
    cmpQueueSize = toolkit.createComposite(root, SWT.NONE);
    cmpQueueSize.setLayout(new GridLayout(2, false));
    txtQueueSize = toolkit.createText(cmpQueueSize, "", SWT.BORDER);
    GridData lytQuequeSize = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
        1);
    lytQuequeSize.minimumWidth = 25;
    lytQuequeSize.widthHint = 25;
    txtQueueSize.setLayoutData(lytQuequeSize);
    btnDefaultQueueSize = new Button(cmpQueueSize, SWT.PUSH);
    lblHoldLast = toolkit.createLabel(root, "", SWT.NONE);
    cmpHoldLast = toolkit.createComposite(root, SWT.NONE);
    cmpHoldLast.setLayout(new GridLayout(2, false));
    btnHoldLast = new Button(cmpHoldLast, SWT.CHECK);
    btnDefaultHoldLast = new Button(cmpHoldLast, SWT.PUSH);
  }

  private void initActions() {
    btnPreventLostDefault.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent se) -> {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              PictogramElement pe = getSelectedPictogramElement();
              if (pe == null)
                return;
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof InternalInputPort) {
                InternalInputPort iip = (InternalInputPort) bo;
                iip.setPreventLostDefault(btnPreventLostDefault.getSelection());
                btnPreventLost
                    .setEnabled(!btnPreventLostDefault.getSelection());
                if (btnPreventLostDefault.getSelection()) {
                  iip.setPreventLost(iip.isPreventLostDefault());
                  btnPreventLost.setSelection(iip.isPreventLostDefault());
                }
              }
            }
          }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              "Model Update");
        });
    btnPreventLost.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent se) -> {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              PictogramElement pe = getSelectedPictogramElement();
              if (pe == null)
                return;
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof InternalInputPort) {
                InternalInputPort iip = (InternalInputPort) bo;
                iip.setPreventLost(btnPreventLost.getSelection());
              }
            }
          }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              "Model Update");
        });
    btnIgnoreLossyCast.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent e) -> {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              PictogramElement pe = getSelectedPictogramElement();
              if (pe == null)
                return;
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof InternalInputPort) {
                InternalInputPort iip = (InternalInputPort) bo;
                iip.setIgnoreLossyCast(btnIgnoreLossyCast.getSelection());
              }
            }
          }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              "Change console type");
        });
    txtQueueSize.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        Shell shell = txtQueueSize.getShell();
        boolean parse = (NumericUtil
            .isOneDimPositiveInteger(txtQueueSize.getText())
            && Integer.parseInt(txtQueueSize.getText()) != 0)
            || GlobalUtil.isGlobalVariable(txtQueueSize.getText())
            || AbstractCreateUserDefinedTaskPage.INF.equals(txtQueueSize.getText());
        PictogramElement pe = getSelectedPictogramElement();
        if (pe == null)
          return;
        Object bo = Graphiti.getLinkService()
            .getBusinessObjectForLinkedPictogramElement(pe);
        if (bo == null || !(bo instanceof InternalInputPort))
          return;
        final InternalInputPort iip = (InternalInputPort) bo;
        if (parse) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              iip.setQueueSize(txtQueueSize.getText());
              btnDefaultQueueSize.setEnabled(
                  !iip.getDefaultQueueSize().equals(iip.getQueueSize()));
            }
          }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              "Change queque size");
        } else {
          MessageDialog.openError(shell, "Can't edit value",
              "Don't understant value");
          txtQueueSize.setText(iip.getQueueSize());
        }
      }
    });
    txtQueueSize.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          btnIgnoreLossyCast.setFocus();
      }
    });
    btnDefaultQueueSize.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        PictogramElement pe = getSelectedPictogramElement();
        if (pe == null)
          return;
        Object bo = Graphiti.getLinkService()
            .getBusinessObjectForLinkedPictogramElement(pe);
        if (bo == null || !(bo instanceof InternalInputPort))
          return;
        final InternalInputPort iip = (InternalInputPort) bo;
        ModelUtil.runModelChange(new Runnable() {
          public void run() {
            iip.setQueueSize(iip.getDefaultQueueSize());
            refresh();
          }
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change console type");
      }
    });
    btnHoldLast.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent e) -> {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              PictogramElement pe = getSelectedPictogramElement();
              if (pe == null)
                return;
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof InternalInputPort) {
                InternalInputPort iip = (InternalInputPort) bo;
                iip.setHoldLast(btnHoldLast.getSelection());
                btnDefaultHoldLast
                    .setEnabled(iip.isDefaultHoldLast() != iip.isHoldLast());
              }
            }
          }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              "Change console type");
        });
    btnDefaultHoldLast.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent e) -> {
          PictogramElement pe = getSelectedPictogramElement();
          if (pe == null)
            return;
          Object bo = Graphiti.getLinkService()
              .getBusinessObjectForLinkedPictogramElement(pe);
          if (bo == null || !(bo instanceof InternalInputPort))
            return;
          final InternalInputPort iip = (InternalInputPort) bo;
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              iip.setHoldLast(iip.isDefaultHoldLast());
              refresh();
            }
          }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
              "Change console type");
        });
  }

  private void initComponents() {
    lblId.setText("Name:");
    lblTypeOfData.setText("Type of data:");
    lblAsynchronus.setText("Asynchronus:");
    lblGroup.setText("Group:");
    lblSyncConn.setText("Prevent data lost:");
    btnPreventLostDefault.setText("default");
    btnPreventLost.setText("custom");
    lblIgnoreLossyCast.setText("Ignore lossy cast");
    lblQueueSize.setText("Data queue size");
    btnDefaultQueueSize.setText("set to default");
    lblHoldLast.setText("Hold last data");
    btnDefaultHoldLast.setText("set to default");
  }

  private void addStyles() {
    dataTypeValue.setFont(JFaceResources.getFontRegistry().getItalic(""));
    lblAsynchronousValue
        .setFont(JFaceResources.getFontRegistry().getItalic(""));
  }

  @Override
  public void refresh() {
    PictogramElement pe = getSelectedPictogramElement();
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (bo == null)
        return;
      final InternalInputPort ip = (InternalInputPort) bo;

      lblIdValue.setText(ip.getId());

      // Data type
      StringBuilder dataType = new StringBuilder();
      for (DataType dt : ip.getDataType())
        dataType.append(ModelUtil.getName(dt.getClass(), false)).append(", ");
      if (dataType.length() > 2)
        dataType.delete(dataType.length() - 2, dataType.length());

      dataTypeValue.setText(dataType.toString());

      lblAsynchronousValue.setText(Boolean.toString(ip.isAsynchronous()));
      lblGroupValue.setText(
          ip.getGroup() == -1 ? "None" : Integer.toString(ip.getGroup()));

      if (ip.isPreventLostDefault()) {
        btnPreventLostDefault.setSelection(true);
        btnPreventLost.setEnabled(false);
      } else {
        btnPreventLostDefault.setSelection(false);
        btnPreventLost.setSelection(ip.isPreventLost());
        btnPreventLost.setEnabled(true);
      }

      btnIgnoreLossyCast.setSelection(ip.isIgnoreLossyCast());
      txtQueueSize.setText(ip.getQueueSize());
      btnHoldLast.setSelection(ip.isHoldLast());

      boolean quequeVisible = !((ip.isAsynchronous()
          || !ip.getTask().isAtomic()) && ip.getTask() instanceof EmbeddedTask);
      lblQueueSize.setVisible(quequeVisible);
      cmpQueueSize.setVisible(quequeVisible);
      if (quequeVisible)
        btnDefaultQueueSize
            .setEnabled(!ip.getDefaultQueueSize().equals(ip.getQueueSize()));

      boolean holdVisible = !(ip.isAsynchronous() || !ip.getTask().isAtomic());
      lblHoldLast.setVisible(holdVisible);
      cmpHoldLast.setVisible(holdVisible);
      if (holdVisible)
        btnDefaultHoldLast
            .setEnabled(ip.isDefaultHoldLast() != ip.isHoldLast());

    }
    lblTypeOfData.getParent().layout();
  }

  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
  }
}
