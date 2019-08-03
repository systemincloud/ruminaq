/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.gui.diagram;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IRectangle;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.ISingleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.api.ContextButtonPadLocationExtension;
import org.ruminaq.gui.api.ContextMenuEntryExtension;
import org.ruminaq.gui.api.DecoratorExtension;
import org.ruminaq.gui.api.DomainContextButtonPadDataExtension;
import org.ruminaq.gui.api.GenericContextButtonPadDataExtension;
import org.ruminaq.gui.providers.DoubleClickFeatureProvider;
import org.ruminaq.util.ServiceFilterArgs;
import org.ruminaq.util.ServiceUtil;

public class RuminaqBehaviorProvider extends DefaultToolBehaviorProvider {

	public RuminaqBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public boolean isShowSelectionTool() {
		return false;
	}

	@Override
	public boolean isShowMarqueeTool() {
		return false;
	}

	@Override
	public boolean isShowGuides() {
		return false;
	}

	@Override
	public boolean isShowFlyoutPalette() {
		return true;
	}

	@Override
	public boolean equalsBusinessObjects(Object o1, Object o2) {
		return o1 instanceof EObject && o2 instanceof EObject ? o1 == o2 : false;
	}

//	@Override
//	public IPaletteCompartmentEntry[] getPalette() {
//		if (ConstantsUtil
//		    .isTest(getDiagramTypeProvider().getDiagram().eResource().getURI())) {
//			return ServiceUtil
//			    .getServicesAtLatestVersion(RuminaqBehaviorProvider.class,
//			        TestPaletteCompartmentEntryExtension.class)
//			    .stream().map(ext -> ext.getFeatures(getFeatureProvider()))
//			    .flatMap(Collection::stream)
//			    .toArray(size -> (IPaletteCompartmentEntry[]) new Object[size]);
//		} else {
//			return ServiceUtil
//			    .getServicesAtLatestVersion(RuminaqBehaviorProvider.class,
//			        PaletteCompartmentEntryExtension.class)
//			    .stream().map(ext -> ext.getFeatures(getFeatureProvider()))
//			    .flatMap(Collection::stream)
//			    .toArray(size -> (IPaletteCompartmentEntry[]) new Object[size]);
//		}
//	}

	@Override
	public IContextButtonPadData getContextButtonPad(
	    IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);
		PictogramElement pe = context.getPictogramElement();

		setGenericContextButtonsProxy(data, pe,
		    ServiceUtil.getServicesAtLatestVersion(RuminaqBehaviorProvider.class,
		        GenericContextButtonPadDataExtension.class,
		        new ServiceFilterArgs() {
			        @Override
			        public List<?> getArgs() {
				        return Arrays.asList(getFeatureProvider(), context);
			        }
		        }).stream().findFirst()
		        .orElse(new GenericContextButtonPadDataExtension() {
			        @Override
			        public int getGenericContextButtons() {
				        return Constants.CONTEXT_BUTTON_DELETE;
			        }
		        }).getGenericContextButtons());

		ServiceUtil.getServicesAtLatestVersion(RuminaqBehaviorProvider.class,
		    DomainContextButtonPadDataExtension.class, new ServiceFilterArgs() {
			    @Override
			    public List<?> getArgs() {
				    return Arrays.asList(getFeatureProvider(), context);
			    }
		    }).stream().forEach(e -> {
			    data.getDomainSpecificContextButtons()
			        .addAll(e.getContextButtonPad(getFeatureProvider(), context));
		    });

		data.getPadLocation().setRectangle(
		    ServiceUtil.getServicesAtLatestVersion(RuminaqBehaviorProvider.class,
		        ContextButtonPadLocationExtension.class, new ServiceFilterArgs() {
			        @Override
			        public List<?> getArgs() {
				        return Arrays.asList(getFeatureProvider(), context);
			        }
		        }).stream().findFirst()
		        .orElse(new ContextButtonPadLocationExtension() {
			        @Override
			        public IRectangle getPadLocation(IRectangle rectangle) {
				        return data.getPadLocation().getRectangleCopy();
			        }
		        }).getPadLocation(data.getPadLocation().getRectangleCopy()));

		return data;
	}

	public void setGenericContextButtonsProxy(IContextButtonPadData data,
	    PictogramElement pe, int i) {
		super.setGenericContextButtons(data, pe, i);
	}

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		return Stream.of(getFeatureProvider().getCustomFeatures(context))
		    .filter(ServiceUtil
		        .getServicesAtLatestVersion(RuminaqBehaviorProvider.class,
		            ContextMenuEntryExtension.class, new ServiceFilterArgs() {
			            @Override
			            public List<?> getArgs() {
				            return Arrays.asList(getFeatureProvider(), context);
			            }
		            })
		        .stream().findFirst().orElse(new ContextMenuEntryExtension() {

			        @Override
			        public Predicate<ICustomFeature> isAvailable(
			            ICustomContext context) {
				        return new Predicate<ICustomFeature>() {
					        @Override
					        public boolean test(ICustomFeature arg0) {
						        return false;
					        }
				        };
			        }

		        }).isAvailable(context))
		    .map(cf -> {
			    ContextMenuEntry menuEntry = new ContextMenuEntry(cf, context);
			    menuEntry.setText(cf.getName());
			    return menuEntry;
		    }).toArray(IContextMenuEntry[]::new);
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		ICustomFeature feature = (new DoubleClickFeatureProvider(
		    getFeatureProvider())).getDoubleClickFeature(context);
		return feature != null ? feature : super.getDoubleClickFeature(context);
	}

	@Override
	public ICustomFeature getSingleClickFeature(ISingleClickContext context) {
		return super.getSingleClickFeature(context);
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		return ServiceUtil.getServicesAtLatestVersion(RuminaqBehaviorProvider.class,
		    DecoratorExtension.class, new ServiceFilterArgs() {
			    @Override
			    public List<?> getArgs() {
				    return Arrays.asList(getFeatureProvider(), pe);
			    }
		    }).stream().map(ext -> ext.getDecorators(pe)).flatMap(x -> x.stream())
		    .toArray(IDecorator[]::new);
	}

	@Override
	public PictogramElement getSelection(PictogramElement originalPe,
	    PictogramElement[] oldSelection) {
		return null;
	}
}
