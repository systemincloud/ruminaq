package org.ruminaq.tasks.gate.xor;

import org.osgi.service.component.annotations.Component;
import org.ruminaq.gui.api.CreateFeaturesExtension;

@Component(property = { "service.ranking:Integer=15" })
public class CreateFeature implements CreateFeaturesExtension {

}
