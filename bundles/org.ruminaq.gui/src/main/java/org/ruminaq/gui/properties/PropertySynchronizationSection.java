/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.gui.model.diagram.RuminaqShape;
import org.ruminaq.gui.model.diagram.TaskShape;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.ruminaq.Synchronization;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.util.NumericUtil;

public class PropertySynchronizationSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private static final String NONE = "-";
  private static final String IN = "[IN] ";
  private static final String OUT = "[OUT] ";

  private static final Image CHECKED = getImage("checked.gif");
  private static final Image UNCHECKED = getImage("unchecked.gif");

  private Composite root;

  private TreeViewer treVwOutputPorts;

  private Tree treOutputPorts;
  private TreeViewerColumn treclVwOutputPortsName;
  private TreeViewerColumn treclVwOutputPortsGrp;
  private TreeViewerColumn treclVwOutputPortsTask;
  private TreeViewerColumn treclVwOutputPortsPort;
  private TreeViewerColumn treclVwOutputPortsNotify;
  private TreeViewerColumn treclVwOutputPortsNLoop;
  private TreeViewerColumn treclVwOutputPortsSkip;
  private TreeViewerColumn treclVwOutputPortsSLoop;
  private TreeViewerColumn treclVwOutputPortsUp;
  private TreeViewerColumn treclVwOutputPortsDown;
  private TreeViewerColumn treclVwOutputResetTask;
  private TreeViewerColumn treclVwOutputResetPort;
  private TreeViewerColumn treclVwOutputPortsBtn;

  private GroupEditingSupport treclEdOutputPortsGrp;
  private TaskEditingSupport treclEdOutputPortsTask;
  private PortEditingSupport treclEdOutputPortsPort;
  private NotifyEditingSupport treclEdOutputPortsNotify;
  private NotifyLoopEditingSupport treclEdOutputPortsNLoop;
  private SkipEditingSupport treclEdOutputPortsSkip;
  private SkipLoopEditingSupport treclEdOutputPortsSLoop;
  private ResetTaskEditingSupport treclEdOutputResetTask;
  private ResetPortEditingSupport treclEdOutputResetPort;

  private Map<Object, Button> buttons = new HashMap<>();

  private static Image getImage(String file) {
    Bundle bundle = FrameworkUtil
        .getBundle(PropertySynchronizationSection.class);
    URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
    ImageDescriptor image = ImageDescriptor.createFromURL(url);
    return image.createImage();
  }

  private static Optional<TaskShape> shapeFrom(PictogramElement pe) {
    return Optional.ofNullable(pe).filter(TaskShape.class::isInstance)
        .map(TaskShape.class::cast);
  }

  private static Optional<Task> taskFrom(PictogramElement pe) {
    return shapeFrom(pe).map(TaskShape::getModelObject)
        .filter(Task.class::isInstance).map(Task.class::cast);
  }

  private final class SynchronizationContentProvider
      implements ITreeContentProvider {

    @Override
    public void dispose() {
      buttons.values().stream().forEach(Button::dispose);
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @Override
    public Object[] getChildren(Object o) {
      return ((InternalOutputPort) o).getSynchronization()
          .toArray(new Synchronization[((InternalOutputPort) o)
              .getSynchronization().size()]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object o) {
      return ((EObjectContainmentEList<InternalOutputPort>) o).toArray(
          new InternalOutputPort[((EObjectContainmentEList<InternalOutputPort>) o)
              .size()]);
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
          .orElse(false);
    }

  }

  private final static class TreeLabelProvider extends LabelProvider
      implements ITableLabelProvider {
    @Override
    public String getColumnText(Object o, int columnIndex) {
      if (o instanceof InternalOutputPort) {
        InternalOutputPort op = (InternalOutputPort) o;
        switch (columnIndex) {
          case 0:
            return op.getId();
          case 10:
            return op.getResetPort() == null ? NONE
                : op.getResetPort().getTask().getId();
          case 11:
            return op.getResetPort() == null ? NONE
                : op.getResetPort() instanceof InternalOutputPort
                    ? OUT + op.getResetPort().getId()
                    : IN + op.getResetPort().getId();
          default:
            return "";
        }
      } else if (o instanceof Synchronization) {
        Synchronization s = (Synchronization) o;
        switch (columnIndex) {
          case 1:
            return s.getGroup() == 0 ? NONE : "" + s.getGroup();
          case 2:
            return s.getWaitForPort() == null ? NONE
                : s.getWaitForPort().getTask().getId();
          case 3:
            return s.getWaitForPort() == null ? NONE
                : s.getWaitForPort() instanceof InternalOutputPort
                    ? OUT + s.getWaitForPort().getId()
                    : IN + s.getWaitForPort().getId();
          case 4:
            return s.getWaitForTicks();
          case 6:
            return s.getSkipFirst();
          default:
            return "";
        }
      }
      return "";
    }

    @Override
    public Image getColumnImage(Object paramObject, int paramInt) {
      return null;
    }

    @Override
    public void addListener(ILabelProviderListener arg0) {
      // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
      // TODO Auto-generated method stub

    }

    @Override
    public boolean isLabelProperty(Object arg0, String arg1) {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public void removeListener(ILabelProviderListener arg0) {
      // TODO Auto-generated method stub

    }
  }

  private final class GroupEditingSupport extends EditingSupport {
    private TextCellEditor cellEditor = null;

    private GroupEditingSupport(ColumnViewer viewer) {
      super(viewer);
      cellEditor = new TextCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      return cellEditor;
    }

    @Override
    protected boolean canEdit(Object o) {
      return o instanceof Synchronization;
    }

    @Override
    protected Object getValue(Object o) {
      if (o instanceof Synchronization)
        return "" + ((Synchronization) o).getGroup();
      else
        return "";
    }

    @Override
    protected void setValue(Object o, final Object value) {
      if (o instanceof Synchronization) {
        if (!NumericUtil.isOneDimPositiveInteger((String) value))
          return;
        final Synchronization s = (Synchronization) o;
        ModelUtil.runModelChange(() -> {
          s.setGroup(Integer.parseInt((String) value));
          treVwOutputPorts.refresh();
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      }
    }
  }

  private final class TaskEditingSupport extends EditingSupport {
    private ComboBoxViewerCellEditor cellEditor = null;
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
    protected boolean canEdit(Object o) {
      return o instanceof Synchronization;
    }

    @Override
    protected Object getValue(Object o) {
      if (o instanceof Synchronization) {
        Synchronization s = (Synchronization) o;
        return s.getWaitForPort() != null ? s.getWaitForPort().getTask().getId()
            : NONE;
      }
      return null;
    }

    @Override
    protected void setValue(Object o, final Object value) {
      if (o instanceof Synchronization) {
        final Synchronization s = (Synchronization) o;
        ModelUtil.runModelChange(() -> {
          String newValue = (String) value;
          if (NONE.equals(newValue))
            s.setWaitForPort(null);
          else {
            for (Task t : mt.getTask()) {
              if (t.getId().equals(newValue)) {
                if (t.getOutputPort().size() > 0)
                  s.setWaitForPort(t.getOutputPort().get(0));
                else if (t.getInputPort().size() > 0)
                  s.setWaitForPort(t.getInputPort().get(0));
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

  private class PortEditingSupport extends EditingSupport {
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
    protected boolean canEdit(Object o) {
      return o instanceof Synchronization;
    }

    @Override
    protected Object getValue(Object o) {
      if (o instanceof Synchronization) {
        Synchronization s = (Synchronization) o;
        return s.getWaitForPort() == null ? NONE
            : s.getWaitForPort() instanceof InternalOutputPort
                ? OUT + s.getWaitForPort().getId()
                : IN + s.getWaitForPort().getId();
      }
      return null;
    }

    @Override
    protected void setValue(Object o, final Object value) {
      if (o instanceof Synchronization) {
        final Synchronization s = (Synchronization) o;
        ModelUtil.runModelChange(() -> {
          String newValue = (String) value;

          if (newValue.startsWith(IN)) {
            for (InternalInputPort ip : s.getWaitForPort().getTask()
                .getInputPort())
              if (ip.getId().equals(newValue.substring(IN.length())))
                s.setWaitForPort(ip);

          } else if (newValue.startsWith(OUT)) {
            for (InternalOutputPort op : s.getWaitForPort().getTask()
                .getOutputPort())
              if (op.getId().equals(newValue.substring(OUT.length())))
                s.setWaitForPort(op);
          }

          treVwOutputPorts.refresh();
          Stream.of(treOutputPorts.getColumns()).forEach(TreeColumn::pack);
          root.layout();
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      }
    }
  }

  private class ResetTaskEditingSupport extends EditingSupport {
    private ComboBoxViewerCellEditor cellEditor = null;
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

  private class ResetPortEditingSupport extends EditingSupport {
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
      return o instanceof InternalOutputPort;
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

  private class SkipEditingSupport extends EditingSupport {
    private TextCellEditor cellEditor = null;

    private SkipEditingSupport(ColumnViewer viewer) {
      super(viewer);
      cellEditor = new TextCellEditor((Composite) getViewer().getControl());
    }

    @Override
    protected CellEditor getCellEditor(Object o) {
      return cellEditor;
    }

    @Override
    protected boolean canEdit(Object o) {
      return o instanceof Synchronization;
    }

    @Override
    protected Object getValue(Object o) {
      if (o instanceof Synchronization)
        return get((Synchronization) o);
      else
        return null;
    }

    @Override
    protected void setValue(Object o, final Object value) {
      if (o instanceof Synchronization) {
        final Synchronization s = (Synchronization) o;
        ModelUtil.runModelChange(new Runnable() {
          @Override
          public void run() {
            set(s, (String) value);
            treVwOutputPorts.refresh();
            for (TreeColumn tc : treOutputPorts.getColumns())
              tc.pack();
          }
        }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
            "Change");
      }
    }

    protected Object get(Synchronization s) {
      return s.getSkipFirst();
    }

    protected void set(Synchronization s, String value) {
      s.setSkipFirst(value);
    }
  }

  private class NotifyEditingSupport extends SkipEditingSupport {
    private NotifyEditingSupport(ColumnViewer viewer) {
      super(viewer);
    }

    @Override
    protected Object get(Synchronization s) {
      return s.getWaitForTicks();
    }

    @Override
    protected void set(Synchronization s, String value) {
      s.setWaitForTicks(value);
    }
  }

  private final class NotifyLoopEditingSupport extends EditingSupport {
    private CheckboxCellEditor cellEditor = null;

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
            for (Synchronization s : op.getSynchronization())
              s.setLoop(false);
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
    private CheckboxCellEditor cellEditor = null;

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
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(1, false));

    treOutputPorts = new Tree(root, SWT.FULL_SELECTION);
    treOutputPorts
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
    treVwOutputPorts = new TreeViewer(treOutputPorts);

    treclVwOutputPortsName = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsGrp = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsGrp.getColumn().setAlignment(SWT.CENTER);
    treclVwOutputPortsTask = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsPort = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsNotify = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsNotify.getColumn().setAlignment(SWT.CENTER);
    treclVwOutputPortsNLoop = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsSkip = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsSkip.getColumn().setAlignment(SWT.CENTER);
    treclVwOutputPortsSLoop = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsUp = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsDown = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputResetTask = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputResetPort = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
    treclVwOutputPortsBtn = new TreeViewerColumn(treVwOutputPorts, SWT.NONE);
  }

  private void initActions() {
    treOutputPorts.addMouseListener(new MouseListener() {

      @Override
      public void mouseDoubleClick(MouseEvent arg0) {
      }

      @Override
      public void mouseDown(MouseEvent arg0) {
      }

      @Override
      public void mouseUp(MouseEvent arg0) {
        Point pt = new Point(arg0.x, arg0.y);
        ColumnViewer viewer = treclVwOutputPortsBtn.getViewer();
        TreeColumn column = treclVwOutputPortsBtn.getColumn();
        ViewerCell cell = viewer.getCell(pt);
        if (cell != null) {
          TreeColumn clickedColumn = treOutputPorts
              .getColumn(cell.getColumnIndex());

          if (clickedColumn == column) {
            TreeItem item = (TreeItem) cell.getItem();
            Object o = item.getData();

            if (o instanceof InternalOutputPort) {
              final InternalOutputPort iop = (InternalOutputPort) o;
              final Synchronization s = RuminaqFactory.eINSTANCE
                  .createSynchronization();
              ModelUtil.runModelChange(() -> {
                if (iop.getSynchronization().size() != 0)
                  iop.getSynchronization()
                      .get(iop.getSynchronization().size() - 1).setLoop(false);
                if (iop.getSynchronization().size() != 0)
                  iop.getSynchronization()
                      .get(iop.getSynchronization().size() - 1)
                      .setSkipLoop(false);

                s.setParent(iop);
                iop.getSynchronization().add(s);
              }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                  "Add Synchronization");
              for (TreeItem it : treOutputPorts.getItems()) {
                if (it.getData() == iop) {
                  new TreeItem(it, SWT.NONE).setData(s);
                  it.setExpanded(true);
                }
              }
              refresh();

            } else if (o instanceof Synchronization) {
              final Synchronization snc = (Synchronization) o;
              ModelUtil.runModelChange(
                  () -> snc.getParent().getSynchronization().remove(snc),
                  getDiagramContainer().getDiagramBehavior().getEditingDomain(),
                  "Remove synchronization");
              for (TreeItem it1 : treOutputPorts.getItems())
                for (TreeItem it2 : it1.getItems())
                  if (it2.getData() == snc)
                    it2.dispose();
              if (buttons.remove(snc) != null)
                buttons.remove(snc).dispose();
              refresh();
            }
          }
        }
      }
    });
  }

  private void initComponents() {
    treOutputPorts.setHeaderVisible(true);
    treOutputPorts.setLinesVisible(true);
    treclVwOutputPortsName.getColumn().setText("Output Port");
    treclVwOutputPortsGrp.getColumn().setText("Group");
    treclVwOutputPortsTask.getColumn().setText("Sync Task");
    treclVwOutputPortsPort.getColumn().setText("Sync Port");
    treclVwOutputPortsNotify.getColumn().setText("Ticks");
    treclVwOutputPortsNLoop.getColumn().setText("L");
    treclVwOutputPortsSkip.getColumn().setText("Skips");
    treclVwOutputPortsSLoop.getColumn().setText("L");
    treclVwOutputPortsUp.getColumn().setText("U");
    treclVwOutputPortsDown.getColumn().setText("D");
    treclVwOutputResetTask.getColumn().setText("Reset Task");
    treclVwOutputResetPort.getColumn().setText("Reset Port");
    treclVwOutputPortsBtn.getColumn().setText(" ");

    treVwOutputPorts.setContentProvider(new SynchronizationContentProvider());
    treVwOutputPorts.setLabelProvider(new TreeLabelProvider());

    treclVwOutputPortsNLoop.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object o) {
        return null;
      }

      @Override
      public Image getImage(Object o) {
        if (o instanceof InternalOutputPort) {
          if (((InternalOutputPort) o).isLoop())
            return CHECKED;
          else
            return UNCHECKED;
        } else if (o instanceof Synchronization) {
          Synchronization s = (Synchronization) o;
          if (s.getParent().getSynchronization().lastIndexOf(
              o) == s.getParent().getSynchronization().size() - 1) {
            if (s.isLoop())
              return CHECKED;
            else
              return UNCHECKED;
          } else
            return null;
        } else
          return null;
      }
    });

    treclVwOutputPortsSLoop.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object o) {
        return null;
      }

      @Override
      public Image getImage(Object o) {
        if (o instanceof Synchronization) {
          Synchronization s = (Synchronization) o;
          if (s.isSkipLoop())
            return CHECKED;
          else
            return UNCHECKED;
        } else
          return null;
      }
    });

    treclVwOutputPortsBtn.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object o) {
        if (o instanceof InternalOutputPort)
          return "+";
        else
          return "-";
      }
    });

    for (TreeColumn tc : treOutputPorts.getColumns())
      tc.pack();
  }

  private void addStyles() {
    root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh() {
    if (treOutputPorts.isDisposed())
      return;
    treOutputPorts.removeAll();
    taskFrom(getSelectedPictogramElement()).ifPresent((Task t) -> {
      treVwOutputPorts.setInput(t.getOutputPort());

      treclEdOutputPortsGrp = new GroupEditingSupport(
          treclVwOutputPortsGrp.getViewer());
      treclVwOutputPortsGrp.setEditingSupport(treclEdOutputPortsGrp);

      treclEdOutputPortsPort = new PortEditingSupport(
          treclVwOutputPortsPort.getViewer());
      treclVwOutputPortsPort.setEditingSupport(treclEdOutputPortsPort);

      treclEdOutputPortsTask = new TaskEditingSupport(
          treclVwOutputPortsTask.getViewer(),
          ((RuminaqDiagram) getDiagramTypeProvider().getDiagram())
              .getMainTask(),
          t);
      treclVwOutputPortsTask.setEditingSupport(treclEdOutputPortsTask);

      treclEdOutputPortsSkip = new SkipEditingSupport(
          treclVwOutputPortsSkip.getViewer());
      treclVwOutputPortsSkip.setEditingSupport(treclEdOutputPortsSkip);

      treclEdOutputPortsSLoop = new SkipLoopEditingSupport(
          treclVwOutputPortsSLoop.getViewer());
      treclVwOutputPortsSLoop.setEditingSupport(treclEdOutputPortsSLoop);

      treclEdOutputPortsNotify = new NotifyEditingSupport(
          treclVwOutputPortsNotify.getViewer());
      treclVwOutputPortsNotify.setEditingSupport(treclEdOutputPortsNotify);

      treclEdOutputPortsNLoop = new NotifyLoopEditingSupport(
          treclVwOutputPortsNLoop.getViewer());
      treclVwOutputPortsNLoop.setEditingSupport(treclEdOutputPortsNLoop);

      treclEdOutputResetPort = new ResetPortEditingSupport(
          treclVwOutputResetPort.getViewer());
      treclVwOutputResetPort.setEditingSupport(treclEdOutputResetPort);

      treclEdOutputResetTask = new ResetTaskEditingSupport(
          treclVwOutputResetTask.getViewer(),
          ((RuminaqDiagram) getDiagramTypeProvider().getDiagram())
              .getMainTask(),
          t);
      treclVwOutputResetTask.setEditingSupport(treclEdOutputResetTask);
    });
    for (TreeItem ti : treVwOutputPorts.getTree().getItems())
      ti.setExpanded(true);
    treVwOutputPorts.refresh();
    for (TreeColumn tc : treOutputPorts.getColumns())
      tc.pack();
    root.layout();
  }

  @Override
  public void setInput(IWorkbenchPart part, ISelection selection) {
    super.setInput(part, selection);
  }
}
