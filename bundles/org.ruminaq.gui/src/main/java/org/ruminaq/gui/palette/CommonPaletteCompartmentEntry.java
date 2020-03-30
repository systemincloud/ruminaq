/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.palette;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.func.ICreateInfo;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.PaletteCompartmentEntryExtension;
import org.ruminaq.gui.features.create.PaletteCreateFeature;

/**
 * Service PaletteCompartmentEntryExtension implementation.
 *
 * @author Marek Jagielski
 */
@Component(property = { "service.ranking:Integer=5" })
public class CommonPaletteCompartmentEntry
    implements PaletteCompartmentEntryExtension {

  public static final String DEFAULT_COMPARTMENT = "Commons";
  public static final String CONNECTIONS_STACK = "Connections";
  public static final String PORTS_STACK = "Ports";
  public static final String SOURCES_STACK = "Sources";
  public static final String FLOW_STACK = "Flow";
  public static final String LOGIC_STACK = "Logic";
  public static final String USERDEFINED_STACK = "User defined";
  public static final String SINKS_STACK = "Sinks";

  @Override
  public Collection<IPaletteCompartmentEntry> getPalette(IFeatureProvider fp,
      boolean isTest) {
    IPaletteCompartmentEntry commonCompartmentEntry = new PaletteCompartmentEntry(
        DEFAULT_COMPARTMENT, null);
    commonCompartmentEntry.setInitiallyOpen(false);

    ICreateConnectionFeature[] createConnectionFeatures = fp
        .getCreateConnectionFeatures();

    Stream.of(CONNECTIONS_STACK).forEachOrdered((String stackName) -> {
      StackEntry connectionsStackEntry = new StackEntry(CONNECTIONS_STACK, "",
          null);
      getConnectionCreationToolEntries(isTest, createConnectionFeatures,
          stackName).forEach(connectionsStackEntry::addCreationToolEntry);

      if (!connectionsStackEntry.getCreationToolEntries().isEmpty()) {
        commonCompartmentEntry.getToolEntries().add(connectionsStackEntry);
      }
    });

    ICreateFeature[] createFeatures = fp.getCreateFeatures();

    Stream.of(PORTS_STACK, SOURCES_STACK, SINKS_STACK)
        .forEachOrdered((String stackName) -> {
          StackEntry stackEntry = new StackEntry(stackName, "", null);
          getCreationToolEntries(isTest, createFeatures, stackName)
              .forEach(stackEntry::addCreationToolEntry);

          commonCompartmentEntry.getToolEntries().add(stackEntry);
        });

    return Collections.singletonList(commonCompartmentEntry);
  }

  private static List<ConnectionCreationToolEntry> getConnectionCreationToolEntries(
      boolean isTest, ICreateConnectionFeature[] createConnectionFeatures,
      String stackName) {
    return filterCreateInfos(isTest, createConnectionFeatures, stackName,
        ICreateConnectionFeature.class).stream()
            .map((ICreateConnectionFeature cf) -> {
              ConnectionCreationToolEntry cte = new ConnectionCreationToolEntry(
                  cf.getCreateName(), cf.getCreateDescription(),
                  cf.getCreateImageId(), cf.getCreateLargeImageId());
              cte.addCreateConnectionFeature(cf);
              return cte;
            }).collect(Collectors.toList());
  }

  private static List<ObjectCreationToolEntry> getCreationToolEntries(
      boolean isTest, ICreateFeature[] createFeatures, String stackName) {
    return filterCreateInfos(isTest, createFeatures, stackName,
        ICreateFeature.class).stream()
        .map(cf -> new ObjectCreationToolEntry(cf.getCreateName(),
            cf.getCreateDescription(), cf.getCreateImageId(),
            cf.getCreateLargeImageId(), cf))
        .collect(Collectors.toList());
  }

  private static <K extends ICreateInfo> List<K> filterCreateInfos(
      boolean isTest, K[] createFeatures, String stackName, Class<K> type) {
    return Stream.of(createFeatures)
        .filter(PaletteCreateFeature.class::isInstance)
        .map(PaletteCreateFeature.class::cast)
        .filter(cf -> !(isTest && !cf.isTestOnly()))
        .filter(cf -> DEFAULT_COMPARTMENT.equals(cf.getCompartment()))
        .filter(cf -> stackName.equals(cf.getStack())).filter(type::isInstance)
        .map(type::cast).collect(Collectors.toList());
  }
}
