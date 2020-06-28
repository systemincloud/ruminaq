/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declarations of parameters.
 *
 * @author Marek Jagielski
 */
@Repeatable(Parameter.List.class)
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE })
public @interface Parameter {

  String name();

  String defaultValue() default "";

  String description() default "";

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  @interface List {

    Parameter[] value();

  }
}
