/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.util;

//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.Platform;
//import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IStatusLineManager;
//import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

//import org.ruminaq.consts.SicConstants.SicPlugin;
//import org.ruminaq.eclipse.Activator;

public class ErrorUtils {

//	public static void throwCoreException(String message) throws CoreException {
//		IStatus status = new Status(IStatus.ERROR,	SicPlugin.ECLIPSE_ID.s(), IStatus.OK, message, null);
//		Platform.getLog(Activator.getDefault().getBundle()).log(status);
//		throw new CoreException(status);
//	}
//
//	public static void showErrorWithLogging(IStatus status){
//		Platform.getLog(Activator.getDefault().getBundle()).log(status);
//		ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "An error occured", null, status);
//	}

  public static void showErrorMessage(final String msg) {
    Display.getDefault().asyncExec(new Runnable() {

      @Override
      public void run() {
        IWorkbench wb = PlatformUI.getWorkbench();
        IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        if (win == null)
          return;
        IWorkbenchPage page = win.getActivePage();
        if (page == null)
          return;
        IWorkbenchPart part = page.getActivePart();
        if (part == null)
          return;

        IActionBars actionBars = null;
        IWorkbenchPartSite site = part.getSite();
        if (site instanceof IViewSite)
          actionBars = ((IViewSite) site).getActionBars();
        else if (site instanceof IEditorSite)
          actionBars = ((IEditorSite) site).getActionBars();

        if (actionBars == null)
          return;

        IStatusLineManager statusLineManager = actionBars
            .getStatusLineManager();
        if (statusLineManager == null)
          return;

        statusLineManager.setErrorMessage(msg);
        statusLineManager.markDirty();
        statusLineManager.update(true);
      }
    });
  }

}
