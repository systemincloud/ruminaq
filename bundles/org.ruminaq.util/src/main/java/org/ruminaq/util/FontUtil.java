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
package org.ruminaq.util;

import java.util.Collection;

import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class FontUtil {

	public static Font findFont(Diagram diagram, String name, int size) {
		Collection<Font> fonts = diagram.getFonts();
		if(fonts != null)
			for(Font font : fonts)
				if(name.equals(font.getName()) && size == font.getSize()) return font;
		return null;
	}
}
