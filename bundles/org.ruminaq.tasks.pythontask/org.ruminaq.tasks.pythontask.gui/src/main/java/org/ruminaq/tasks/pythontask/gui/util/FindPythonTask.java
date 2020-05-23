package org.ruminaq.tasks.pythontask.gui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.dialogs.SearchPattern;
import org.python.pydev.core.ICodeCompletionASTManager;
import org.python.pydev.core.IInfo;
import org.python.pydev.core.IInterpreterInfo;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.core.IModulesManager;
import org.python.pydev.core.IPythonNature;
import org.python.pydev.core.MisconfigurationException;
import org.python.pydev.core.ModulesKey;
import org.python.pydev.plugin.nature.PythonNature;

import com.python.pydev.analysis.actions.MatchHelper;
import com.python.pydev.analysis.additionalinfo.AbstractAdditionalDependencyInfo;
import com.python.pydev.analysis.additionalinfo.AbstractAdditionalTokensInfo;
import com.python.pydev.analysis.additionalinfo.AdditionalInfoAndIInfo;
import com.python.pydev.analysis.additionalinfo.AdditionalProjectInterpreterInfo;
import com.python.pydev.analysis.additionalinfo.AdditionalSystemInterpreterInfo;
import com.python.pydev.analysis.additionalinfo.ModInfo;

public enum FindPythonTask {
  INSTANCE;

  public AdditionalInfoAndIInfo find(IProject p, String className) {
    List<AbstractAdditionalTokensInfo> additionalInfo = getInfos(p);

    List<AdditionalInfoAndIInfo> contentProvider = new LinkedList<>();

    for (AbstractAdditionalTokensInfo ai : additionalInfo) {
      Collection<IInfo> allTokens = new HashSet<IInfo>(ai.getAllTokens());
      for (IInfo iInfo : allTokens)
        contentProvider.add(new AdditionalInfoAndIInfo(ai, iInfo));

      IModulesManager modulesManager = null;
      try {
        if (ai instanceof AdditionalProjectInterpreterInfo) {
          AdditionalProjectInterpreterInfo projectInterpreterInfo = (AdditionalProjectInterpreterInfo) ai;
          IProject project = projectInterpreterInfo.getProject();
          PythonNature nature = PythonNature.getPythonNature(project);
          if (nature != null) {
            ICodeCompletionASTManager astManager = nature.getAstManager();
            if (astManager != null)
              modulesManager = astManager.getModulesManager();
          }
        } else if (ai instanceof AdditionalSystemInterpreterInfo) {
          AdditionalSystemInterpreterInfo systemInterpreterInfo = (AdditionalSystemInterpreterInfo) ai;
          IInterpreterInfo defaultInterpreterInfo = systemInterpreterInfo
              .getManager().getDefaultInterpreterInfo(false);
          modulesManager = defaultInterpreterInfo.getModulesManager();
        }
      } catch (Throwable e) {
      }

      if (modulesManager != null) {
        SortedMap<ModulesKey, ModulesKey> allDirectModulesStartingWith = modulesManager
            .getAllDirectModulesStartingWith("");
        Collection<ModulesKey> values = allDirectModulesStartingWith.values();
        for (ModulesKey modulesKey : values) {
          contentProvider.add(new AdditionalInfoAndIInfo(ai,
              new ModInfo(modulesKey.name, null, null, 0, 0)));
        }
      }
    }

    SearchPattern headPattern = new SearchPattern();
    headPattern.setPattern(className);

    AdditionalInfoAndIInfo result = null;
    for (AdditionalInfoAndIInfo ai : contentProvider)
      if (MatchHelper.matchItem(headPattern, ai.info)) {
        result = ai;
      }
    return result;
  }

  public List<AbstractAdditionalTokensInfo> getInfos(IProject p) {
    PythonNature pNature = PythonNature.getPythonNature(p);
    IInterpreterManager manager = pNature.getRelatedInterpreterManager();
    AbstractAdditionalTokensInfo additionalSystemInfo = null;
    try {
      additionalSystemInfo = AdditionalSystemInterpreterInfo
          .getAdditionalSystemInfo(manager,
              manager.getDefaultInterpreterInfo(true).getExecutableOrJar());
    } catch (MisconfigurationException e) {
    }

    List<AbstractAdditionalTokensInfo> additionalInfo = new ArrayList<AbstractAdditionalTokensInfo>();
    additionalInfo.add(additionalSystemInfo);

    List<IPythonNature> natures = PythonNature
        .getPythonNaturesRelatedTo(manager.getInterpreterType());
    for (IPythonNature nature : natures) {
      AbstractAdditionalDependencyInfo info;
      try {
        info = AdditionalProjectInterpreterInfo
            .getAdditionalInfoForProject(nature);
        if (info != null)
          additionalInfo.add(info);
      } catch (MisconfigurationException e) {
      }
    }
    return additionalInfo;
  }
}
