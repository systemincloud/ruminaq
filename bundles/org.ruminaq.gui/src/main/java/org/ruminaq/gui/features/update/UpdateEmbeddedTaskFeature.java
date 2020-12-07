/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.eclipse.wizards.project.CreateSourceFolders;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AddEmbeddedTaskFeature;
import org.ruminaq.gui.image.Images;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.Connection;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.InternalOutputPort;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.OutputPort;
import org.ruminaq.model.ruminaq.Task;

/**
 * EmbeddedTask UpdateFeature.
 *
 * <p>It provides extraction of UserDefinedTask parameters from other diagram.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(UpdateEmbeddedTaskFeature.Filter.class)
public class UpdateEmbeddedTaskFeature
    extends AbstractUpdateUserDefinedTaskFeature {

  public static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return EmbeddedTask.class;
    }
  }

  private boolean updateNeededChecked = false;

  private boolean superUpdateNeeded = false;
  private boolean iconUpdateNeeded = false;

  private MainTask embeddedTask = null;
  private String desc = AddEmbeddedTaskFeature.NOT_CHOSEN;

  public UpdateEmbeddedTaskFeature(IFeatureProvider fp) {
    super(fp);
  }
  
  @Override
  protected String iconDesc() {
    return desc;
  }
  
  @Override
  protected List<FileInternalInputPort> inputPorts() {
    List<FileInternalInputPort> inputs = new LinkedList<>();
    for (InputPort ip : embeddedTask.getInputPort()) {
      List<DataType> dt = new LinkedList<>();
      for (Connection c : embeddedTask.getConnection()) {
        if (c.getSourceRef() == ip) {
          if (c.getTargetRef() instanceof InternalInputPort)
            loop: for (DataType dt2 : ((InternalInputPort) c.getTargetRef())
                .getDataType()) {
              for (DataType d : dt)
                if (EcoreUtil.equals(d, dt2))
                  continue loop;
              dt.add(EcoreUtil.copy(dt2));
            }
        }
      }

      inputs.add(new FileInternalInputPort(ip.getId(), dt, ip.isAsynchronous(),
          ip.getGroup(), ip.isHoldLast(), ip.getQueueSize()));
    }
    return inputs;
  }

  private boolean compareIcons(PictogramElement pe) {
    EmbeddedTask et = (EmbeddedTask) getBusinessObjectForPictogramElement(pe);

    String id = "";
    for (GraphicsAlgorithm ga : pe.getGraphicsAlgorithm()
        .getGraphicsAlgorithmChildren())
      if (ga instanceof Image) {
        id = ((Image) ga).getId();
        break;
      }

    boolean ret = true;
    if (et.getImplementationTask().startsWith(CreateSourceFolders.TEST_RESOURCES)
        && Images.IMG_EMBEDDEDTASK_DIAGRAM_MAIN.equals(id))
      return false;
    if (et.getImplementationTask().startsWith(CreateSourceFolders.MAIN_RESOURCES)
        && Images.IMG_EMBEDDEDTASK_DIAGRAM_TEST.equals(id))
      return false;

    return ret;
  }

  private boolean updateIcon(IUpdateContext context) {
    PictogramElement pe = context.getPictogramElement();
    EmbeddedTask et = (EmbeddedTask) getBusinessObjectForPictogramElement(pe);

    for (GraphicsAlgorithm ga : pe.getGraphicsAlgorithm()
        .getGraphicsAlgorithmChildren())
      if (ga instanceof Image) {
        if (et.getImplementationTask().startsWith(CreateSourceFolders.MAIN_RESOURCES))
          ((Image) ga).setId(Images.IMG_EMBEDDEDTASK_DIAGRAM_MAIN);
        if (et.getImplementationTask().startsWith(CreateSourceFolders.TEST_RESOURCES))
          ((Image) ga).setId(Images.IMG_EMBEDDEDTASK_DIAGRAM_TEST);
        return true;
      }
    return false;
  }

  @Override
  protected String getResource(Task task) {
    EmbeddedTask be = (EmbeddedTask) task;
    return be.getImplementationTask();
  }

  @Override
  public boolean load(String path) {
    if ("".equals(path) || (!path.startsWith(CreateSourceFolders.MAIN_RESOURCES)
        && !path.startsWith(CreateSourceFolders.TEST_RESOURCES)))
      return false;

    this.desc = new Path(path).lastSegment();

    ResourceSet resSet = new ResourceSetImpl();

    Resource resource = null;
    String tmp = EclipseUtil.getUriOf(getDiagram()).segment(0);
    try {
      resource = resSet.getResource(URI.createURI("/" + tmp + "/" + path),
          true);
    } catch (Exception e) {
    }
    if (resource == null)
      return false;

    if (resource.getContents().size() > 1)
      this.embeddedTask = (MainTask) resource.getContents().get(1);
    if (embeddedTask != null)
      return true;
    else
      return false;
  }

  @Override
  protected List<FileInternalOutputPort> outputPorts() {
    List<FileInternalOutputPort> outputs = new LinkedList<>();
    for (OutputPort op : embeddedTask.getOutputPort()) {
      List<DataType> dt = new LinkedList<>();
      for (Connection c : embeddedTask.getConnection()) {
        if (c.getTargetRef() == op) {
          if (c.getSourceRef() instanceof InternalOutputPort)
            loop: for (DataType dt2 : ((InternalOutputPort) c.getSourceRef())
                .getDataType()) {
              for (DataType d : dt)
                if (EcoreUtil.equals(d, dt2))
                  continue loop;
              dt.add(EcoreUtil.copy(dt2));
            }
        }
      }

      outputs.add(new FileInternalOutputPort(op.getId(), dt));
    }
    return outputs;
  }

  @Override
  protected boolean isAtomic() {
    return embeddedTask.isAtomic();
  }

  @Override
  protected Map<String, String> getParameters() {
    final Map<String, String> ret = new HashMap<>();
//    EmbeddedTask et = (EmbeddedTask) udt;
//    if (et != null) {
//      String path = et.getImplementationTask();
//      if ("".equals(path) || (!path.startsWith(SourceFolders.MAIN_RESOURCES)
//          && !path.startsWith(SourceFolders.TEST_RESOURCES)))
//        return ret;
//      String project = EclipseUtil.getModelPathFromEObject(et).segment(0);
//      IPath ipath = Path.fromOSString("/" + project + "/" + path);
//      IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(ipath);
//      String fullPath = file.getRawLocation().toOSString();
//      logger.trace("fullPath = {}", fullPath);
//      byte[] encoded;
//      try {
//        encoded = Files.readAllBytes(Paths.get(fullPath));
//      } catch (IOException e) {
//        return ret;
//      }
//      String fileContent = new String(encoded, Charset.defaultCharset());
//      Matcher m = Pattern.compile(GlobalUtil.GV).matcher(fileContent);
//      while (m.find()) {
//        String tmp = m.group();
//        tmp = tmp.substring(2, tmp.length() - 1);
//        if (!ret.keySet().contains(tmp))
//          ret.put(tmp, "");
//      }
//    }
    return ret;
  }
}
