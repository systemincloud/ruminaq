package org.ruminaq.tasks.pythontask;

import org.apache.commons.cli.Options;
import org.ruminaq.model.sic.Task;
import org.ruminaq.tasks.pythontask.impl.PythonTaskI;
import org.ruminaq.tasks.pythontask.impl.jython.JythonProgramArguments;

public final class RunnerServiceProvider extends AbstractRunnerService {
    @Override public void  initModelPackages()                                { PythontaskPackage.eINSTANCE.getClass(); }
    @Override public TaskI getImplementation(EmbeddedTaskI parent, Task task) { return task instanceof PythonTask ? new PythonTaskI(parent, task) : null; }
    @Override public void  addOptions(Options options)                        { options.addOption(PythonTaskI.ATTR_PY_BIN,      true, "");
                                                                                options.addOption(PythonTaskI.ATTR_PY_ENV,      true, "");
                                                                                options.addOption(PythonTaskI.ATTR_PY_PATH,     true, "");
                                                                                options.addOption(PythonTaskI.ATTR_PY_EXT_LIBS, true, "");
                                                                                options.addOption(PythonTaskI.ATTR_PY_TYPE,     true, "");
                                                                                JythonProgramArguments .INSTANCE.addOptions(options); }
}
