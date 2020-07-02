/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.rtask.hacks;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.SuppressAjWarnings;

//import de.walware.rj.servi.internal.NodeHandler;

@Aspect
@SuppressWarnings("restriction")
@SuppressAjWarnings({ "adviceDidNotMatch" })
public class LocalNodeFactoryHack {
//
//  @Around("call(* de.walware.rj.servi.internal.LocalNodeFactory.createNode(..)) && args(poolObj)")
//  public void around(ProceedingJoinPoint point, final NodeHandler poolObj)
//      throws Throwable {
//    System.out.println("XXX");
//    point.proceed();
//  }

  public static LocalNodeFactoryHack aspectOf() {
    return new LocalNodeFactoryHack();
  }
}
