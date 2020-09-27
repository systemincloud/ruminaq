/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

/**
 * Other plugins can contribute to Graphiti images.
 *
 * @author Marek Jagielski
 */
public interface ImagesExtension {

  /**
   * Api for service providing paths to images.
   *
   * @return map image key to path
   */
  default Map<String, String> getImageKeyPath() {
    return getImageDecriptors().stream()
        .peek((ImageDescriptor i) -> { System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"); })
        .peek((ImageDescriptor i) -> { System.out.println(i.name()); })
        .peek((ImageDescriptor i) -> { System.out.println(i.clazz()); })
        .peek((ImageDescriptor i) -> { System.out.println(FrameworkUtil.getBundle(i.clazz())); })
        .peek((ImageDescriptor i) -> { System.out.println(i.path()); })
        .peek((ImageDescriptor i) -> { System.out.println(FileLocator
            .find(FrameworkUtil.getBundle(i.clazz()), new Path(i.path()), null)); })
        .peek((ImageDescriptor i) -> { System.out.println(FileLocator
            .find(FrameworkUtil.getBundle(i.clazz()), new Path(i.path()), null)
            .toExternalForm()); })
        .collect(Collectors.toMap(ImageDescriptor::name, i -> FileLocator
            .find(FrameworkUtil.getBundle(i.clazz()), new Path(i.path()), null)
            .toExternalForm()));
  }

  default Collection<ImageDescriptor> getImageDecriptors() {
    return Collections.emptyList();
  }
}
