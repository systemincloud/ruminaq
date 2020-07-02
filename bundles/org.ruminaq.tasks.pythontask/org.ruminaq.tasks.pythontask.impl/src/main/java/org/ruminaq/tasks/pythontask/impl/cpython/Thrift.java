/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.pythontask.impl.cpython;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public enum Thrift {
  INSTANCE;

  private static final String THRIFTPY_LIB_VERSION = "v0.3.9";
  private static final String URL = "https://github.com/eleme/thriftpy.git";

  public String getThrifpy(IProject p, String bin) {
    File thriftpyDir = new File(
        p.getLocation().toString() + "/target/thriftpy");
    if (!thriftpyDir.exists()) {
      thriftpyDir.getParentFile().mkdirs();
      try {
        Git g = Git.cloneRepository().setURI(URL).setDirectory(thriftpyDir)
            .call();
        g.checkout().setName(THRIFTPY_LIB_VERSION).call();

        ProcessBuilder pb = new ProcessBuilder(bin, "setup.py", "develop",
            "--install-dir=" + thriftpyDir.getParent());
        pb.directory(thriftpyDir);
        pb.environment().put("PYTHONPATH", thriftpyDir.getParent());
        Process pr = pb.start();
        pr.waitFor();
      } catch (GitAPIException e) {
        e.printStackTrace();
      } catch (IOException | InterruptedException e) {
      }
    }
    return thriftpyDir.getAbsolutePath();
  }
}
