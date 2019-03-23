package org.ruminaq.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ruminaq.model.api.ModelExtension;
import org.ruminaq.model.api.ModelExtensionHandler;
import org.ruminaq.model.ruminaq.DataType;

@Component(immediate = true)
public class ModelExtensionHandlerImpl implements ModelExtensionHandler {

	private Collection<ModelExtension> extensions;

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	protected void bind(ModelExtension extension) {
		if (extensions == null) {
			extensions = new ArrayList<>();
		}
		extensions.add(extension);
	}

	protected void unbind(ModelExtension extension) {
		extensions.remove(extension);
	}

	@Override
	public Collection<? extends Class<? extends DataType>> getDataTypes() {
		return extensions.stream()
				.map(ModelExtension::getDataTypes)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<DataType> getDataTypeFromName(String name) {
		return extensions.stream()
				.map(ext -> ext.getDataTypeFromName(name))
				.filter(Optional::isPresent)
				.findFirst()
				.get();
	}

	@Override
	public boolean canCastFromTo(Class<? extends DataType> from, Class<? extends DataType> to) {
		return extensions.stream()
				.anyMatch(ext -> ext.canCastFromTo(from, to));
	}

	@Override
	public boolean isLossyCastFromTo(Class<? extends DataType> from, Class<? extends DataType> to) {
		return extensions.stream()
				.anyMatch(ext -> ext.isLossyCastFromTo(from, to));
	}

}
