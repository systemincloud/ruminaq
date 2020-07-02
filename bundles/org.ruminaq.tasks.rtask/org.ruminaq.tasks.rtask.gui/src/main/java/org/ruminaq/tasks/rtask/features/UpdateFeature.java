/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.features;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.statet.ltk.core.LTK;
import org.eclipse.statet.ltk.model.core.ISourceUnitManager;
import org.eclipse.statet.r.core.model.IRModelInfo;
import org.eclipse.statet.r.core.model.IRModelManager;
import org.eclipse.statet.r.core.model.IRWorkspaceSourceUnit;
import org.eclipse.statet.r.core.model.RModel;
import org.ruminaq.gui.features.update.UpdateUserDefinedTaskFeature;
import org.ruminaq.model.ruminaq.UserDefinedTask;
import org.ruminaq.tasks.rtask.AddFeatureImpl;
import org.ruminaq.tasks.rtask.EclipseExtensionImpl;
import org.ruminaq.tasks.rtask.model.rtask.RTask;
import org.ruminaq.util.EclipseUtil;

//import de.walware.ecommons.ltk.ISourceUnitManager;
//import de.walware.ecommons.ltk.LTK;
//import de.walware.ecommons.ltk.ast.IAstNode;
//import de.walware.statet.r.core.model.IRLangSourceElement;
//import de.walware.statet.r.core.model.IRModelInfo;
//import de.walware.statet.r.core.model.IRModelManager;
//import de.walware.statet.r.core.model.IRWorkspaceSourceUnit;
//import de.walware.statet.r.core.model.RElementAccess;
//import de.walware.statet.r.core.model.RModel;
//import de.walware.statet.r.core.rsource.ast.Assignment;
//import de.walware.statet.r.core.rsource.ast.Block;
//import de.walware.statet.r.core.rsource.ast.FCall;
//import de.walware.statet.r.core.rsource.ast.FCall.Arg;
//import de.walware.statet.r.core.rsource.ast.FCall.Args;
//import de.walware.statet.r.core.rsource.ast.FDef;
//import de.walware.statet.r.core.rsource.ast.RAstNode;
//import de.walware.statet.r.core.rsource.ast.StringConst;
//import de.walware.statet.r.core.rsource.ast.Symbol;

public class UpdateFeature extends UpdateUserDefinedTaskFeature {

//    private IRLangSourceElement se = null;
  private String desc = AddFeatureImpl.AddFeature.NOT_CHOSEN;

  public UpdateFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof RTask);
  }

  @Override
  protected String getResource(Object bo) {
    RTask be = (RTask) bo;
    return be.getImplementation();
  }

  @Override
  public boolean load(String path) {
    if ("".equals(path) || (!path.startsWith(EclipseExtensionImpl.MAIN_R)
        && !path.startsWith(EclipseExtensionImpl.TEST_R)))
      return false;
    IProject p = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(EclipseUtil.getProjectNameFromDiagram(getDiagram()));

    this.desc = new Path(path).lastSegment();

    IFile file = p.getFile(path);

    if (file == null)
      return false;
    if (file.getFileExtension() == null)
      return false;

    String ext = file.getFileExtension();
    if (ext.equals("R")) {
      try {
        ISourceUnitManager sum = LTK.getSourceUnitManager();
        IContentDescription contentDescription = file.getContentDescription();
        IContentType contentType = contentDescription.getContentType();
        IRWorkspaceSourceUnit su = (IRWorkspaceSourceUnit) sum.getSourceUnit(
            LTK.PERSISTENCE_CONTEXT, file, contentType, true, null);
        IRModelInfo mi = (IRModelInfo) su.getModelInfo(RModel.R_TYPE_ID,
            IRModelManager.MODEL_FILE, new NullProgressMonitor());
//                this.se = mi.getSourceElement();
        return true;
      } catch (CoreException e) {
      }
    }
    return false;
  }

  @Override
  protected void loadIconDesc() {
    this.iconDesc = desc;
  }
//
//    protected Args getPublic() {
//        IAstNode node = (IAstNode) this.se.getAdapter(IAstNode.class);
//        for(int i = 0; i < node.getChildCount(); i++) {
//            IAstNode el = node.getChild(i);
//            if(el instanceof Assignment) {
//                Assignment a = (Assignment) el;
//                RAstNode right = a.getRightChild();
//                if(right instanceof FCall) {
//                    FCall rightCall = (FCall) right;
//                    for(Object rightAttachment : rightCall.getAttachments())
//                        if(rightAttachment instanceof RElementAccess)
//                            if("R6Class".equals(((RElementAccess) rightAttachment).getSegmentName())) {
//                                Args args = rightCall.getArgsChild();
//                                for(Arg arg : args.getChildren())
//                                    for(RAstNode ac : arg.getChildren())
//                                        if(ac instanceof Symbol && "public".equals(ac.getText()) && arg.getValueChild() instanceof FCall) {
//                                              for(Object c : ((FCall) arg.getValueChild()).getAttachments()) {
//                                                  if(c instanceof RElementAccess) {
//                                                        RElementAccess rea = (RElementAccess) c;
//                                                        FCall fcall = (FCall) rea.getNode();
//                                                        return fcall.getArgsChild();
//                                                  }
//                                              }
//                                        }
//                            }
//                }
//            }
//        }
//        return null;
//    }

//    protected FDef getInitializePorts() {
//        Args publicScope = getPublic();
//        for(Arg a : publicScope.getChildren()) {
//            RAstNode v = a.getValueChild();
//            if(v instanceof FDef)
//                if("initialize_ports".equals(a.getNameChild().getText())) return (FDef) v;
//        }
//        return null;
//    }
//
//    protected Args getRTaskInfo() {
//        Args publicScope = getPublic();
//        if(publicScope != null)
//            for(Arg a : publicScope.getChildren()) {
//            	RAstNode cn = a.getNameChild();
//            	if(cn == null) continue;
//            	if("rtaskinfo".equals(cn.getText())) {
//            		RAstNode v = a.getValueChild();
//            		if(v instanceof FCall) {
//            			FCall fcall = (FCall) v;
//            			return fcall.getArgsChild();
//            		}
//            	}
//            }
//        return null;
//    }
//
//
//
//    @Override
//    protected void loadInputPorts() {
//        FDef ip = getInitializePorts();
//        for(RAstNode n1 : ip.getChildren()) {
//            if(n1 instanceof Block) {
//                for(RAstNode n2 : n1.getChildren()) {
//                    if(n2 instanceof Assignment) {
//                        Assignment as = (Assignment) n2;
//                        RAstNode c = as.getRightChild();
//                        for(Object at : c.getAttachments()) {
//                            if(at instanceof RElementAccess) {
//                                RElementAccess rea = (RElementAccess) at;
//                                if("InputPort".equals(rea.getSegmentName())) {
//                                    String   name         = null;
//                                    String[] dataType     = new String[] { "Control" };
//                                    boolean  asynchronous = false;
//                                    int      group        = -1;
//                                    boolean  hold         = false;
//                                    int      queue        = 1;
//
//                                    RAstNode n3 = rea.getNode();
//                                    if(n3 instanceof FCall) {
//                                        FCall f = (FCall) n3;
//                                        for(Arg a : f.getArgsChild().getChildren()) {
//                                            switch(a.getNameChild().getText()) {
//                                                case "name"         : name = a.getValueChild().getText(); break;
//                                                case "data_type"    :
//                                                    RAstNode vc = a.getValueChild();
//                                                    if(vc instanceof StringConst) dataType = new String[] { vc.getText() };
//                                                    else if(vc instanceof FCall) {
//                                                        FCall vcf = (FCall) vc;
//                                                        Arg[] vcfc = vcf.getArgsChild().getChildren();
//                                                        dataType = new String[vcfc.length];
//                                                        for(int i = 0; i < dataType.length; i++)
//                                                            dataType[i] = vcfc[i].getValueChild().getText();
//                                                    }
//                                                    break;
//                                                case "asynchronous" : asynchronous = Boolean.parseBoolean(a.getValueChild().getText());	break;
//                                                case "group"        : group        = Integer.parseInt    (a.getValueChild().getText()); break;
//                                                case "hold"         : hold         = Boolean.parseBoolean(a.getValueChild().getText()); break;
//                                                case "queue"        : queue        = Integer.parseInt    (a.getValueChild().getText()); break;
//                                            }
//
//                                        }
//                                    }
//                                    List<DataType> dts = new LinkedList<>();
//                                    for(String d : dataType) {
//                                        DataType tmp = DataTypeManager.INSTANCE.getDataTypeFromName(d);
//                                        if(tmp != null) dts.add(tmp);
//                                    }
//                                    String queueSize = queue  == -1 ? Constants.INF : Integer.toString(queue);
//                                    inputs.add(new FileInternalInputPort(name, dts, asynchronous, group, hold, queueSize));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void loadOutputPorts() {
//        FDef ip = getInitializePorts();
//        for(RAstNode n1 : ip.getChildren()) {
//            if(n1 instanceof Block) {
//                for(RAstNode n2 : n1.getChildren()) {
//                    if(n2 instanceof Assignment) {
//                        Assignment as = (Assignment) n2;
//                        RAstNode c = as.getRightChild();
//                        for(Object at : c.getAttachments()) {
//                            if(at instanceof RElementAccess) {
//                                RElementAccess rea = (RElementAccess) at;
//                                if("OutputPort".equals(rea.getSegmentName())) {
//                                    String   name     = null;
//                                    String[] dataType = new String[] { "Control" };
//
//                                    RAstNode n3 = rea.getNode();
//                                    if(n3 instanceof FCall) {
//                                        FCall f = (FCall) n3;
//                                        for(Arg a : f.getArgsChild().getChildren()) {
//                                            switch(a.getNameChild().getText()) {
//                                                case "name"         : name = a.getValueChild().getText(); break;
//                                                case "data_type"    :
//                                                    RAstNode vc = a.getValueChild();
//                                                    if(vc instanceof StringConst) dataType = new String[] { vc.getText() };
//                                                    else if(vc instanceof FCall) {
//                                                        FCall vcf = (FCall) vc;
//                                                        Arg[] vcfc = vcf.getArgsChild().getChildren();
//                                                        dataType = new String[vcfc.length];
//                                                        for(int i = 0; i < dataType.length; i++)
//                                                            dataType[i] = vcfc[i].getValueChild().getText();
//                                                    }
//                                                    break;
//                                            }
//
//                                        }
//                                    }
//                                    List<DataType> dts = new LinkedList<>();
//                                    for(String d : dataType) {
//                                        DataType tmp = DataTypeManager.INSTANCE.getDataTypeFromName(d);
//                                        if(tmp != null) dts.add(tmp);
//                                    }
//                                    outputs.add(new FileInternalOutputPort(name, dts));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void loadAtomic() {
//        Args args = getRTaskInfo();
//        for(RAstNode c : args.getChildren()) {
//            if(c instanceof Arg) {
//                Arg arg = (Arg) c;
//                if("atomic".equals(arg.getNameChild().getText())) {
//                    this.atomic = Boolean.parseBoolean(arg.getValueChild().getText());
//                    return;
//                }
//            }
//        }
//        this.atomic = true;
//    }
//
//    @Override
//    protected void loadOnlyLocal() {
//        Args args = getRTaskInfo();
//        for(RAstNode c : args.getChildren()) {
//            if(c instanceof Arg) {
//                Arg arg = (Arg) c;
//                if("only_local".equals(arg.getNameChild().getText())) {
//                    this.onlyLocal = Boolean.parseBoolean(arg.getValueChild().getText());
//                    return;
//                }
//            }
//        }
//        this.onlyLocal = false;
//    }

  @Override
  protected Map<String, String> getParameters(UserDefinedTask udt) {
    final Map<String, String> ret = new HashMap<>();
    return ret;
  }

  @Override
  protected void loadInputPorts() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void loadOutputPorts() {
    // TODO Auto-generated method stub

  }

  @Override
  protected void loadAtomic() {
    // TODO Auto-generated method stub

  }

}
