/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.model.ruminaq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Marek Jagielski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PortInfo {
  
  PortType portType();
  
  // Input and Output
  
  String id();

  int n() default 1;
  
  boolean opt() default false;

  // Only Input

  boolean asynchronous() default false;

  int group() default -1;

  boolean hold() default false;

  String queue() default "1";

  NGroup ngroup() default NGroup.SAME;

}
