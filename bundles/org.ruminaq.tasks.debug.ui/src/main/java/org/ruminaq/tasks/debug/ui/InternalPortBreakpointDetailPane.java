package org.ruminaq.tasks.debug.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.IDetailPane3;
import org.eclipse.jdt.internal.debug.ui.breakpoints.AbstractJavaBreakpointEditor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.ruminaq.tasks.debug.ui.InternalPortBreakpointEditor.Properties;

@SuppressWarnings("restriction")
public class InternalPortBreakpointDetailPane implements IDetailPane3 {

  public static final String DETAIL_PANE_INTERNAL_PORT_BREAKPOINT = "DETAIL_PANE_INTERNAL_PORT_BREAKPOINT";

  private InternalPortBreakpointEditor editor;
  private Composite editorParent;
  private IWorkbenchPartSite site;

  private String name;
  private String description;
  private String id;

  private Set<Integer> properties = new HashSet<Integer>();
  private ListenerList listeners = new ListenerList();

  public InternalPortBreakpointDetailPane() {
    this.name = "X";
    this.description = "Y";
    this.id = "Z";

    properties.add(Properties.PROP_HIT_COUNT_ENABLED.ordinal());
    properties.add(Properties.PROP_HIT_COUNT.ordinal());
    properties.add(Properties.PROP_SUSPEND_POLICY.ordinal());
  }

  @Override
  public void addPropertyListener(IPropertyListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removePropertyListener(IPropertyListener listener) {
    listeners.remove(listener);
  }

  public ISelectionProvider getSelectionProvider() {
    return null;
  }

  protected void firePropertyChange(int property) {
    Object[] ls = listeners.getListeners();
    for (int i = 0; i < ls.length; i++)
      ((IPropertyListener) ls[i]).propertyChanged(this, property);
  }

  @Override
  public void init(IWorkbenchPartSite partSite) {
    site = partSite;
  }

  @Override
  public String getID() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void dispose() {
    editor = null;
    site = null;
    listeners.clear();
    properties.clear();
    editorParent.dispose();
  }

  @Override
  public Control createControl(Composite parent) {
    editorParent = SWTFactory.createComposite(parent, parent.getFont(), 1, 1,
        GridData.FILL_BOTH, 0, 0);
    editorParent.setBackground(
        parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    editor = new InternalPortBreakpointEditor();
    editor.setMnemonics(false);
    editor.addPropertyListener(new IPropertyListener() {
      public void propertyChanged(Object source, int propId) {
        if (properties.contains(new Integer(propId))) {
          try {
            editor.doSave();
            return;
          } catch (CoreException e) {
          }
        }
        firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
      }
    });

    return editor.createControl(editorParent);
  }

  @Override
  public void doSaveAs() {
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public boolean isSaveOnCloseNeeded() {
    return isDirty() && editor.getStatus().isOK();
  }

  @Override
  public boolean setFocus() {
    return false;
  }

  @Override
  public void doSave(IProgressMonitor arg0) {
    IStatusLineManager statusLine = getStatusLine();
    if (statusLine != null)
      statusLine.setErrorMessage(null);
    try {
      editor.doSave();
    } catch (CoreException e) {
    }
  }

  private IStatusLineManager getStatusLine() {
    if (site instanceof IViewSite) {
      IViewSite s = (IViewSite) site;
      IWorkbenchPage page = s.getPage();
      IWorkbenchPart activePart = page.getActivePart();
      if (activePart instanceof IViewPart) {
        IViewPart activeViewPart = (IViewPart) activePart;
        IViewSite activeViewSite = activeViewPart.getViewSite();
        return activeViewSite.getActionBars().getStatusLineManager();
      }
      if (activePart instanceof IEditorPart) {
        IEditorPart activeEditorPart = (IEditorPart) activePart;
        IEditorActionBarContributor contributor = activeEditorPart
            .getEditorSite().getActionBarContributor();
        if (contributor instanceof EditorActionBarContributor)
          return ((EditorActionBarContributor) contributor).getActionBars()
              .getStatusLineManager();
      }
      return s.getActionBars().getStatusLineManager();
    }
    return null;
  }

  @Override
  public boolean isDirty() {
    return editor != null && editor.isDirty();
  }

  protected AbstractJavaBreakpointEditor getEditor() {
    return editor;
  }

  @Override
  public void display(IStructuredSelection selection) {
    IStatusLineManager statusLine = getStatusLine();
    if (statusLine != null)
      statusLine.setErrorMessage(null);
    AbstractJavaBreakpointEditor editor = getEditor();
    Object input = null;
    if (selection != null && selection.size() == 1)
      input = selection.getFirstElement();
    try {
      editor.setInput(input);
    } catch (CoreException e) {
    }
  }
}
