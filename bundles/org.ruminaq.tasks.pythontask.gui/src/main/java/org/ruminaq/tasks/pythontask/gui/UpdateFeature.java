/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.python.pydev.ast.codecompletion.revisited.modules.SourceModule;
import org.python.pydev.core.IModule;
import org.python.pydev.core.IModulesManager;
import org.python.pydev.parser.jython.SimpleNode;
import org.python.pydev.parser.jython.ast.Assign;
import org.python.pydev.parser.jython.ast.Call;
import org.python.pydev.parser.jython.ast.ClassDef;
import org.python.pydev.parser.jython.ast.FunctionDef;
import org.python.pydev.parser.jython.ast.Name;
import org.python.pydev.parser.jython.ast.NameTok;
import org.python.pydev.parser.jython.ast.Num;
import org.python.pydev.parser.jython.ast.Str;
import org.python.pydev.parser.jython.ast.decoratorsType;
import org.python.pydev.parser.jython.ast.keywordType;
import org.python.pydev.parser.jython.ast.stmtType;
import org.python.pydev.parser.visitors.scope.ASTEntry;
import org.python.pydev.parser.visitors.scope.EasyASTIteratorVisitor;
import org.python.pydev.plugin.nature.PythonNature;
import org.ruminaq.eclipse.wizards.task.AbstractCreateCustomTaskPage;
import org.ruminaq.gui.features.update.UpdateUserDefinedTaskFeature;
import org.ruminaq.model.DataTypeManager;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.UserDefinedTask;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import org.ruminaq.util.EclipseUtil;

public class UpdateFeature extends UpdateUserDefinedTaskFeature {

  private SourceModule sourceModule;
  private String desc = AddFeatureImpl.AddFeature.NOT_CHOSEN;

  public UpdateFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof PythonTask);
  }

  @Override
  protected String getResource(Object bo) {
    PythonTask be = (PythonTask) bo;
    return be.getImplementation();
  }

  @Override
  public boolean load(String className) {
    IProject p = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(EclipseUtil.getProjectNameFromDiagram(getDiagram()));
    PythonNature nature = PythonNature.getPythonNature(p);
    if (nature != null) {
      IModulesManager modulesManager = nature.getAstManager()
          .getModulesManager();
      IModule module = modulesManager.getModule(className, nature, true);
      if (module != null && module instanceof SourceModule) {
        SourceModule sourceModule = (SourceModule) module;
        SimpleNode ast = sourceModule.getAst();
        EasyASTIteratorVisitor visitor = EasyASTIteratorVisitor.create(ast);
        Iterator<ASTEntry> it = visitor.getClassesAndMethodsIterator();
        while (it.hasNext()) {
          ASTEntry entry = it.next();
          if (entry.node instanceof ClassDef) {
            ClassDef classDef = (ClassDef) entry.node;
            decoratorsType[] ds = classDef.decs;
            if (ds == null)
              continue;
            for (decoratorsType d : ds)
              if (d.func instanceof Name
                  && ((Name) d.func).id.equals("PythonTaskInfo"))
                this.sourceModule = sourceModule;
          }
        }
      }
    }
    int i = className.lastIndexOf(".");
    this.desc = this.sourceModule == null ? AddFeatureImpl.AddFeature.NOT_CHOSEN
        : className.substring(i != -1 ? i + 1 : 0);
    return this.sourceModule != null;
  }

  @Override
  protected void loadIconDesc() {
    this.iconDesc = desc;
  }

  @Override
  protected void loadInputPorts() {
    SimpleNode ast = sourceModule.getAst();
    EasyASTIteratorVisitor visitor = EasyASTIteratorVisitor.create(ast);
    Iterator<ASTEntry> it = visitor.getClassesAndMethodsIterator();
    while (it.hasNext()) {
      ASTEntry entry = it.next();
      if (entry.node instanceof FunctionDef) {
        FunctionDef functionDef = (FunctionDef) entry.node;
        if (functionDef.name instanceof NameTok
            && ((NameTok) functionDef.name).id.equals("__init_ports__")) {
          for (stmtType t : functionDef.body) {
            if (t instanceof Assign) {
              Assign a = (Assign) t;
              if (a.value instanceof Call) {
                Call c = (Call) a.value;
                if (c.func instanceof Name
                    && ((Name) c.func).id.equals("InputPort")) {
                  String name = null;
                  String[] dataType = new String[] { "Control" };
                  boolean asynchronous = false;
                  int group = -1;
                  boolean hold = false;
                  int queue = 1;

                  if (c.keywords != null)
                    for (keywordType k : c.keywords) {
                      if (k.arg instanceof NameTok
                          && ((NameTok) k.arg).id.equals("name"))
                        name = ((Str) k.value).s;
                      if (k.arg instanceof NameTok
                          && ((NameTok) k.arg).id.equals("data_type")) {
                        if (k.value instanceof Name) {
                          Name n = (Name) k.value;
                          dataType = new String[] { n.id };
                        } else if (k.value instanceof org.python.pydev.parser.jython.ast.List) {
                          org.python.pydev.parser.jython.ast.List l = (org.python.pydev.parser.jython.ast.List) k.value;
                          if (l.elts != null) {
                            dataType = new String[l.elts.length];
                            for (int i = 0; i < dataType.length; i++)
                              dataType[i] = ((Name) l.elts[i]).id;
                          }
                        }
                      }
                      if (k.arg instanceof NameTok
                          && ((NameTok) k.arg).id.equals("asynchronous"))
                        asynchronous = Boolean
                            .parseBoolean(((Name) k.value).id);
                      if (k.arg instanceof NameTok
                          && ((NameTok) k.arg).id.equals("group"))
                        group = Integer.parseInt(((Num) k.value).num);
                      if (k.arg instanceof NameTok
                          && ((NameTok) k.arg).id.equals("hold"))
                        hold = Boolean.parseBoolean(((Name) k.value).id);
                      if (k.arg instanceof NameTok
                          && ((NameTok) k.arg).id.equals("queue"))
                        queue = Integer.parseInt(((Num) k.value).num);
                    }

                  List<DataType> dts = new LinkedList<>();
                  for (String d : dataType) {
                    DataType tmp = DataTypeManager.INSTANCE
                        .getDataTypeFromName(d);
                    if (tmp != null)
                      dts.add(tmp);
                  }
                  String queueSize = queue == -1 ? AbstractCreateCustomTaskPage.INF
                      : Integer.toString(queue);
                  inputs.add(new FileInternalInputPort(name, dts, asynchronous,
                      group, hold, queueSize));
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  protected void loadOutputPorts() {
    SimpleNode ast = sourceModule.getAst();
    EasyASTIteratorVisitor visitor = EasyASTIteratorVisitor.create(ast);
    Iterator<ASTEntry> it = visitor.getClassesAndMethodsIterator();
    while (it.hasNext()) {
      ASTEntry entry = it.next();
      if (entry.node instanceof FunctionDef) {
        FunctionDef functionDef = (FunctionDef) entry.node;
        if (functionDef.name instanceof NameTok
            && ((NameTok) functionDef.name).id.equals("__init_ports__")) {
          for (stmtType t : functionDef.body) {
            if (t instanceof Assign) {
              Assign a = (Assign) t;
              if (a.value instanceof Call) {
                Call c = (Call) a.value;
                if (c.func instanceof Name
                    && ((Name) c.func).id.equals("OutputPort")) {
                  String name = null;
                  String[] dataType = new String[] { "Control" };

                  if (c.keywords != null)
                    for (keywordType k : c.keywords) {
                      if (k.arg instanceof NameTok
                          && ((NameTok) k.arg).id.equals("name"))
                        name = ((Str) k.value).s;
                      if (k.arg instanceof NameTok
                          && ((NameTok) k.arg).id.equals("data_type")) {
                        if (k.value instanceof Name) {
                          Name n = (Name) k.value;
                          dataType = new String[] { n.id };
                        } else if (k.value instanceof org.python.pydev.parser.jython.ast.List) {
                          org.python.pydev.parser.jython.ast.List l = (org.python.pydev.parser.jython.ast.List) k.value;
                          if (l.elts != null) {
                            dataType = new String[l.elts.length];
                            for (int i = 0; i < dataType.length; i++)
                              dataType[i] = ((Name) l.elts[i]).id;
                          }
                        }
                      }
                    }

                  List<DataType> dts = new LinkedList<>();
                  for (String d : dataType) {
                    DataType tmp = DataTypeManager.INSTANCE
                        .getDataTypeFromName(d);
                    if (tmp != null)
                      dts.add(tmp);
                  }
                  outputs.add(new FileInternalOutputPort(name, dts));
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  protected void loadAtomic() {
    SimpleNode ast = sourceModule.getAst();
    EasyASTIteratorVisitor visitor = EasyASTIteratorVisitor.create(ast);
    Iterator<ASTEntry> it = visitor.getClassesAndMethodsIterator();
    while (it.hasNext()) {
      ASTEntry entry = it.next();
      if (entry.node instanceof ClassDef) {
        ClassDef classDef = (ClassDef) entry.node;
        decoratorsType[] ds = classDef.decs;
        if (ds == null)
          continue;
        for (decoratorsType d : ds)
          if (d.func instanceof Name
              && ((Name) d.func).id.equals("PythonTaskInfo"))
            for (keywordType k : d.keywords)
              if ("atomic".equals(((NameTok) k.arg).id)) {
                this.atomic = "True".equals(((Name) k.value).id);
                return;
              }
      }
    }
    this.atomic = true;
  }

  @Override
  protected Map<String, String> getParameters(UserDefinedTask udt) {
    final Map<String, String> ret = new HashMap<>();
    SimpleNode ast = sourceModule.getAst();
    EasyASTIteratorVisitor visitor = EasyASTIteratorVisitor.create(ast);
    Iterator<ASTEntry> it = visitor.getClassesAndMethodsIterator();
    while (it.hasNext()) {
      ASTEntry entry = it.next();
      if (entry.node instanceof ClassDef) {
        ClassDef classDef = (ClassDef) entry.node;
        decoratorsType[] ds = classDef.decs;
        if (ds == null)
          continue;
        for (decoratorsType d : ds)
          if (d.func instanceof Name
              && ((Name) d.func).id.equals("SicParameter")) {
            String name = "";
            String defaultValue = "";
            for (keywordType k : d.keywords) {
              if ("name".equals(((NameTok) k.arg).id))
                name = ((Str) k.value).s;
              if ("default_value".equals(((NameTok) k.arg).id))
                defaultValue = ((Str) k.value).s;
            }
            ret.put(name, defaultValue);
          }
      }
    }
    return ret;
  }

}
