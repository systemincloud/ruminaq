/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.sipo.gui;

import java.util.Collections;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.TasksUtil;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.gui.features.update.UpdateTaskFeature;
import org.ruminaq.model.desc.PortsDescrUtil;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.tasks.sipo.gui.UpdateFeatureImpl.UpdateFeature.Filter;
import org.ruminaq.tasks.sipo.model.Port;
import org.ruminaq.tasks.sipo.model.sipo.Sipo;

/**
 * Service UpdateFeatureExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=10" })
public class UpdateFeatureImpl implements UpdateFeatureExtension {

  @Override
  public List<Class<? extends IUpdateFeature>> getFeatures() {
    return Collections.singletonList(UpdateFeature.class);
  }

  /**
   * Constant UpdateFeature.
   */
  @FeatureFilter(Filter.class)
  public static class UpdateFeature extends UpdateTaskFeature {

    public static class Filter extends AbstractUpdateFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return Sipo.class;
      }
    }

    private boolean updateNeededChecked = false;

    private boolean superUpdateNeeded = false;
    private boolean clkUpdateNeeded = false;
    private boolean idxUpdateNeeded = false;
    private boolean trgUpdateNeeded = false;
    private boolean sizeUpdateNeeded = false;
    private boolean lastUpdateNeeded = false;
    private boolean sizeOutUpdateNeeded = false;

    public UpdateFeature(IFeatureProvider fp) {
      super(fp);
    }

    @Override
    public boolean canUpdate(IUpdateContext context) {
      Object bo = getBusinessObjectForPictogramElement(
          context.getPictogramElement());
      return (bo instanceof Sipo);
    }

    @Override
    public IReason updateNeeded(IUpdateContext context) {
      this.updateNeededChecked = true;
      superUpdateNeeded = super.updateNeeded(context).toBoolean();

      ContainerShape parent = (ContainerShape) context.getPictogramElement();
      Sipo sp = (Sipo) getBusinessObjectForPictogramElement(parent);

      this.clkUpdateNeeded = sp.isClock()
          ? TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.CLK)) == null
          : TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.CLK)) != null;
      this.idxUpdateNeeded = sp.isIndex()
          ? TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.IDX)) == null
          : TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.IDX)) != null;
      this.trgUpdateNeeded = sp.isTrigger() && !sp.isIndex()
          ? TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.TRIGGER)) == null
          : TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.TRIGGER)) != null;
      this.sizeUpdateNeeded = sp.isIndex()
          ? TasksUtil.getAllMutlipleInternalOutputPorts(sp,
              PortsDescrUtil.getName(Port.OUT)).size() != 0
          : TasksUtil
              .getAllMutlipleInternalOutputPorts(sp,
                  PortsDescrUtil.getName(Port.OUT))
              .size() != Integer.parseInt(sp.getSize());
      if (trgUpdateNeeded)
        sizeUpdateNeeded = true;
      this.lastUpdateNeeded = sp.isIndex()
          ? TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.LOUT)) == null
          : TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.LOUT)) != null;
      this.sizeOutUpdateNeeded = sp.isSizeOut()
          ? TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.SIZE)) == null
          : TasksUtil.getInternalPort(sp,
              PortsDescrUtil.getName(Port.SIZE)) != null;

      boolean updateNeeded = superUpdateNeeded || clkUpdateNeeded
          || idxUpdateNeeded || trgUpdateNeeded || sizeUpdateNeeded
          || lastUpdateNeeded || sizeOutUpdateNeeded;

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
      Sipo sp = (Sipo) getBusinessObjectForPictogramElement(parent);

      if (clkUpdateNeeded)
        updated = updated | clkUpdate(parent, sp);
      if (idxUpdateNeeded)
        updated = updated | idxUpdate(parent, sp);
      if (trgUpdateNeeded)
        updated = updated | trgUpdate(parent, sp);
      if (sizeUpdateNeeded)
        updated = updated | sizeUpdate(parent, sp);
      if (lastUpdateNeeded)
        updated = updated | lastUpdate(parent, sp);
      if (sizeOutUpdateNeeded)
        updated = updated | sizeOutUpdate(parent, sp);
      return updated;
    }

    private boolean sizeOutUpdate(ContainerShape parent, Sipo sp) {
//    if (sp.isSizeOut())
//      addPort(sp, parent, Port.SIZE);
//    else
//      removePort(sp, parent, Port.SIZE);
      return true;
    }

    private boolean lastUpdate(ContainerShape parent, Sipo sp) {
//    if (sp.isIndex())
//      addPort(sp, parent, Port.LOUT);
//    else
//      removePort(sp, parent, Port.LOUT);
      return true;
    }

    private boolean trgUpdate(ContainerShape parent, Sipo sp) {
//    if (sp.isTrigger() && !sp.isIndex())
//      addPort(sp, parent, Port.TRIGGER);
//    else
//      removePort(sp, parent, Port.TRIGGER);
      return true;
    }

    private boolean sizeUpdate(ContainerShape parent, Sipo sp) {
      int n = sp.isIndex() ? -Integer.parseInt(sp.getSize())
          : Integer.parseInt(sp.getSize())
              - TasksUtil.getAllMutlipleInternalOutputPorts(sp,
                  PortsDescrUtil.getName(Port.OUT)).size();
//    if (n > 0)
//      for (int i = 0; i < n; i++)
//        addPort(sp, parent, Port.OUT);
//    else if (n < 0)
//      for (int i = 0; i < -n; i++)
//        removePort(sp, parent, Port.OUT);
      return true;
    }

    private boolean idxUpdate(ContainerShape parent, Sipo sp) {
//    if (sp.isIndex())
//      addPort(sp, parent, Port.IDX);
//    else
//      removePort(sp, parent, Port.IDX);
      return true;
    }

    private boolean clkUpdate(ContainerShape parent, Sipo sp) {
//    if (sp.isClock())
//      addPort(sp, parent, Port.CLK);
//    else
//      removePort(sp, parent, Port.CLK);
      return true;
    }
  }
}
