/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.javatask.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ruminaq.tasks.javatask.client.data.Control;
import org.ruminaq.tasks.javatask.client.data.Data;

/**
 *
 * @author Marek Jagielski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface InputPortInfo {
  String name();

  Class<? extends Data>[] dataType() default { Control.class };

  boolean asynchronous() default false;

  int group() default -1;

  boolean hold() default false;

  int queue() default 1;
}
