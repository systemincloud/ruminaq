/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.model.ruminaq.Parameter;
import org.ruminaq.model.ruminaq.RuminaqFactory;
import org.ruminaq.util.GlobalUtil;
import com.google.common.base.Joiner;
import ch.qos.logback.classic.Logger;

public class TestParametersSection extends AbstractParametersSection {

  private final Logger logger = ModelerLoggerFactory
      .getLogger(TestParametersSection.class);

  private String fullPath;

  private MainTask getMainTask() {
    return getRuminaqDiagram().getMainTask();
  }

  protected Set<String> parseParameters() {
    logger.trace("getParameters");
    final Set<String> ret = new HashSet<>();
    String pathString = getDiagramTypeProvider().getDiagram().eResource()
        .getURI().toPlatformString(true);
    IPath path = Path.fromOSString(pathString);
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

    this.fullPath = file.getRawLocation().toOSString();
    logger.trace("fullPath = {}", fullPath);

    byte[] encoded;
    try {
      encoded = Files.readAllBytes(Paths.get(fullPath));
    } catch (IOException e) {
      logger.error("\n{}\n{}", e.getMessage(),
          Joiner.on("\n").join(e.getStackTrace()));
      return ret;
    }
    String fileContent = new String(encoded, Charset.defaultCharset());
    logger.trace("fileContent = {}", fileContent != null);
    Matcher m = Pattern.compile(GlobalUtil.GV).matcher(fileContent);
    while (m.find()) {
      String tmp2 = m.group();
      tmp2 = tmp2.substring(2, tmp2.length() - 1);
      if (!ret.contains(tmp2))
        ret.add(tmp2);
    }
    return ret;
  }

  @Override
  protected Collection<Parameter> getParameters() {
    Set<String> shouldBe = parseParameters();
    Set<String> is = getMainTask().getParameters().keySet();
    logger.trace("should be {}", shouldBe.toArray());
    logger.trace("is {}", is.toArray());
    final List<String> toRemove = new LinkedList<>();
    for (String s : is)
      if (!shouldBe.contains(s))
        toRemove.add(s);
    ModelUtil.runModelChange(
        () -> toRemove.stream().forEach(getMainTask().getParameters()::remove),
        getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        "Change parameter");

    final List<String> toAdd = new LinkedList<>();
    for (String s : shouldBe)
      if (!is.contains(s))
        toAdd.add(s);
    ModelUtil.runModelChange(() -> {
      for (String s : toAdd)
        getMainTask().getParameters().put(s, "");
    }, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        "Change parameter");

    return getMainTask().getParameters().entrySet().stream()
        .map((Entry<String, String> e) -> {
          Parameter p = RuminaqFactory.eINSTANCE.createParameter();
          p.setKey(e.getKey());
          p.setValue(e.getValue());
          p.setDefaultValue("");
          p.setDefault("".equals(e.getValue()));
          return p;
        }).collect(Collectors.toList());
  }

  @Override
  protected void saveParameter(final String key, final String value) {
    ModelUtil.runModelChange(
        () -> getMainTask().getParameters().put(key, value),
        getDiagramContainer().getDiagramBehavior().getEditingDomain(),
        "Change parameter");
  }
}
