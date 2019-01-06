/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public class Float64 extends Data {

	private double[] values;

	public Float64(double value) {
		super(1);
		values = new double[] { value };
	}

	public Float64(List<Integer> dims, double[] values) {
		this(dims, values, false);
	}

	public Float64(List<Integer> dims, double[] values, boolean copy) {
		super(dims);
		if (copy) {
			this.values = new double[values.length];
			System.arraycopy(values, 0, this.values, 0, values.length);
		} else
			this.values = values;
	}

	public double[] getValues() {
		return values;
	}

	public double getValue() {
		return values[0];
	}
}
