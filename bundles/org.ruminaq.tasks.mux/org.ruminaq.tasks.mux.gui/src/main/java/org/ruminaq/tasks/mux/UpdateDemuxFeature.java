package org.ruminaq.tasks.mux;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.tasks.demux.impl.Port;
import org.ruminaq.tasks.mux.model.mux.Demux;

public class UpdateDemuxFeature extends UpdateTaskFeature {

  private boolean updateNeededChecked = false;

  private boolean superUpdateNeeded = false;
  private boolean outputsUpdateNeeded = false;

  public UpdateDemuxFeature(IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canUpdate(IUpdateContext context) {
    Object bo = getBusinessObjectForPictogramElement(
        context.getPictogramElement());
    return (bo instanceof Demux);
  }

  @Override
  public IReason updateNeeded(IUpdateContext context) {
    this.updateNeededChecked = true;
    superUpdateNeeded = super.updateNeeded(context).toBoolean();

    ContainerShape parent = (ContainerShape) context.getPictogramElement();
    Demux dmx = (Demux) getBusinessObjectForPictogramElement(parent);

    this.outputsUpdateNeeded = dmx.getSize() != dmx.getOutputPort().size();

    boolean updateNeeded = superUpdateNeeded || outputsUpdateNeeded;
    return updateNeeded ? Reason.createTrueReason()
        : Reason.createFalseReason();
  }

  @Override
  public boolean update(IUpdateContext context) {
    if (!updateNeededChecked)
      if (!this.updateNeeded(context).toBoolean())
        return false;

    boolean updated = false;
    if (superUpdateNeeded)
      updated = updated | super.update(context);

    ContainerShape parent = (ContainerShape) context.getPictogramElement();
    Demux dmx = (Demux) getBusinessObjectForPictogramElement(parent);

    if (outputsUpdateNeeded)
      updated = updated | outputsUpdate(parent, dmx);

    return updated;
  }

  private boolean outputsUpdate(ContainerShape parent, Demux dmx) {
    int n = dmx.getSize() - dmx.getOutputPort().size();
    if (n > 0)
      for (int i = 0; i < n; i++)
        addPort(dmx, parent, Port.OUT);
    else if (n < 0)
      for (int i = 0; i < -n; i++)
        removePort(dmx, parent, Port.OUT);

    return true;
  }
}
