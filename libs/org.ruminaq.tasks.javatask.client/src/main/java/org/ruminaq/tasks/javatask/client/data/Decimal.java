/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.data;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Marek Jagielski
 */
public class Decimal extends Data {

	private List<BigDecimal> values = new LinkedList<>();

	public Decimal(BigDecimal value) {
		super(1);
		this.values.add(value);
	}

	public Decimal(List<Integer> dims, List<BigDecimal> values) {
		super(dims);
		this.values.addAll(values);
	}

	public List<BigDecimal> getValues() {
		return values;
	}

	public BigDecimal getValue() {
		return values.get(0);
	}
}
