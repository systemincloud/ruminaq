package org.ruminaq.hacks.org.eclipse.graphiti.ui.internal.parts;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.eclipse.draw2d.IFigure;
import org.eclipse.graphiti.tb.IDecorator;
import org.ruminaq.gui.diagram.RuminaqDecorator;

@Aspect
@SuppressAjWarnings({ "adviceDidNotMatch" })
public class PictogramElementDelegateHack {

  @Around("call(* org.eclipse.graphiti.ui.internal.parts.PictogramElementDelegate.decorateFigure(..)) && args(figure, decorator)")
  public Object around(ProceedingJoinPoint point, IFigure figure,
      IDecorator decorator) throws Throwable {
    if (decorator instanceof RuminaqDecorator)
      return ((RuminaqDecorator) decorator).decorateFigure(figure, decorator);
    return point.proceed();
  }

  public static PictogramElementDelegateHack aspectOf() {
    return new PictogramElementDelegateHack();
  }
}
