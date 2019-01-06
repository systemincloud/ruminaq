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
package org.ruminaq.tasks.constant.impl.strategy;

import java.util.List;

import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.data.Int32I;
import org.ruminaq.tasks.constant.impl.AbstractConstantStrategy;
import org.ruminaq.tasks.constant.impl.ConstantI;
import org.ruminaq.tasks.constant.impl.Port;
import org.ruminaq.util.NumericUtil;
import org.slf4j.Logger;

public class Int32Strategy extends AbstractConstantStrategy {

    private static final Logger LOGGER = RunnerLoggerFactory.getLogger(Int32Strategy.class);

    public static final String DEFAULT_VALUE = "0";

    public Int32Strategy(ConstantI task, String value) {
        super(task, value);
    }

    @Override
    public void execute() {
        LOGGER.trace("create Int32");
        List<Integer> dims = NumericUtil.getMultiDimsIntegerDimensions(value);
        String[] vs        = NumericUtil.getMutliDimsValues(value);
        int n = dims.stream().reduce(1, (a, b) -> a * b);
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = Integer.parseInt(vs[i]);
        }
        task.putData(Port.OUT, new Int32I(values, dims));
    }
}
