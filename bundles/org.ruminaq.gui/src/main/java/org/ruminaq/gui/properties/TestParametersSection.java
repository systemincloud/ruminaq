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
package org.ruminaq.gui.properties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.model.ModelHandler;
import org.ruminaq.model.ruminaq.MainTask;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.util.GlobalUtil;
import org.slf4j.Logger;

import com.google.common.base.Joiner;

public class TestParametersSection extends AbstractParametersSection {

	private final Logger logger = ModelerLoggerFactory
	    .getLogger(TestParametersSection.class);

	private String fullPath;
	private MainTask mt;

	protected Set<String> getParameters() {
		logger.trace("getParameters");
		final Set<String> ret = new HashSet<>();
		this.mt = ModelHandler.getModel(getDiagramTypeProvider().getDiagram(),
		    getDiagramTypeProvider().getFeatureProvider());
		logger.trace("mt = {}", mt);
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
	protected Map<String, String> getActualParams() {
		Set<String> shouldBe = getParameters();
		Set<String> is = mt.getParameters().keySet();
		logger.trace("should be {}", shouldBe.toArray());
		logger.trace("is {}", is.toArray());
		final List<String> toRemove = new LinkedList<>();
		for (String s : is)
			if (!shouldBe.contains(s))
				toRemove.add(s);
		ModelUtil.runModelChange(new Runnable() {
			@Override
			public void run() {
				for (String s : toRemove)
					mt.getParameters().remove(s);
			}
		}, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
		    "Change parameter");

		final List<String> toAdd = new LinkedList<>();
		for (String s : shouldBe)
			if (!is.contains(s))
				toAdd.add(s);
		ModelUtil.runModelChange(new Runnable() {
			@Override
			public void run() {
				for (String s : toAdd)
					mt.getParameters().put(s, "");
			}
		}, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
		    "Change parameter");

		return mt.getParameters();
	}

	@Override
	protected void saveParameter(final String key, final String value) {
		ModelUtil.runModelChange(new Runnable() {
			@Override
			public void run() {
				if (mt == null)
					return;
				mt.getParameters().put(key, value);
			}
		}, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
		    "Change parameter");
	}
}
