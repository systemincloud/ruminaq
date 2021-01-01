/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.features.update;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.add.AddEmbeddedTaskFeature;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.Connection;
import org.ruminaq.model.ruminaq.EmbeddedTask;
import org.ruminaq.model.ruminaq.InternalInputPort;
import org.ruminaq.model.ruminaq.MainTask;

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

  protected static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return EmbeddedTask.class;
    }
  }

  private MainTask embeddedTask;

  public UpdateEmbeddedTaskFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean load(String path) {
    this.embeddedTask = Optional.of(path)
        .map(p -> new ResourceSetImpl().getResource(URI.createURI(
            "/" + EclipseUtil.getUriOf(getDiagram()).segment(0) + "/" + path),
            true))
        .filter(Objects::nonNull).map(Resource::getContents).map(EList::stream)
        .orElseGet(Stream::empty).skip(1).findFirst()
        .filter(MainTask.class::isInstance).map(MainTask.class::cast)
        .orElse(null);
    return embeddedTask != null;
  }

  @Override
  protected String iconDesc() {
    return Optional.ofNullable(embeddedTask).map(EclipseUtil::getUriOf)
        .map(URI::lastSegment).orElse(AddEmbeddedTaskFeature.NOT_CHOSEN);
  }

  @Override
  protected List<FileInternalInputPort> inputPorts() {
    return Optional.ofNullable(embeddedTask).map(MainTask::getInputPort)
        .map(List::stream).orElseGet(Stream::empty)
        .map(ip -> new FileInternalInputPort(ip.getId(),
            embeddedTask.getConnection().stream()
                .filter(c -> c.getSourceRef().equals(ip))
                .map(Connection::getTargetRef)
                .filter(InternalInputPort.class::isInstance)
                .map(InternalInputPort.class::cast)
                .map(InternalInputPort::getDataType).flatMap(Collection::stream)
                .distinct().collect(Collectors.toList()),
            ip.isAsynchronous(), ip.getGroup(), ip.isHoldLast(),
            ip.getQueueSize()))
        .collect(Collectors.toList());
  }

  @Override
  protected List<FileInternalOutputPort> outputPorts() {
    return Optional.ofNullable(embeddedTask).map(MainTask::getOutputPort)
        .map(List::stream).orElseGet(Stream::empty)
        .map(ip -> new FileInternalOutputPort(ip.getId(),
            embeddedTask.getConnection().stream()
                .filter(c -> c.getSourceRef().equals(ip))
                .map(Connection::getTargetRef)
                .filter(InternalInputPort.class::isInstance)
                .map(InternalInputPort.class::cast)
                .map(InternalInputPort::getDataType).flatMap(Collection::stream)
                .distinct().collect(Collectors.toList())))
        .collect(Collectors.toList());
  }

  @Override
  protected boolean isAtomic() {
    return Optional.ofNullable(embeddedTask)
        .filter(Predicate.not(MainTask::isAtomic)).isEmpty();
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
