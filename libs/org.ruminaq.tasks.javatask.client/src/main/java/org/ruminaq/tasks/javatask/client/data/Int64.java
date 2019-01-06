/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public class Int64 extends Data {

	private long[] values;

	public Int64(long value) {
		super(1);
		values = new long[] { value };
	}

	public Int64(List<Integer> dims, long[] values) {
		this(dims, values, false);
	}

	public Int64(List<Integer> dims, long[] values, boolean copy) {
		super(dims);
		if (copy) {
			this.values = new long[values.length];
			System.arraycopy(values, 0, this.values, 0, values.length);
		} else
			this.values = values;
	}

	public long[] getValues() {
		return values;
	}

	public long getValue() {
		return values[0];
	}
}
