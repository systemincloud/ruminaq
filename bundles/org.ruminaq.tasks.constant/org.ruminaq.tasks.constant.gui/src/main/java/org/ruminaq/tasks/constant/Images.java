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
package org.ruminaq.tasks.constant;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

public class Images {

    public enum K {
        IMG_CONSTANT_PALETTE("/icons/palette.constant.png");

        private String path;

        K(String path) {
            this.path = path;
        }
    }

    private static Map<String, String> images = Arrays.stream(K.values())
            .collect(Collectors.toMap(
                    K::name,
                    i -> FileLocator.find(
                            FrameworkUtil.getBundle(Images.class),
                            new Path(i.path),
                            null).toString()));

    public static Map<String, String> getImageKeyPath() {
        return images;
    }
}
