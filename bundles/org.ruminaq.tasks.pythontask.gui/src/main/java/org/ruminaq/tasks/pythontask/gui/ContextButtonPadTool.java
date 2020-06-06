package org.ruminaq.tasks.pythontask.gui;

import java.util.function.Predicate;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.diagram.RuminaqBehaviorProvider;
import org.ruminaq.tasks.pythontask.gui.ContextButtonPadTool.Filter;
import org.ruminaq.tasks.pythontask.model.pythontask.PythonTask;
import org.ruminaq.util.ServiceFilter;
import org.ruminaq.util.ServiceFilterArgs;

@Component(property = { "service.ranking:Integer=10" })
@ServiceFilter(Filter.class)
public class ContextButtonPadTool
    implements GenericContextButtonPadDataExtension {

  public static class Filter implements Predicate<ServiceFilterArgs> {

    @Override
    public boolean test(ServiceFilterArgs args) {
      IFeatureProvider fp = (IFeatureProvider) args.getArgs().get(0);
      IPictogramElementContext context = (IPictogramElementContext) args
          .getArgs().get(1);
      PictogramElement pe = context.getPictogramElement();
      return fp.getBusinessObjectForPictogramElement(pe) instanceof PythonTask;
    }
  }

  @Override
  public int getGenericContextButtons() {
    return RuminaqBehaviorProvider.CONTEXT_BUTTON_DELETE
        | RuminaqBehaviorProvider.CONTEXT_BUTTON_UPDATE;
  }
}
