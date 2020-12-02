/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.pythontask.gui.features;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.services.Graphiti;
import org.python.pydev.ast.codecompletion.revisited.CompletionCache;
import org.python.pydev.ast.item_pointer.ItemPointer;
import org.python.pydev.core.ICodeCompletionASTManager;
import org.python.pydev.core.IInfo;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.core.IPythonNature;
import org.python.pydev.editor.actions.PyOpenAction;
import org.python.pydev.plugin.nature.PythonNature;
import org.python.pydev.plugin.nature.SystemPythonNature;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.tasks.pythontask.gui.util.FindPythonTask;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import com.python.pydev.analysis.AnalysisPlugin;
import com.python.pydev.analysis.additionalinfo.AdditionalInfoAndIInfo;
import com.python.pydev.analysis.additionalinfo.AdditionalProjectInterpreterInfo;
import com.python.pydev.analysis.additionalinfo.AdditionalSystemInterpreterInfo;

public class DoubleClickFeature extends AbstractCustomFeature {

  public DoubleClickFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canExecute(ICustomContext context) {
    return true;
  }

  @Override
  public boolean hasDoneChanges() {
    return false;
  }

  @Override
  public void execute(ICustomContext context) {
    PythonTask bo = null;
    for (Object o : Graphiti.getLinkService()
        .getAllBusinessObjectsForLinkedPictogramElement(
            context.getInnerPictogramElement()))
      if (o instanceof PythonTask) {
        bo = (PythonTask) o;
        break;
      }
    if (bo == null)
      return;
    String pclass = bo.getImplementation();
    if (pclass.equals(""))
      return;

    IProject p = EclipseUtil.getProjectOf(getDiagram());
    PythonNature pNature = PythonNature.getPythonNature(p);
    IInterpreterManager manager = pNature.getRelatedInterpreterManager();
    List<IPythonNature> natures = PythonNature
        .getPythonNaturesRelatedTo(manager.getInterpreterType());
    AdditionalInfoAndIInfo pinfo = FindPythonTask.INSTANCE.find(p, pclass);

    if (pinfo != null) {
      IInfo entry;
      try {
        if (pinfo.additionalInfo instanceof AdditionalProjectInterpreterInfo) {
          AdditionalProjectInterpreterInfo projectInterpreterInfo = (AdditionalProjectInterpreterInfo) pinfo.additionalInfo;
          IProject project = projectInterpreterInfo.getProject();
          PythonNature pythonNature = PythonNature.getPythonNature(project);
          if (pythonNature != null) {
            natures = new ArrayList<IPythonNature>();
            natures.add(pythonNature);
          }

        } else if (pinfo.additionalInfo instanceof AdditionalSystemInterpreterInfo) {
          AdditionalSystemInterpreterInfo systemInterpreterInfo = (AdditionalSystemInterpreterInfo) pinfo.additionalInfo;
          SystemPythonNature pythonNature = new SystemPythonNature(
              systemInterpreterInfo.getManager());
          natures = new ArrayList<IPythonNature>();
          natures.add(pythonNature);
        }
      } catch (Throwable e) {
        return;
      }
      entry = pinfo.info;

      List<ItemPointer> pointers = new ArrayList<ItemPointer>();

      CompletionCache completionCache = new CompletionCache();
      for (IPythonNature pythonNature : natures) {
        ICodeCompletionASTManager astManager = pythonNature.getAstManager();
        if (astManager == null)
          continue;
        AnalysisPlugin.getDefinitionFromIInfo(pointers, astManager,
            pythonNature, entry, completionCache, false, false);
        if (pointers.size() > 0) {
          new PyOpenAction().run(pointers.get(0));
          break;
        }
      }
    }
  }
}
