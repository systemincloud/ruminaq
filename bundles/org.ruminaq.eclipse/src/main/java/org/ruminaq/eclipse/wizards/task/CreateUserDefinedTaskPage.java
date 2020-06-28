/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.wizards.task;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
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
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.Messages;
import org.ruminaq.eclipse.usertask.model.userdefined.In;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.Out;
import org.ruminaq.eclipse.usertask.model.userdefined.Parameter;
import org.ruminaq.eclipse.usertask.model.userdefined.UserdefinedFactory;

/**
 *
 * @author Marek Jagielski
 */
public abstract class CreateUserDefinedTaskPage extends WizardPage
    implements ICreateUserDefinedTaskPage {

  private Composite root;

  private Group grpGeneral;
  private Button btnAtomic;
  private Button btnGenerator;
  private Button btnExternalSource;
  private Button btnConstant;

  private Group grpRunner;
  private Button btnRunnerStart;
  private Button btnRunnerStop;

  private Label lblInputPorts;
  private Table tblInputs;
  private TableColumn tblclInputsName;
  private TableColumn tblclInputsData;
  private TableColumn tblclInputsAsync;
  private TableColumn tblclInputsGroup;
  private TableColumn tblclInputsHold;
  private TableColumn tblclInputsQueue;
  private DragSource tblInputsDragSrc;
  private DropTarget tblInputsDropTrg;

  private Label lblInputsAddName;
  private Text txtInputsAddName;
  private Combo cmbInputsAddData;
  private Button btnInputsAddAsync;
  private Button btnInputsAddHold;
  private Label lblInputsAddGroup;
  private Spinner spnInputsAddGroup;
  private Composite cmpInputsAddQueue;
  private Label lblInputsAddQueue;
  private Button btnInputsAddQueueInf;
  private Spinner spnInputsAddQueue;
  private Button btnInputsAdd;

  private Button btnInputsRemove;

  private Label lblOutputPorts;
  private Table tblOutputs;
  private TableColumn tblclOutputsName;
  private TableColumn tblclOutputsData;
  private DragSource tblOutputsDragSrc;
  private DropTarget tblOutputsDropTrg;

  private Group grpOutputsAdd;
  private Label lblOutputsAddName;
  private Text txtOutputsAddName;
  private Combo cmbOutputsAddData;
  private Button btnOutputsAdd;

  private Button btnOutputsRemove;

  private Label lblParameters;
  private Table tblParameters;
  private TableColumn tblclParametersName;
  private TableColumn tblclParametersValue;

  private Group grpParametersAdd;
  private Label lblParametersAddName;
  private Text txtParametersAddName;
  private Label lblParametersAddValue;
  private Text txtParametersAddValue;
  private Button btnParametersAdd;

  private Button btnParametersRemove;

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

  private Transfer[] types = new Transfer[] { RowTransfer.getInstance() };

  public CreateUserDefinedTaskPage(String pageName) {
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
    root.setLayout(new GridLayout(2, false));

    grpGeneral = new Group(root, SWT.NONE);
    grpGeneral.setLayout(new GridLayout(5, false));
    grpGeneral
        .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

    btnAtomic = new Button(grpGeneral, SWT.CHECK);
    btnGenerator = new Button(grpGeneral, SWT.CHECK);
    btnExternalSource = new Button(grpGeneral, SWT.CHECK);
    btnConstant = new Button(grpGeneral, SWT.CHECK);

    grpRunner = new Group(root, SWT.NONE);
    grpRunner.setLayout(new GridLayout(2, false));
    grpRunner
        .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

    btnRunnerStart = new Button(grpRunner, SWT.CHECK);
    btnRunnerStop = new Button(grpRunner, SWT.CHECK);

    lblInputPorts = new Label(root, SWT.NONE);
    lblInputPorts
        .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

    tblInputs = new Table(root, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tblInputs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

    tblclInputsName = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsData = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsAsync = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsGroup = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsHold = new TableColumn(tblInputs, SWT.NONE);
    tblclInputsQueue = new TableColumn(tblInputs, SWT.NONE);

    tblInputsDragSrc = new DragSource(tblInputs, DND.DROP_MOVE);
    tblInputsDropTrg = new DropTarget(tblInputs,
        DND.DROP_MOVE | DND.DROP_DEFAULT);

    Group grpInputsAdd = new Group(root, SWT.NONE);
    grpInputsAdd.setLayout(new GridLayout(5, false));

    lblInputsAddName = new Label(grpInputsAdd, SWT.NONE);
    lblInputsAddName
        .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    txtInputsAddName = new Text(grpInputsAdd, SWT.BORDER);
    cmbInputsAddData = new Combo(grpInputsAdd, SWT.NONE | SWT.READ_ONLY);
    Composite cmpInputsAddOptions = new Composite(grpInputsAdd, SWT.NULL);
    cmpInputsAddOptions.setLayout(new GridLayout(1, false));
    btnInputsAddAsync = new Button(cmpInputsAddOptions, SWT.CHECK);
    btnInputsAddHold = new Button(cmpInputsAddOptions, SWT.CHECK);
    Composite cmpInputsAddGroup = new Composite(cmpInputsAddOptions, SWT.NULL);
    cmpInputsAddGroup.setLayout(new GridLayout(2, false));
    lblInputsAddGroup = new Label(cmpInputsAddGroup, SWT.NONE);
    spnInputsAddGroup = new Spinner(cmpInputsAddGroup, SWT.BORDER);
    cmpInputsAddQueue = new Composite(cmpInputsAddOptions, SWT.NULL);
    cmpInputsAddQueue.setLayout(new GridLayout(3, false));
    lblInputsAddQueue = new Label(cmpInputsAddQueue, SWT.NONE);
    btnInputsAddQueueInf = new Button(cmpInputsAddQueue, SWT.CHECK);
    spnInputsAddQueue = new Spinner(cmpInputsAddQueue, SWT.BORDER);
    btnInputsAdd = new Button(grpInputsAdd, SWT.PUSH);

    btnInputsRemove = new Button(root, SWT.PUSH);

    lblOutputPorts = new Label(root, SWT.NONE);
    lblOutputPorts
        .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

    tblOutputs = new Table(root, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tblOutputs
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

    tblclOutputsName = new TableColumn(tblOutputs, SWT.NONE);
    tblclOutputsData = new TableColumn(tblOutputs, SWT.NONE);

    tblOutputsDragSrc = new DragSource(tblOutputs, DND.DROP_MOVE);
    tblOutputsDropTrg = new DropTarget(tblOutputs,
        DND.DROP_MOVE | DND.DROP_DEFAULT);

    grpOutputsAdd = new Group(root, SWT.NONE);
    grpOutputsAdd.setLayout(new GridLayout(5, false));

    btnOutputsRemove = new Button(root, SWT.PUSH);

    lblOutputsAddName = new Label(grpOutputsAdd, SWT.NONE);
    lblOutputsAddName
        .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    txtOutputsAddName = new Text(grpOutputsAdd, SWT.BORDER);
    cmbOutputsAddData = new Combo(grpOutputsAdd, SWT.NONE | SWT.READ_ONLY);
    btnOutputsAdd = new Button(grpOutputsAdd, SWT.PUSH);

    lblParameters = new Label(root, SWT.NONE);
    lblParameters
        .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

    tblParameters = new Table(root,
        SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
    tblParameters
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

    tblclParametersName = new TableColumn(tblParameters, SWT.NONE);
    tblclParametersValue = new TableColumn(tblParameters, SWT.NONE);

    grpParametersAdd = new Group(root, SWT.NONE);
    grpParametersAdd.setLayout(new GridLayout(5, false));

    btnParametersRemove = new Button(root, SWT.PUSH);

    lblParametersAddName = new Label(grpParametersAdd, SWT.NONE);
    lblParametersAddName
        .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    txtParametersAddName = new Text(grpParametersAdd, SWT.BORDER);
    lblParametersAddValue = new Label(grpParametersAdd, SWT.NONE);
    lblParametersAddValue
        .setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    txtParametersAddValue = new Text(grpParametersAdd, SWT.BORDER);
    btnParametersAdd = new Button(grpParametersAdd, SWT.PUSH);
  }

  private void initComponents() {
    btnAtomic.setText("atomic");
    btnAtomic.setSelection(true);
    btnGenerator.setText("generator");
    btnExternalSource.setText("external source");
    btnConstant.setText("constant");

    btnRunnerStart.setText("runnerStart");
    btnRunnerStop.setText("runnerStop");

    lblInputPorts.setText("Input Ports:");

    tblInputs.setHeaderVisible(true);
    tblInputs.setLinesVisible(true);

    tblclInputsName.setText("Name");
    tblclInputsData.setText("Data type");
    tblclInputsAsync.setText("Async");
    tblclInputsGroup.setText("Grp");
    tblclInputsHold.setText("Hold");
    tblclInputsQueue.setText("Queue");
    tblInputsDragSrc.setTransfer(types);
    tblInputsDropTrg.setTransfer(types);
    Stream.of(tblInputs.getColumns()).forEach(TableColumn::pack);

    lblInputsAddName.setText("Name:");
    getDataTypes().stream().forEach(cmbInputsAddData::add);
    cmbInputsAddData.select(0);
    btnInputsAddAsync.setText("asynchronous");
    btnInputsAddHold.setText("hold last data");
    lblInputsAddGroup.setText("group");
    spnInputsAddGroup.setMinimum(-1);
    spnInputsAddGroup.setMaximum(Integer.MAX_VALUE);
    spnInputsAddGroup.setSelection(-1);
    btnInputsAddQueueInf.setText("inf");
    lblInputsAddQueue.setText("queue size:");
    spnInputsAddQueue.setMinimum(1);
    spnInputsAddQueue.setMaximum(Integer.MAX_VALUE);
    spnInputsAddQueue.setSelection(1);
    btnInputsAdd.setText("Add");
    btnInputsAdd.setEnabled(false);

    btnInputsRemove.setText("Remove");
    btnInputsRemove.setEnabled(false);

    lblOutputPorts.setText("Output Ports:");

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

    lblParameters.setText("Parameters:");

    tblParameters.setHeaderVisible(true);
    tblParameters.setLinesVisible(true);

    tblclParametersName.setText("Name");
    tblclParametersValue.setText("DefaultValue");
    Stream.of(tblParameters.getColumns()).forEach(TableColumn::pack);

    lblParametersAddName.setText("Name:");
    lblParametersAddValue.setText("Default value:");
    btnParametersAdd.setText("Add");
    btnParametersAdd.setEnabled(false);

    btnParametersRemove.setText("Remove");
    btnParametersRemove.setEnabled(false);
  }

  protected abstract List<String> getDataTypes();

  private void initActions() {
    btnAtomic.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        if (!btnAtomic.getSelection()) {
          btnConstant.setSelection(false);
        }
      }
    });
    btnConstant.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        if (btnConstant.getSelection()) {
          btnAtomic.setSelection(true);
          btnGenerator.setSelection(false);
          btnExternalSource.setSelection(false);
        }
      }
    });
    btnGenerator.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        if (btnGenerator.getSelection()) {
          btnConstant.setSelection(false);
        }
      }
    });
    btnExternalSource.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        if (btnExternalSource.getSelection()) {
          btnConstant.setSelection(false);
        }
      }
    });
    tblInputs.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        btnInputsRemove.setEnabled(true);
      }
    });
    tblInputsDragSrc.addDragListener(new DragSourceAdapter() {
      @Override
      public void dragStart(DragSourceEvent event) {
        int[] is = tblInputs.getSelectionIndices();
        for (int i = 0; i < is.length; i++)
          if (i > 0 && (is[i] - is[i - 1]) != 1) {
            event.doit = false;
            return;
          }
        event.doit = true;
      }
    });
    tblInputsDropTrg.addDropListener(new DropTargetAdapter() {
      @Override
      public void dragOver(DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
      }

      @Override
      public void drop(DropTargetEvent event) {
        DropTarget target = (DropTarget) event.widget;
        Table table = (Table) target.getControl();
        if (table != tblInputs)
          return;

        TableItem[] items = tblInputs.getSelection();

        TableItem ti = (TableItem) event.item;
        int idx = -1;
        int i = 0;
        for (TableItem it : tblInputs.getItems()) {
          if (it == ti) {
            idx = i;
            break;
          }
          i++;
        }
        if (i == -1 || i >= tblInputs.getItems().length)
          return;

        i = idx + 1;
        for (TableItem it : items) {
          new TableItem(tblInputs, SWT.NONE, i)
              .setText(new String[] { it.getText(0), it.getText(1),
                  it.getText(2), it.getText(3), it.getText(4), it.getText(5) });
          i++;
        }
        for (TableItem it : items)
          it.dispose();
        tblInputs.redraw();
      }
    });
    txtInputsAddName.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent event) {
        boolean exist = false;
        for (TableItem it : tblInputs.getItems())
          if (it.getText(0).equals(txtInputsAddName.getText()))
            exist = true;
        if ("".equals(txtInputsAddName.getText()) || exist)
          btnInputsAdd.setEnabled(false);
        else
          btnInputsAdd.setEnabled(true);
      }
    });
    btnInputsAddAsync.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        btnInputsAddHold.setEnabled(!btnInputsAddAsync.getSelection());
        spnInputsAddGroup.setEnabled(!btnInputsAddAsync.getSelection());
      }
    });
    btnInputsAddQueueInf.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        spnInputsAddQueue.setEnabled(!btnInputsAddQueueInf.getSelection());
      }
    });
    spnInputsAddQueue.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        root.layout();
      }
    });
    btnInputsAdd.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        TableItem item = new TableItem(tblInputs, SWT.NONE);
        boolean async = btnInputsAddAsync.getSelection();
        String grp = Integer.toString(spnInputsAddGroup.getSelection());
        boolean inf = btnInputsAddQueueInf.getSelection();
        item.setText(new String[] { txtInputsAddName.getText(),
            cmbInputsAddData.getText(), Boolean.toString(async),
            async ? "-1" : grp,
            async ? Boolean.toString(false)
                : Boolean.toString(btnInputsAddHold.getSelection()),
            inf ? Constants.INF
                : Integer.toString(spnInputsAddQueue.getSelection()) });
        for (TableColumn tc : tblInputs.getColumns())
          tc.pack();
        tblInputs.layout();
        txtInputsAddName.setText("");
      }
    });
    btnInputsRemove.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        int[] selectionIds = tblInputs.getSelectionIndices();
        if (selectionIds.length != 0)
          btnInputsRemove.setEnabled(false);
        for (int i = 0, n = selectionIds.length; i < n; i++)
          tblInputs.remove(selectionIds[i]);
      }
    });
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
        if (i == -1 || i >= tblInputs.getItems().length)
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
    txtOutputsAddName.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent event) {
        boolean exist = false;
        for (TableItem it : tblOutputs.getItems())
          if (it.getText(0).equals(txtOutputsAddName.getText()))
            exist = true;
        if ("".equals(txtOutputsAddName.getText()) || exist)
          btnOutputsAdd.setEnabled(false);
        else
          btnOutputsAdd.setEnabled(true);
      }
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
    tblParameters.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        btnParametersRemove.setEnabled(true);
      }
    });
    txtParametersAddName.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent event) {
        boolean exist = false;
        for (TableItem it : tblParameters.getItems())
          if (it.getText(0).equals(txtParametersAddName.getText()))
            exist = true;
        if ("".equals(txtParametersAddName.getText()) || exist)
          btnParametersAdd.setEnabled(false);
        else
          btnParametersAdd.setEnabled(true);
      }
    });
    btnParametersAdd.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        TableItem item = new TableItem(tblParameters, SWT.NONE);
        item.setText(new String[] { txtParametersAddName.getText(),
            txtParametersAddValue.getText() });
        for (TableColumn tc : tblParameters.getColumns())
          tc.pack();
        sortParameters();
        tblParameters.layout();
        txtParametersAddName.setText("");
        txtParametersAddValue.setText("");
      }
    });
    btnParametersRemove.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        int[] selectionIds = tblParameters.getSelectionIndices();
        if (selectionIds.length != 0)
          btnParametersRemove.setEnabled(false);
        for (int i = 0, n = selectionIds.length; i < n; i++)
          tblParameters.remove(selectionIds[i]);
      }
    });
  }

  private void sortParameters() {
    TableItem[] items = tblParameters.getItems();
    Collator collator = Collator.getInstance(Locale.getDefault());
    for (int i = 1; i < items.length; i++) {
      String value1 = items[i].getText(0);
      for (int j = 0; j < i; j++) {
        String value2 = items[j].getText(0);
        if (collator.compare(value1, value2) < 0) {
          String[] values = { items[i].getText(0) };
          items[i].dispose();
          TableItem item = new TableItem(tblParameters, SWT.NONE, j);
          item.setText(values);
          items = tblParameters.getItems();
          break;
        }
      }
    }
  }

  @Override
  public Module getModel() {
    Module module = UserdefinedFactory.eINSTANCE.createModule();

    module.setAtomic(btnAtomic.getSelection());
    module.setConstant(btnConstant.getSelection());
    module.setExternalSource(btnExternalSource.getSelection());
    module.setGenerator(btnGenerator.getSelection());

    module.setRunnerStart(btnRunnerStart.getSelection());
    module.setRunnerStop(btnRunnerStop.getSelection());

    for (TableItem it : tblParameters.getItems()) {
      Parameter parameter = UserdefinedFactory.eINSTANCE.createParameter();
      parameter.setName(it.getText(0));
      parameter.setDefaultValue(it.getText(1));
      module.getParameters().add(parameter);
    }

    for (TableItem it : tblInputs.getItems()) {
      In in = UserdefinedFactory.eINSTANCE.createIn();

      in.setName(it.getText(0));
      in.setDataType(it.getText(1));
      in.setAsynchronous(Boolean.parseBoolean(it.getText(2)));
      String sGrp = it.getText(3);
      in.setGroup(Integer.parseInt(sGrp));
      in.setHold(Boolean.parseBoolean(it.getText(4)));
      in.setQueue(it.getText(5).equals(Constants.INF) ? -1
          : Integer.parseInt(it.getText(5)));

      module.getInputs().add(in);
    }

    for (TableItem it : tblOutputs.getItems()) {
      Out out = UserdefinedFactory.eINSTANCE.createOut();

      out.setName(it.getText(0));
      out.setDataType(it.getText(1));

      module.getOutputs().add(out);
    }

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
