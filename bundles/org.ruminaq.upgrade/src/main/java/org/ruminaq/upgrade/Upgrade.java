/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.upgrade;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.ruminaq.logs.ModelerLoggerFactory;
import ch.qos.logback.classic.Logger;

public class Upgrade {

  private final Logger logger = ModelerLoggerFactory.getLogger(Upgrade.class);

  public enum VersionsOrder {
    v0_2_0("0.2.0"), v0_2_1("0.2.1"), v0_2_2("0.2.2"), v0_2_3("0.2.3"),
    v0_3_0("0.3.0"), v0_4_0("0.4.0"), v0_4_1("0.4.1"), v0_4_2("0.4.2"),
    v0_5_0("0.5.0"), v0_5_1("0.5.1"), v0_5_2("0.5.2"), v0_5_3("0.5.3"),
    v0_6_0("0.6.0"), v0_6_1("0.6.1"), v0_7_0("0.7.0"), v0_7_1("0.7.1"),
    v0_7_2("0.7.2"), v0_8_0("0.8.0"), v0_9_0("0.9.0");

    private String version;

    public String getVersion() {
      return version;
    }

    public String getScript() {
      return version.replace(".", "_") + ".groovy";
    }

    VersionsOrder(String version) {
      this.version = version;
    }
  }

  private String actualVersion;
  private String newVersion;
  private IProject project;

  public Upgrade(String actualVersion, String newVersion, IProject project) {
    this.actualVersion = actualVersion;
    this.newVersion = newVersion;
    this.project = project;
  }

  public boolean execute() {
    logger.trace("Upgrade");
    boolean flag = false;
    for (VersionsOrder vo : VersionsOrder.values()) {
      if (vo.getVersion().equals(actualVersion)) {
        flag = true;
        continue;
      }
      if (flag) {
        Binding binding = new Binding();
        binding.setVariable("root", project.getLocation().toOSString());
        GroovyShell shell = new GroovyShell(binding);

        String script = null;
        try {
          logger.trace("Run script {}", vo.getScript());
          script = IOUtils.toString(
              Upgrade.class.getResourceAsStream("/" + vo.getScript()), "UTF-8");
        } catch (IOException e) {
          e.printStackTrace();
        }

        if (script != null)
          try {
            shell.evaluate(script);
          } catch (Exception e) {
            e.printStackTrace();
          }
        else
          return false;
      }
      if (vo.getVersion().equals(newVersion))
        flag = false;
    }

    try {
      project.refreshLocal(IResource.DEPTH_INFINITE, null);
    } catch (CoreException e) {
    }
    return true;
  }
}
