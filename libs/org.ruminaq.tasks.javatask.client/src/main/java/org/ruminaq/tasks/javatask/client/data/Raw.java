/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public class Raw extends Data {

	private List<byte[]> values = new LinkedList<byte[]>();

	public Raw(byte[] value) {
		this(value, false);
	}

	public Raw(byte[] value, boolean copy) {
		super(1);
		if (copy) {
			byte[] _values = Arrays.copyOf(value, value.length);
			this.values.add(_values);
		} else {
			this.values.add(value);
		}
	}

	public Raw(List<Integer> dims, byte[] values) {
		this(dims, values, false);
	}

	public Raw(List<Integer> dims, byte[] values, boolean copy) {
		super(dims);
		if (copy) {
			byte[] _values = new byte[values.length];
			System.arraycopy(values, 0, _values, 0, values.length);
			this.values.add(_values);
		} else
			this.values.add(values);
	}

	public Raw(List<Integer> dims, List<byte[]> values) {
		this(dims, values, false);
	}

	public Raw(List<Integer> dims, List<byte[]> values, boolean copy) {
		super(dims);
		if (copy) {
			for (byte[] v : values) {
				this.values.add(Arrays.copyOf(v, v.length));
			}
		} else {
			this.values.addAll(values);
		}
	}

	public List<byte[]> getValues() {
		return values;
	}

	public byte[] getValue() {
		return values.get(0);
	}
}
