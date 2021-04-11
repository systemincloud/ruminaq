/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.InternalPort;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.NumericUtil;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Synchronization;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * SynchronizationSection for InternalOutputPort.
 *
 * @author Marek Jagielski
 */
public class PropertySynchronizationSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private static final String NONE = "-";
  private static final String IN = "[IN] ";
  private static final String OUT = "[OUT] ";

  private Composite root;

  private TreeViewer treVwOutputPorts;

  private Tree treOutputPorts;
  private Group group;
  private SyncTaskPort syncTaskPort;
  private Notify notify;
  private Skip skip;
  private Up up;
  private Down down;
  private Reset reset;
  private AddRemove addRemove;

  private Map<Object, List<Button>> buttons = new HashMap<>();

  private static Optional<TaskShape> shapeFrom(PictogramElement pe) {
    return Optional.ofNullable(pe).filter(TaskShape.class::isInstance)
        .map(TaskShape.class::cast);
  }

  private static Optional<Task> taskFrom(PictogramElement pe) {
    return shapeFrom(pe).map(TaskShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast);
  }

  private class Disposable {
    protected Map<Object, Button> buttons = new HashMap<>();

    public void dispose(Object o) {
      Optional.ofNullable(buttons.remove(o)).ifPresent(Button::dispose);
    }
  }

  private final class PortName {
    private TreeViewerColumn treclVwOutputPortsName;

    private PortName(TreeViewer treVwOutputPorts) {
      treclVwOutputPortsName = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsName.getColumn().setText("Output Port");
      treclVwOutputPortsName.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          return Optional.of(element)
              .filter(InternalOutputPort.class::isInstance)
              .map(InternalOutputPort.class::cast)
              .map(InternalOutputPort::getId).orElse("");
        }
      });
    }
  }

  private final class Group {
    private TreeViewerColumn treclVwOutputPortsGrp;

    private Group(TreeViewer treVwOutputPorts) {
      treclVwOutputPortsGrp = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsGrp.getColumn().setAlignment(SWT.CENTER);
      treclVwOutputPortsGrp.getColumn().setText("Group");
      treclVwOutputPortsGrp.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          return Optional.of(element).filter(Synchronization.class::isInstance)
              .map(Synchronization.class::cast)
              .map(s -> s.getGroup() == 0 ? NONE : "" + s.getGroup())
              .orElse("");
        }
      });

    }

    public void refresh() {
      treclVwOutputPortsGrp.setEditingSupport(
          new GroupEditingSupport(treclVwOutputPortsGrp.getViewer()));
    }
  }

  private final class SyncTaskPort {
    private TreeViewerColumn treclVwOutputPortsTask;
    private TreeViewerColumn treclVwOutputPortsPort;

    private SyncTaskPort(TreeViewer treVwOutputPorts) {
      treclVwOutputPortsTask = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsPort = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsTask.getColumn().setText("Sync Task");
      treclVwOutputPortsPort.getColumn().setText("Sync Port");
      treclVwOutputPortsTask.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          return Optional.of(element).filter(Synchronization.class::isInstance)
              .map(Synchronization.class::cast)
              .map(s -> s.getWaitForPort() == null ? NONE
                  : s.getWaitForPort().getTask().getId())
              .orElse("");
        }
      });
      treclVwOutputPortsPort.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          return Optional.of(element).filter(Synchronization.class::isInstance)
              .map(Synchronization.class::cast)
              .map(s -> s.getWaitForPort() == null ? NONE
                  : s.getWaitForPort() instanceof InternalOutputPort
                      ? OUT + s.getWaitForPort().getId()
                      : IN + s.getWaitForPort().getId())
              .orElse("");
        }
      });
    }

    public void refresh(Task t) {
      treclVwOutputPortsPort.setEditingSupport(
          new PortEditingSupport(treclVwOutputPortsPort.getViewer()));

      treclVwOutputPortsTask.setEditingSupport(
          new TaskEditingSupport(treclVwOutputPortsTask.getViewer(),
              ((RuminaqDiagram) getDiagramTypeProvider().getDiagram())
                  .getMainTask(),
              t));
    }
  }

  private final class Notify extends Disposable {
    private TreeViewerColumn treclVwOutputPortsNotify;
    private TreeViewerColumn treclVwOutputPortsLoop;

    private Notify(TreeViewer treVwOutputPorts) {
      treclVwOutputPortsNotify = new TreeViewerColumn(treVwOutputPorts,
          SWT.NONE);
      treclVwOutputPortsLoop = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsNotify.getColumn().setAlignment(SWT.CENTER);
      treclVwOutputPortsNotify.getColumn().setText("Ticks");
      treclVwOutputPortsLoop.getColumn().setText("Loop");
      treclVwOutputPortsNotify.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          return Optional.of(element).filter(Synchronization.class::isInstance)
              .map(Synchronization.class::cast)
              .map(Synchronization::getWaitForTicks).orElse("");
        }
      });
      treclVwOutputPortsLoop.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public void update(ViewerCell cell) {
          TreeItem item = (TreeItem) cell.getItem();
          Button button;
          if (buttons.containsKey(cell.getElement())) {
            button = buttons.get(cell.getElement());
          }

          if (cell.getElement() instanceof InternalOutputPort) {
            button = new Button((Composite) cell.getViewerRow().getControl(),
                SWT.CHECK);
            button.setData(((InternalOutputPort) cell.getElement()).isLoop());
            TreeEditor editor = new TreeEditor(item.getParent());
            editor.grabHorizontal = true;
            editor.grabVertical = true;
            editor.setEditor(button, item, cell.getColumnIndex());
            editor.layout();
          } else if (cell.getElement() instanceof Synchronization) {
            Synchronization s = (Synchronization) cell.getElement();
            if (s.getParent().getSynchronization().lastIndexOf(
                cell.getElement()) == s.getParent().getSynchronization().size()
                    - 1) {
              button = new Button((Composite) cell.getViewerRow().getControl(),
                  SWT.CHECK);
              button.setData(s.isLoop());
              TreeEditor editor = new TreeEditor(item.getParent());
              editor.grabHorizontal = true;
              editor.grabVertical = true;
              editor.setEditor(button, item, cell.getColumnIndex());
              editor.layout();
            }
          }
        }
      });
      treclVwOutputPortsNotify.setEditingSupport(
          new SkipEditingSupport(treclVwOutputPortsNotify.getViewer()) {

            @Override
            protected Object getValue(Synchronization synchronization) {
              return synchronization.getWaitForTicks();
            }

            @Override
            protected void setValue(Synchronization synchronization,
                Object value) {
              ModelUtil.runModelChange(() -> {
                synchronization.setWaitForTicks((String) value);
                treVwOutputPorts.refresh();
                treclVwOutputPortsNotify.getColumn().pack();
              }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                  "Change");
            }
          });
      treclVwOutputPortsLoop.setEditingSupport(
          new NotifyLoopEditingSupport(treclVwOutputPortsLoop.getViewer()));
    }
  }

  private final class Skip extends Disposable {
    private TreeViewerColumn treclVwOutputPortsSkip;
    private TreeViewerColumn treclVwOutputPortsLoop;

    private Skip() {
      treclVwOutputPortsSkip = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsSkip.getColumn().setAlignment(SWT.CENTER);
      treclVwOutputPortsLoop = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsSkip.getColumn().setText("Skips");
      treclVwOutputPortsLoop.getColumn().setText("Loop");
      treclVwOutputPortsSkip.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          return Optional.of(element).filter(Synchronization.class::isInstance)
              .map(Synchronization.class::cast)
              .map(Synchronization::getSkipFirst).orElse("");
        }
      });
      treclVwOutputPortsLoop.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public void update(ViewerCell cell) {
          TreeItem item = (TreeItem) cell.getItem();
          Optional.of(cell.getElement())
              .filter(Synchronization.class::isInstance)
              .map(Synchronization.class::cast)
              .ifPresent((Synchronization s) -> {
                Button button = Optional.of(cell.getElement())
                    .filter(buttons::containsKey).map(buttons::get)
                    .orElseGet(() -> {
                      Button b = new Button(
                          (Composite) cell.getViewerRow().getControl(),
                          SWT.CHECK);
                      b.setData(s.isSkipLoop());
                      buttons.put(cell.getElement(), b);
                      return b;
                    });
                TreeEditor editor = new TreeEditor(item.getParent());
                editor.grabHorizontal = true;
                editor.grabVertical = true;
                editor.setEditor(button, item, cell.getColumnIndex());
                editor.layout();
              });
        }
      });
      treclVwOutputPortsSkip.setEditingSupport(
          new SkipEditingSupport(treclVwOutputPortsSkip.getViewer()));
      treclVwOutputPortsLoop.setEditingSupport(
          new SkipLoopEditingSupport(treclVwOutputPortsLoop.getViewer()));
    }
  }

  private final class Up extends Disposable {
    private TreeViewerColumn treclVwOutputPortsUp;

    private Up(TreeViewer treVwOutputPorts) {
      treclVwOutputPortsUp = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsUp.getColumn().setText(" ");
      treclVwOutputPortsUp.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public void update(ViewerCell cell) {
          TreeItem item = (TreeItem) cell.getItem();
          Optional.of(cell.getElement())
              .filter(Synchronization.class::isInstance)
              .map(Synchronization.class::cast).ifPresent(s -> {
                Button button = Optional.of(cell.getElement())
                    .filter(buttons::containsKey).map(buttons::get)
                    .orElseGet(() -> {
                      Button b = new Button(
                          (Composite) cell.getViewerRow().getControl(),
                          SWT.PUSH);
                      b.setData(s.isSkipLoop());
                      buttons.put(cell.getElement(), b);
                      return b;
                    });
                button.setText("\u21E7");
                TreeEditor editor = new TreeEditor(item.getParent());
                editor.grabHorizontal = true;
                editor.grabVertical = true;
                editor.setEditor(button, item, cell.getColumnIndex());
                editor.layout();
              });
        }
      });
    }
  }

  private final class Down extends Disposable {
    private TreeViewerColumn treclVwOutputPortsDown;

    private Down(TreeViewer treVwOutputPorts) {
      treclVwOutputPortsDown = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsDown.getColumn().setText(" ");
      treclVwOutputPortsDown.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public void update(ViewerCell cell) {
          TreeItem item = (TreeItem) cell.getItem();
          Optional.of(cell.getElement())
              .filter(Synchronization.class::isInstance)
              .map(Synchronization.class::cast)
              .ifPresent((Synchronization s) -> {
                Button button = Optional.of(cell.getElement())
                    .filter(buttons::containsKey).map(buttons::get)
                    .orElseGet(() -> {
                      Button b = new Button(
                          (Composite) cell.getViewerRow().getControl(),
                          SWT.PUSH);
                      b.setData(s.isSkipLoop());
                      buttons.put(cell.getElement(), b);
                      return b;
                    });
                button.setText("\u21E9");
                TreeEditor editor = new TreeEditor(item.getParent());
                editor.grabHorizontal = true;
                editor.grabVertical = true;
                editor.setEditor(button, item, cell.getColumnIndex());
                editor.layout();
              });
        }
      });
    }
  }

  private final class Reset {
    private TreeViewerColumn treclVwOutputResetTask;
    private TreeViewerColumn treclVwOutputResetPort;

    private Reset(TreeViewer treVwOutputPorts) {
      treclVwOutputResetTask = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputResetPort = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputResetTask.getColumn().setText("Reset Task");
      treclVwOutputResetPort.getColumn().setText("Reset Port");
      treclVwOutputResetTask.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          return Optional.of(element)
              .filter(InternalOutputPort.class::isInstance)
              .map(InternalOutputPort.class::cast)
              .map(op -> op.getResetPort() == null ? NONE
                  : op.getResetPort().getTask().getId())
              .orElse("");
        }
      });
      treclVwOutputResetPort.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          return Optional.of(element)
              .filter(InternalOutputPort.class::isInstance)
              .map(InternalOutputPort.class::cast)
              .map(op -> op.getResetPort() == null ? NONE
                  : op.getResetPort() instanceof InternalOutputPort
                      ? OUT + op.getResetPort().getId()
                      : IN + op.getResetPort().getId())
              .orElse("");
        }
      });
    }

    public void refresh(Task t) {
      treclVwOutputResetPort.setEditingSupport(
          new ResetPortEditingSupport(treclVwOutputResetPort.getViewer()));
      treclVwOutputResetTask.setEditingSupport(
          new ResetTaskEditingSupport(treclVwOutputResetTask.getViewer(),
              ((RuminaqDiagram) getDiagramTypeProvider().getDiagram())
                  .getMainTask(),
              t));
    }
  }

  private final class AddRemove extends Disposable {
    private TreeViewerColumn treclVwOutputPortsBtn;

    private AddRemove(TreeViewer treVwOutputPorts) {
      treclVwOutputPortsBtn = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
      treclVwOutputPortsBtn.getColumn().setText(" ");
      treclVwOutputPortsBtn.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public void update(ViewerCell cell) {
          TreeItem item = (TreeItem) cell.getItem();
          Button button = Optional.of(cell.getElement())
              .filter(buttons::containsKey).map(buttons::get).orElseGet(() -> {
                Button b = new Button(
                    (Composite) cell.getViewerRow().getControl(), SWT.NONE);
                return Optional.of(cell.getElement())
                    .filter(InternalOutputPort.class::isInstance)
                    .map(InternalOutputPort.class::cast)
                    .map(iop -> addNewSynchronizationButton(iop, b))
                    .orElseGet(() -> Optional.of(cell.getElement())
                        .filter(Synchronization.class::isInstance)
                        .map(Synchronization.class::cast)
                        .map(s -> removeSynchronizationButton(s, b))
                        .orElseThrow());
              });
          buttons.put(cell.getElement(), button);
          TreeEditor editor = new TreeEditor(item.getParent());
          editor.grabHorizontal = true;
          editor.grabVertical = true;
          editor.setEditor(button, item, cell.getColumnIndex());
          editor.layout();
          treclVwOutputPortsBtn.getColumn().pack();

        }

        private Button addNewSynchronizationButton(InternalOutputPort iop,
            Button b) {
          b.setText("+");
          b.addSelectionListener(
              (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
                Synchronization s = RuminaqFactory.eINSTANCE
                    .createSynchronization();
                ModelUtil.runModelChange(() -> {
                  if (iop.getSynchronization().size() != 0)
                    iop.getSynchronization()
                        .get(iop.getSynchronization().size() - 1)
                        .setLoop(false);
                  if (iop.getSynchronization().size() != 0)
                    iop.getSynchronization()
                        .get(iop.getSynchronization().size() - 1)
                        .setSkipLoop(false);
                  iop.getSynchronization().add(s);
                }, getDiagramContainer().getDiagramBehavior()
                    .getEditingDomain(), "Add Synchronization");
                Stream.of(treOutputPorts.getItems())
                    .filter(it -> it.getData() == iop).findFirst()
                    .ifPresent(it -> {
                      new TreeItem(it, SWT.NONE).setData(s);
                      it.setExpanded(true);
                    });
                refresh();
              });
          return b;
        }

        private Button removeSynchronizationButton(Synchronization s,
            Button b) {
          b.setText("-");
          b.addSelectionListener(
              (WidgetSelectedSelectionListener) (SelectionEvent evt) -> {
                ModelUtil.runModelChange(
                    () -> s.getParent().getSynchronization().remove(s),
                    getDiagramContainer().getDiagramBehavior()
                        .getEditingDomain(),
                    "Remove synchronization");
                disposeAll(s);
                refresh();
              });
          return b;
        }
      });
    }
  }

  private final class SynchronizationContentProvider
      implements ITreeContentProvider {

    @Override
    public void dispose() {
      buttons.values().stream().flatMap(Collection::stream)
          .forEach(Button::dispose);
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getChildren(Object o) {
      return Optional.of(o).filter(InternalOutputPort.class::isInstance)
          .map(InternalOutputPort.class::cast)
          .map(InternalOutputPort::getSynchronization).map(List::stream)
          .orElseGet(Stream::empty).toArray(Synchronization[]::new);
    }

    @Override
    public Object[] getElements(Object o) {
      return Optional.of(o).filter(EObjectContainmentEList.class::isInstance)
          .map(EObjectContainmentEList.class::cast)
          .map(EObjectContainmentEList<InternalOutputPort>::stream)
          .orElseGet(Stream::empty).toArray(InternalOutputPort[]::new);
    }

    @Override
    public Object getParent(Object paramObject) {
      return null;
    }

    @Override
    public boolean hasChildren(Object o) {
      return Optional.of(o).filter(InternalOutputPort.class::isInstance)
          .map(InternalOutputPort.class::cast)
          .map(InternalOutputPort::getSynchronization).map(l -> !l.isEmpty())
          .orElse(Boolean.FALSE);
    }
  }

  private abstract class AbstractSynchronizationEditingSupport
      extends EditingSupport {

    public AbstractSynchronizationEditingSupport(ColumnViewer viewer) {
      super(viewer);
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
      return null;
    }

    @Override
    protected boolean canEdit(Object element) {
      return Optional.of(element).filter(Synchronization.class::isInstance)
          .isPresent();
    }

    @Override
    protected Object getValue(Object element) {
      return Optional.ofNullable(element)
          .filter(Synchronization.class::isInstance)
          .map(Synchronization.class::cast).map(this::getValue).orElse(NONE);
    }

    protected abstract Object getValue(Synchronization synchronization);

    @Override
    protected void setValue(Object element, Object value) {
      Optional.of(element).filter(Synchronization.class::isInstance)
          .map(Synchronization.class::cast).ifPresent(s -> setValue(s, value));
    }

    protected abstract void setValue(Synchronization synchronization,
        Object value);
  }

  private final class GroupEditingSupport
      extends AbstractSynchronizationEditingSupport {
    private TextCellEditor cellEditor;

    private GroupEditingSupport(ColumnViewer viewer) {
      super(viewer);
      cellEditor = new TextCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      return cellEditor;
    }

    @Override
    protected Object getValue(Synchronization synchronization) {
      return "" + synchronization.getGroup();
    }

    @Override
    protected void setValue(Synchronization synchronization, Object value) {
      if (!NumericUtil.isOneDimPositiveInteger((String) value))
        return;
      ModelUtil.runModelChange(() -> {
        synchronization.setGroup(Integer.parseInt((String) value));
        treVwOutputPorts.refresh();
      }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
          "Change");
    }
  }

  private final class TaskEditingSupport
      extends AbstractSynchronizationEditingSupport {
    private ComboBoxViewerCellEditor cellEditor;
    private MainTask mt;

    private TaskEditingSupport(ColumnViewer viewer, MainTask mt,
        final Task thisTask) {
      super(viewer);
      this.mt = mt;
      cellEditor = new ComboBoxViewerCellEditor(
          (Composite) getViewer().getControl(), SWT.READ_ONLY);
      cellEditor.setLabelProvider(new LabelProvider());
      cellEditor.setContentProvider(new ArrayContentProvider());
      int size = mt.getTask().size() <= 1 ? 1 : mt.getTask().size();
      String[] inputs = new String[size];
      int k = 0;
      inputs[k++] = NONE;
      for (int i = 1; i <= mt.getTask().size(); i++) {
        if (mt.getTask().get(i - 1) == thisTask)
          continue;
        inputs[k++] = mt.getTask().get(i - 1).getId();
      }
      cellEditor.setInput(inputs);
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      return cellEditor;
    }

    @Override
    protected Object getValue(Synchronization synchronization) {
      return Optional.ofNullable(synchronization)
          .map(Synchronization::getWaitForPort).map(InternalPort::getTask)
          .map(Task::getId).orElse("");
    }

    @Override
    protected void setValue(Synchronization synchronization, Object value) {
      ModelUtil.runModelChange(() -> {
        String newValue = (String) value;
        if (NONE.equals(newValue))
          synchronization.setWaitForPort(null);
        else {
          for (Task t : mt.getTask()) {
            if (t.getId().equals(newValue)) {
              if (t.getOutputPort().size() > 0)
                synchronization.setWaitForPort(t.getOutputPort().get(0));
              else if (t.getInputPort().size() > 0)
                synchronization.setWaitForPort(t.getInputPort().get(0));
            }
          }
        }
        treVwOutputPorts.refresh();
        Stream.of(treOutputPorts.getColumns()).forEach(TreeColumn::pack);
        root.layout();
      }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
          "Change");
    }
  }

  private class PortEditingSupport
      extends AbstractSynchronizationEditingSupport {
    private PortEditingSupport(ColumnViewer viewer) {
      super(viewer);
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      if (o instanceof Synchronization) {
        ComboBoxViewerCellEditor cellEditor = new ComboBoxViewerCellEditor(
            (Composite) getViewer().getControl(), SWT.READ_ONLY);
        Synchronization s = (Synchronization) o;
        cellEditor.setLabelProvider(new LabelProvider());
        cellEditor.setContentProvider(new ArrayContentProvider());
        if (s.getWaitForPort() != null) {
          Task t = s.getWaitForPort().getTask();
          int size = t.getInputPort().size() + t.getOutputPort().size();
          if (size > 0) {
            String[] inputs = new String[size];
            int k = 0;
            for (int i = 0; i < t.getInputPort().size(); i++)
              inputs[k++] = IN + t.getInputPort().get(i).getId();
            for (int i = 0; i < t.getOutputPort().size(); i++)
              inputs[k++] = OUT + t.getOutputPort().get(i).getId();
            cellEditor.setInput(inputs);
            return cellEditor;
          }
        }

        String[] inputs = new String[1];
        inputs[0] = NONE;
        cellEditor.setInput(inputs);

        return cellEditor;
      }
      return null;
    }

    @Override
    protected Object getValue(Synchronization synchronization) {
      return synchronization.getWaitForPort() instanceof InternalOutputPort
          ? OUT + synchronization.getWaitForPort().getId()
          : IN + synchronization.getWaitForPort().getId();
    }

    @Override
    protected void setValue(Synchronization synchronization, Object value) {
      ModelUtil.runModelChange(() -> {
        String newValue = (String) value;

        if (newValue.startsWith(IN)) {
          for (InternalInputPort ip : synchronization.getWaitForPort().getTask()
              .getInputPort())
            if (ip.getId().equals(newValue.substring(IN.length())))
              synchronization.setWaitForPort(ip);

        } else if (newValue.startsWith(OUT)) {
          for (InternalOutputPort op : synchronization.getWaitForPort()
              .getTask().getOutputPort())
            if (op.getId().equals(newValue.substring(OUT.length())))
              synchronization.setWaitForPort(op);
        }

        treVwOutputPorts.refresh();
        Stream.of(treOutputPorts.getColumns()).forEach(TreeColumn::pack);
        root.layout();
      }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
          "Change");
    }
  }

  private class ResetTaskEditingSupport extends EditingSupport {
    private ComboBoxViewerCellEditor cellEditor;
    private MainTask mt;

    private ResetTaskEditingSupport(ColumnViewer viewer, MainTask mt,
        final Task thisTask) {
      super(viewer);
      this.mt = mt;
      cellEditor = new ComboBoxViewerCellEditor(
          (Composite) getViewer().getControl(), SWT.READ_ONLY);
      cellEditor.setLabelProvider(new LabelProvider());
      cellEditor.setContentProvider(new ArrayContentProvider());
      int size = mt.getTask().size() <= 1 ? 1 : mt.getTask().size();
      String[] inputs = new String[size];
      int k = 0;
      inputs[k++] = NONE;
      for (int i = 1; i <= mt.getTask().size(); i++) {
        if (mt.getTask().get(i - 1) == thisTask)
          continue;
        inputs[k++] = mt.getTask().get(i - 1).getId();
      }
      cellEditor.setInput(inputs);
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      return cellEditor;
    }

    @Override
    protected boolean canEdit(Object o) {
      return o instanceof InternalOutputPort;
    }

    @Override
    protected Object getValue(Object o) {
      if (o instanceof InternalOutputPort) {
        InternalOutputPort el = (InternalOutputPort) o;
        return el.getResetPort() != null ? el.getResetPort().getTask().getId()
            : NONE;
      }
      return null;
    }

    @Override
    protected void setValue(Object o, final Object value) {
      if (o instanceof InternalOutputPort) {
        final InternalOutputPort op = (InternalOutputPort) o;
        ModelUtil.runModelChange(() -> {
          String newValue = (String) value;
          if (NONE.equals(newValue))
            op.setResetPort(null);
          else {
            for (Task t : mt.getTask()) {
              if (t.getId().equals(newValue)) {
                if (t.getOutputPort().size() > 0)
                  op.setResetPort(t.getOutputPort().get(0));
                else if (t.getInputPort().size() > 0)
                  op.setResetPort(t.getInputPort().get(0));
              }
            }
          }
          treVwOutputPorts.refresh();
          Stream.of(treOutputPorts.getColumns()).forEach(TreeColumn::pack);
          root.layout();
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      }
    }
  }

  private final class ResetPortEditingSupport extends EditingSupport {
    private ResetPortEditingSupport(ColumnViewer viewer) {
      super(viewer);
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      if (o instanceof InternalOutputPort) {
        ComboBoxViewerCellEditor cellEditor = new ComboBoxViewerCellEditor(
            (Composite) getViewer().getControl(), SWT.READ_ONLY);
        InternalOutputPort el = (InternalOutputPort) o;
        cellEditor.setLabelProvider(new LabelProvider());
        cellEditor.setContentProvider(new ArrayContentProvider());
        if (el.getResetPort() != null) {
          Task t = el.getResetPort().getTask();
          int size = t.getInputPort().size() + t.getOutputPort().size();
          if (size > 0) {
            String[] inputs = new String[size];
            int k = 0;
            for (int i = 0; i < t.getInputPort().size(); i++)
              inputs[k++] = IN + t.getInputPort().get(i).getId();
            for (int i = 0; i < t.getOutputPort().size(); i++)
              inputs[k++] = OUT + t.getOutputPort().get(i).getId();
            cellEditor.setInput(inputs);
            return cellEditor;
          }
        }

        String[] inputs = new String[1];
        inputs[0] = NONE;
        cellEditor.setInput(inputs);

        return cellEditor;
      }
      return null;
    }

    @Override
    protected boolean canEdit(Object o) {
      return Optional.of(o).filter(InternalOutputPort.class::isInstance)
          .isPresent();
    }

    @Override
    protected Object getValue(Object o) {
      if (o instanceof InternalOutputPort) {
        InternalOutputPort el = (InternalOutputPort) o;
        return el.getResetPort() == null ? NONE
            : el.getResetPort() instanceof InternalOutputPort
                ? OUT + el.getResetPort().getId()
                : IN + el.getResetPort().getId();
      }
      return null;
    }

    @Override
    protected void setValue(Object o, final Object value) {
      if (o instanceof InternalOutputPort) {
        final InternalOutputPort el = (InternalOutputPort) o;
        ModelUtil.runModelChange(() -> {
          String newValue = (String) value;

          if (newValue.startsWith(IN))
            for (InternalInputPort ip : el.getResetPort().getTask()
                .getInputPort())
              if (ip.getId().equals(newValue.substring(IN.length())))
                el.setResetPort(ip);

              else if (newValue.startsWith(OUT))
                for (InternalOutputPort op : el.getResetPort().getTask()
                    .getOutputPort())
                  if (op.getId().equals(newValue.substring(OUT.length())))
                    el.setResetPort(op);

          treVwOutputPorts.refresh();
          Stream.of(treOutputPorts.getColumns()).forEach(TreeColumn::pack);
          root.layout();
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      }
    }
  }

  private class SkipEditingSupport
      extends AbstractSynchronizationEditingSupport {
    private TextCellEditor cellEditor;

    private SkipEditingSupport(ColumnViewer viewer) {
      super(viewer);
      cellEditor = new TextCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      return cellEditor;
    }

    @Override
    protected Object getValue(Synchronization synchronization) {
      return synchronization.getSkipFirst();
    }

    @Override
    protected void setValue(Synchronization synchronization, Object value) {
      ModelUtil.runModelChange(() -> {
        synchronization.setSkipFirst((String) value);
        treVwOutputPorts.refresh();
        Stream.of(treOutputPorts.getColumns()).forEach(TreeColumn::pack);
      }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
          "Change");
    }
  }

  private final class NotifyLoopEditingSupport extends EditingSupport {
    private CheckboxCellEditor cellEditor;

    private NotifyLoopEditingSupport(ColumnViewer viewer) {
      super(viewer);
      cellEditor = new CheckboxCellEditor((Composite) getViewer().getControl(),
          SWT.CHECK);
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      return cellEditor;
    }

    @Override
    protected boolean canEdit(Object o) {
      return true;
    }

    @Override
    protected Object getValue(Object o) {
      if (o instanceof InternalOutputPort) {
        InternalOutputPort op = (InternalOutputPort) o;
        return op.isLoop();
      } else if (o instanceof Synchronization) {
        Synchronization s = (Synchronization) o;
        return s.isLoop();
      }
      return null;
    }

    @Override
    protected void setValue(Object o, Object value) {
      final boolean boolValue = (Boolean) value;
      if (o instanceof InternalOutputPort) {
        final InternalOutputPort op = (InternalOutputPort) o;
        ModelUtil.runModelChange(() -> {
          op.setLoop(boolValue);
          if (boolValue)
            op.getSynchronization().stream().forEach(s -> s.setLoop(false));
          treVwOutputPorts.refresh();
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      } else if (o instanceof Synchronization) {
        final Synchronization s = (Synchronization) o;
        if (s.getParent().getSynchronization()
            .lastIndexOf(o) != s.getParent().getSynchronization().size() - 1)
          return;
        ModelUtil.runModelChange(() -> {
          s.setLoop(boolValue);
          if (boolValue)
            s.getParent().setLoop(false);
          treVwOutputPorts.refresh();
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      }
    }
  }

  private final class SkipLoopEditingSupport extends EditingSupport {
    private CheckboxCellEditor cellEditor;

    private SkipLoopEditingSupport(ColumnViewer viewer) {
      super(viewer);
      cellEditor = new CheckboxCellEditor((Composite) getViewer().getControl(),
          SWT.CHECK);
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      return cellEditor;
    }

    @Override
    protected boolean canEdit(Object o) {
      return true;
    }

    @Override
    protected Object getValue(Object o) {
      if (o instanceof Synchronization) {
        Synchronization s = (Synchronization) o;
        return s.isSkipLoop();
      }
      return null;
    }

    @Override
    protected void setValue(Object o, Object value) {
      final boolean boolValue = (Boolean) value;
      if (o instanceof InternalOutputPort) {
        final InternalOutputPort op = (InternalOutputPort) o;
        ModelUtil.runModelChange(() -> {
          op.setLoop(boolValue);
          if (boolValue)
            for (Synchronization s : op.getSynchronization())
              s.setSkipLoop(false);
          treVwOutputPorts.refresh();
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      } else if (o instanceof Synchronization) {
        final Synchronization s = (Synchronization) o;
        ModelUtil.runModelChange(() -> {
          s.setSkipLoop(boolValue);
          treVwOutputPorts.refresh();
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      }
    }

  }

  /**
   * Layout.
   *
   * <pre>
   * ______________________________________________________________
   * | Output Port | Group | Sync Task | Sync Port | Ticks | Loop | ...
   * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * |             |       |           |           |       |      | ...
   * |             |       |           |           |       |      | ...
   * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   *     ______________________________________________________
   * ... | Skips | Loop |   |   | Reset Task | Reset Port |   |
   *     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * ... |       |      | ^ | v |            |            | + |
   * ... |       |      |   |   |            |            | - |
   *     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   * </pre>
   */
  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initComponents();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    ((GridData) parent.getLayoutData()).horizontalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessHorizontalSpace = true;
    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(1, false));
    treOutputPorts = new Tree(root, SWT.FULL_SELECTION);
    treOutputPorts
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
    treVwOutputPorts = new TreeViewer(treOutputPorts);
    new PortName(treVwOutputPorts);
    group = new Group(treVwOutputPorts);
    syncTaskPort = new SyncTaskPort(treVwOutputPorts);
    notify = new Notify(treVwOutputPorts);
    skip = new Skip();
    up = new Up(treVwOutputPorts);
    down = new Down(treVwOutputPorts);
    reset = new Reset(treVwOutputPorts);
    addRemove = new AddRemove(treVwOutputPorts);
  }

  private void initComponents() {
    treOutputPorts.setHeaderVisible(true);
    treOutputPorts.setLinesVisible(true);
    treVwOutputPorts.setContentProvider(new SynchronizationContentProvider());
    Stream.of(treOutputPorts.getColumns()).forEach(TreeColumn::pack);
  }

  @Override
  public void refresh() {
    if (treOutputPorts.isDisposed())
      return;
    treOutputPorts.removeAll();
    taskFrom(getSelectedPictogramElement()).ifPresent((Task t) -> {
      treVwOutputPorts.setInput(t.getOutputPort());
      group.refresh();
      syncTaskPort.refresh(t);
      reset.refresh(t);
    });
    Stream.of(treVwOutputPorts.getTree().getItems())
        .forEach(ti -> ti.setExpanded(true));
    treVwOutputPorts.refresh();
    Stream.of(treOutputPorts.getColumns()).forEach(TreeColumn::pack);
    root.layout();
  }

  private void disposeAll(EObject o) {
    Stream.of(treOutputPorts.getItems()).map(TreeItem::getItems)
        .flatMap(Stream::of).filter(it -> it.getData().equals(o))
        .forEach(TreeItem::dispose);
    notify.dispose(o);
    skip.dispose(o);
    up.dispose(o);
    down.dispose(o);
    addRemove.dispose(o);
  }
}
