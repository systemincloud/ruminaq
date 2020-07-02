/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 *
 * @author Marek Jagielski
 */
public class DataTest {

	@Test
	public void testEqualDimensions1() {
		Text text1 = new Text(Arrays.asList(new Integer[] { 1 }), new ArrayList<String>());
		Text text2 = new Text(Arrays.asList(new Integer[] { 1 }), new ArrayList<String>());
		assertTrue(text1.equalDimensions(text2));
		assertTrue(text2.equalDimensions(text1));
	}

	@Test
	public void testEqualDimensions2() {
		Text text1 = new Text(Arrays.asList(new Integer[] { 2 }), new ArrayList<String>());
		Text text2 = new Text(Arrays.asList(new Integer[] { 1 }), new ArrayList<String>());
		assertFalse(text1.equalDimensions(text2));
		assertFalse(text2.equalDimensions(text1));
	}

	@Test
	public void testEqualDimensions3() {
		Text text1 = new Text(Arrays.asList(new Integer[] { 1, 2 }), new ArrayList<String>());
		Text text2 = new Text(Arrays.asList(new Integer[] { 1 }), new ArrayList<String>());
		assertFalse(text1.equalDimensions(text2));
		assertFalse(text2.equalDimensions(text1));
	}

	@Test
	public void testEqualDimensions4() {
		Text text1 = new Text(Arrays.asList(new Integer[] { 1, 2 }), new ArrayList<String>());
		Text text2 = new Text(Arrays.asList(new Integer[] { 1, 2 }), new ArrayList<String>());
		assertTrue(text1.equalDimensions(text2));
		assertTrue(text2.equalDimensions(text1));
	}

	@Test
	public void testEqualDimensions5() {
		Text text1 = new Text(Arrays.asList(new Integer[] { 1, 2 }), new ArrayList<String>());
		Text text2 = new Text(Arrays.asList(new Integer[] { 1, 3 }), new ArrayList<String>());
		assertFalse(text1.equalDimensions(text2));
		assertFalse(text2.equalDimensions(text1));
	}

	@Test
	public void testEqualDimensions6() {
		Text text1 = new Text(Arrays.asList(new Integer[] { 1, 2 }), new ArrayList<String>());
		assertFalse(text1.equalDimensions(null));
	}
}
