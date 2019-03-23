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
package org.ruminaq.tasks.constant.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.tasks.constant.impl.AbstractConstantStrategy;

public enum ConstantServiceManager {

    INSTANCE;

    private List<ConstantService> services = new ArrayList<>();

    ConstantServiceManager() {
        ServiceLoader<ConstantService> sl = ServiceLoader.load(ConstantService.class);
        sl.forEach(srv -> services.add(srv));
    }

    public Optional<AbstractConstantStrategy> getStrategy(DataType dt, String value) {
        return services.stream()
                .map(srv -> srv.getStrategy(dt, value))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
