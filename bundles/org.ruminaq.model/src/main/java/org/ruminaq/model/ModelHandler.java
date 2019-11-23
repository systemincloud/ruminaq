package org.ruminaq.model;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.MainTask;

public class ModelHandler {

  public static MainTask getModel(Diagram diagram) {
    return (MainTask) diagram.getLink().getBusinessObjects().get(0);
  }

  public static boolean containsID(MainTask model, String value) {
    for (BaseElement be : model.getTask())
      if (be.getId().equals(value))
        return true;

    for (BaseElement be : model.getInputPort())
      if (be.getId().equals(value))
        return true;

    for (BaseElement be : model.getOutputPort())
      if (be.getId().equals(value))
        return true;

    return false;
  }

}
