package org.ruminaq.tasks.pythontask.util;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;
import org.python.pydev.ast.codecompletion.revisited.modules.SourceModule;
import org.python.pydev.core.IInfo;
import org.python.pydev.core.IModule;
import org.python.pydev.core.IModulesManager;
import org.python.pydev.parser.jython.SimpleNode;
import org.python.pydev.parser.jython.ast.ClassDef;
import org.python.pydev.parser.jython.ast.Name;
import org.python.pydev.parser.jython.ast.decoratorsType;
import org.python.pydev.parser.visitors.scope.ASTEntry;
import org.python.pydev.parser.visitors.scope.EasyASTIteratorVisitor;
import org.python.pydev.plugin.nature.PythonNature;

import com.python.pydev.analysis.actions.GlobalsTwoPanelElementSelector2;
import com.python.pydev.analysis.actions.MatchHelper;
import com.python.pydev.analysis.additionalinfo.AdditionalInfoAndIInfo;

public class SicGlobalsTwoPanelElementSelector2 extends GlobalsTwoPanelElementSelector2 {

    private PythonNature    nature;
    private IModulesManager modulesManager;

    public SicGlobalsTwoPanelElementSelector2(Shell shell, boolean multi, String selectedText, IProject p) {
        super(shell, multi, selectedText);
        this.nature = PythonNature.getPythonNature(p);
        this.modulesManager = nature.getAstManager().getModulesManager();
    }

    @Override
    protected ItemsFilter createFilter() {
        return new InfoFilter() {
            @Override
            public boolean matchItem(Object item) {
                if(!(item instanceof AdditionalInfoAndIInfo)) return false;
                AdditionalInfoAndIInfo info = (AdditionalInfoAndIInfo) item;
                if(info.info.getType() != IInfo.CLASS_WITH_IMPORT_TYPE) return false;
                if(MatchHelper.matchItem(patternMatcher, info.info)) {
                    IModule module = modulesManager.getModule(info.info.getDeclaringModuleName(), nature, true);
                    if(module != null && module instanceof SourceModule) {
                        SourceModule sourceModule = (SourceModule) module;
                        SimpleNode ast = sourceModule.getAst();
                        EasyASTIteratorVisitor visitor = EasyASTIteratorVisitor.create(ast);
                        Iterator<ASTEntry> it = visitor.getClassesAndMethodsIterator();
                        while(it.hasNext()){
                            ASTEntry entry = it.next();
                            if(entry.node instanceof ClassDef){
                                ClassDef classDef = (ClassDef) entry.node;
                                decoratorsType[] ds = classDef.decs;
                                if(ds == null) continue;
                                for(decoratorsType d : ds)
                                    if(d.func instanceof Name && ((Name) d.func).id.equals("PythonTaskInfo"))
                                        return true;
                            }
                        }
                    }
                }
                return false;
            }
        };
    }
}
