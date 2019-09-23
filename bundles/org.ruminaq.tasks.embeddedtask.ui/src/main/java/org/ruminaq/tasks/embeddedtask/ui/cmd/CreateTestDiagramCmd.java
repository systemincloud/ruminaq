package org.ruminaq.tasks.embeddedtask.ui.cmd;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class CreateTestDiagramCmd extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ISelection selection = HandlerUtil.getCurrentSelection(event);
    if (selection != null & selection instanceof IStructuredSelection) {
      IStructuredSelection strucSelection = (IStructuredSelection) selection;
      IResource file = (IResource) strucSelection.getFirstElement();
      try {
        new TestDiagramGenerator().generateTestDiagram(file);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

}
