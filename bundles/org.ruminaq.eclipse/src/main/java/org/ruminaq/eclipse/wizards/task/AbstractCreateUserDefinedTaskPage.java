/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.usertask.model.userdefined.In;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.Out;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;

/**
 * Common wizard page for all custom tasks.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractCreateUserDefinedTaskPage extends WizardPage
    implements CreateUserDefinedTaskPage {

  private static final int TWO_COLUMNS = 2;

  public static final String INF = "inf";

  private class OutputsSection extends Group {

    private Table tblOutputs;
    private TableColumn tblclOutputsName;
    private TableColumn tblclOutputsData;
    private DragSource tblOutputsDragSrc;
    private DropTarget tblOutputsDropTrg;

    private Label lblOutputsAddName;
    private Text txtOutputsAddName;
    private Combo cmbOutputsAddData;
    private Button btnOutputsAdd;

    private Button btnOutputsRemove;

    public OutputsSection(Composite parent, int style) {
      super(parent, style);
      setLayout(new GridLayout(2, false));
      setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    }

    @Override
    protected void checkSubclass() {
      // allow subclass
    }

    private void initLayout() {
      tblOutputs = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
      tblOutputs
          .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

      tblclOutputsName = new TableColumn(tblOutputs, SWT.NONE);
      tblclOutputsData = new TableColumn(tblOutputs, SWT.NONE);

      tblOutputsDragSrc = new DragSource(tblOutputs, DND.DROP_MOVE);
      tblOutputsDropTrg = new DropTarget(tblOutputs,
          DND.DROP_MOVE | DND.DROP_DEFAULT);

      Group grpOutputsAdd = new Group(this, SWT.NONE);
      grpOutputsAdd.setLayout(new GridLayout(5, false));

      btnOutputsRemove = new Button(this, SWT.PUSH);

      lblOutputsAddName = new Label(grpOutputsAdd, SWT.NONE);
      lblOutputsAddName.setLayoutData(
          new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
      txtOutputsAddName = new Text(grpOutputsAdd, SWT.BORDER);
      cmbOutputsAddData = new Combo(grpOutputsAdd, SWT.NONE | SWT.READ_ONLY);
      btnOutputsAdd = new Button(grpOutputsAdd, SWT.PUSH);
    }

    private void initComponents() {
      tblOutputs.setHeaderVisible(true);
      tblOutputs.setLinesVisible(true);

      tblclOutputsName.setText("Name");
      tblclOutputsData.setText("Data type");
      tblOutputsDragSrc.setTransfer(types);
      tblOutputsDropTrg.setTransfer(types);
      Stream.of(tblOutputs.getColumns()).forEach(TableColumn::pack);

      lblOutputsAddName.setText("Name:");
      getDataTypes().stream().forEach(cmbOutputsAddData::add);
      cmbOutputsAddData.select(0);
      btnOutputsAdd.setText("Add");
      btnOutputsAdd.setEnabled(false);

      btnOutputsRemove.setText("Remove");
      btnOutputsRemove.setEnabled(false);
    }

    private void initActions() {
      tblOutputs.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          btnOutputsRemove.setEnabled(true);
        }
      });
      tblOutputsDragSrc.addDragListener(new DragSourceAdapter() {
        @Override
        public void dragStart(DragSourceEvent event) {
          int[] is = tblOutputs.getSelectionIndices();
          for (int i = 0; i < is.length; i++)
            if (i > 0 && (is[i] - is[i - 1]) != 1) {
              event.doit = false;
              return;
            }
          event.doit = true;
        }
      });
      tblOutputsDropTrg.addDropListener(new DropTargetAdapter() {
        @Override
        public void dragOver(DropTargetEvent event) {
          event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
        }

        @Override
        public void drop(DropTargetEvent event) {
          DropTarget target = (DropTarget) event.widget;
          Table table = (Table) target.getControl();
          if (table != tblOutputs)
            return;

          TableItem[] items = tblOutputs.getSelection();

          TableItem ti = (TableItem) event.item;
          int idx = -1;
          int i = 0;
          for (TableItem it : tblOutputs.getItems()) {
            if (it == ti) {
              idx = i;
              break;
            }
            i++;
          }
          if (i == -1 || i >= tblOutputs.getItems().length)
            return;

          i = idx + 1;
          for (TableItem it : items) {
            new TableItem(tblOutputs, SWT.NONE, i).setText(
                new String[] { it.getText(0), it.getText(1), it.getText(2) });
            i++;
          }
          for (TableItem it : items)
            it.dispose();
          tblOutputs.redraw();
        }
      });
      txtOutputsAddName.addModifyListener((ModifyEvent event) -> {
        boolean exist = false;
        for (TableItem it : tblOutputs.getItems())
          if (it.getText(0).equals(txtOutputsAddName.getText()))
            exist = true;
        if ("".equals(txtOutputsAddName.getText()) || exist)
          btnOutputsAdd.setEnabled(false);
        else
          btnOutputsAdd.setEnabled(true);
      });
      btnOutputsAdd.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          TableItem item = new TableItem(tblOutputs, SWT.NONE);
          item.setText(new String[] { txtOutputsAddName.getText(),
              cmbOutputsAddData.getText() });
          for (TableColumn tc : tblOutputs.getColumns())
            tc.pack();
          tblOutputs.layout();
          txtOutputsAddName.setText("");
        }
      });
      btnOutputsRemove.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          int[] selectionIds = tblOutputs.getSelectionIndices();
          if (selectionIds.length != 0)
            btnOutputsRemove.setEnabled(false);
          for (int i = 0, n = selectionIds.length; i < n; i++)
            tblOutputs.remove(selectionIds[i]);
        }
      });
    }

    public void decorate(Module module) {
      for (TableItem it : tblOutputs.getItems()) {
        Out out = UserdefinedFactory.eINSTANCE.createOut();

        out.setName(it.getText(0));
        out.setDataType(it.getText(1));

        module.getOutputs().add(out);
      }
    }
  }

  Composite root;
  private GeneralSection grpGeneral;
  private RunnerSection grpRunner;
  private Label lblInputPorts;
  private InputsSection grpInputs;
  private Label lblOutputPorts;
  private OutputsSection grpOutputs;
  private Label lblParameters;
  private ParametersSection grpParameters;

  static class RowTransfer extends ByteArrayTransfer {
    private static final String ROWTYPENAME = "RowType";
    private static final int ROWTYPEID = registerType(ROWTYPENAME);
    private static RowTransfer instance = new RowTransfer();

    public static RowTransfer getInstance() {
      return instance;
    }

    @Override
    protected String[] getTypeNames() {
      return new String[] { ROWTYPENAME };
    }

    @Override
    protected int[] getTypeIds() {
      return new int[] { ROWTYPEID };
    }

    @Override
    public void javaToNative(Object object, TransferData transferData) {
    }

    @Override
    public Object nativeToJava(TransferData transferData) {
      return null;
    }
  }

  Transfer[] types = new Transfer[] { RowTransfer.getInstance() };

  public AbstractCreateUserDefinedTaskPage(String pageName) {
    super(pageName);
    setDescription(Messages.createUserDefinedTaskPageDescription);
  }

  @Override
  public void createControl(Composite parent) {
    initLayout(parent);
    initComponents();
    initActions();

    setControl(root);
  }

  private void initLayout(Composite parent) {
    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(TWO_COLUMNS, false));

    grpGeneral = new GeneralSection(root, SWT.NONE);
    grpRunner = new RunnerSection(root, SWT.NONE);

    lblInputPorts = new Label(root, SWT.NONE);
    lblInputPorts.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    grpInputs = new InputsSection(this, root, SWT.NONE);

    lblOutputPorts = new Label(root, SWT.NONE);
    lblOutputPorts.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    grpOutputs = new OutputsSection(root, SWT.NONE);
    grpOutputs.initLayout();

    lblParameters = new Label(root, SWT.NONE);
    lblParameters.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false, TWO_COLUMNS, 1));

    grpParameters = new ParametersSection(root, SWT.NONE);
  }

  private void initComponents() {
    lblInputPorts.setText("Input Ports:");
    lblOutputPorts.setText("Output Ports:");
    grpOutputs.initComponents();
    lblParameters.setText("Parameters:");
  }

  private void initActions() {
    grpInputs.initActions();
    grpOutputs.initActions();
  }

  @Override
  public Module getModel() {
    Module module = UserdefinedFactory.eINSTANCE.createModule();
    grpGeneral.decorate(module);
    grpRunner.decorate(module);
    grpInputs.decorate(module);
    grpOutputs.decorate(module);
    grpParameters.decorate(module);

    boolean hasAsync = module.getInputs().stream().anyMatch(In::isAsynchronous);
    boolean hasNonAsync = module.getInputs().stream()
        .anyMatch(Predicate.not(In::isAsynchronous));

    module.setExecuteAsync((!module.isAtomic() && hasNonAsync) || hasAsync);
    module.setExecuteExtSrc(module.isExternalSource());
    module.setGenerate(module.isGenerator());
    module
        .setExecute((module.isAtomic() && hasNonAsync) || module.isConstant());

    return module;
  }
}
