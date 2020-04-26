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

import org.eclipse.emf.ecore.EPackage;
import org.ruminaq.model.ruminaq.dt.Control;
import org.ruminaq.model.ruminaq.dt.DatatypePackage;

/**
 * 
 * @author Marek Jagielski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PortData {

  Class<? extends DataType> type() default Control.class;

  Class<? extends EPackage> dataPackage() default DatatypePackage.class;

}
