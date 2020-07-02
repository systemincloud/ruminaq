/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.eclipse.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.URI;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.ruminaq.eclipse.RuminaqDiagramUtil;

@RunWith(JUnitPlatform.class)
class RuminaqDiagramUtilTest {

  @ParameterizedTest
  @ValueSource(strings = { "resource:/src/test/resources/tasks/MyTask.rumi",
      "resource:/src/test/resources/tasks/dir/MyTask.rumi" })
  void testIsTestByUri(String input) {
    URI uri = URI.createFileURI(input);
    assertTrue("Is test", RuminaqDiagramUtil.isTest(uri));
  }

  @ParameterizedTest
  @ValueSource(strings = { "resource:/src/main/resources/tasks/MyTask.rumi",
      "resource:/src/test/resources/tasks/dir/MyTask.txt" })
  void testIsNotTestByUri(String input) {
    URI uri = URI.createFileURI(input);
    assertFalse("Isn't test", RuminaqDiagramUtil.isTest(uri));
  }

  @ParameterizedTest
  @ValueSource(strings = { "resource:/project/src/test/resources/tasks/MyTask.rumi" })
  void testIsTestByUriWithBase(String input) {
    URI uri = URI.createFileURI(input);
    assertTrue("Is test", RuminaqDiagramUtil.isTest(uri, "resource:/project/"));
  }

}
