/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.runner.util;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.framework.util.FilePath;
import org.ruminaq.model.ruminaq.BaseElement;

@SuppressWarnings("restriction")
public class Util {

  public static String getUniqueId(BaseElement be) {
    String baseId = "";
    if (be == null)
      return "";
    String platform = be.eResource().getURI().toPlatformString(true);
    if (platform != null) {
      String[] segments = (new FilePath(platform)).getSegments();
      StringBuilder sb = new StringBuilder(File.separator);
      for (int i = 0; i < segments.length; i++)
        sb.append(segments[i]).append(File.separator);
      sb.deleteCharAt(sb.length() - 1);
      baseId = sb.toString();
    }
    if (baseId.startsWith(File.separator))
      baseId = baseId.substring(1);
    return baseId + "#" + be.getId();
  }

  public static String getUniqueId(BaseElement be, String basePath) {
    String baseId = "";
    String[] segments = (new FilePath(basePath)).getSegments();
    StringBuilder sb = new StringBuilder(File.separator);
    for (int i = 0; i < segments.length - 1; i++)
      sb.append(segments[i]).append(File.separator);
    String file = be.eResource().getURI().toFileString().replace(sb.toString(),
        "");
    if (file != null)
      baseId = file.replace(basePath, "");
    if (baseId.contains(":"))
      baseId = baseId.substring(baseId.indexOf(":") + 1);
    return baseId + "#" + be.getId();
  }

  public static URI getModelPathFromEObject(EObject eobject) {
    URI uri = EcoreUtil.getURI(eobject);
    uri = uri.trimFragment();
    if (uri.isPlatform())
      uri = URI.createURI(uri.toPlatformString(true));
    return uri;
  }
}
