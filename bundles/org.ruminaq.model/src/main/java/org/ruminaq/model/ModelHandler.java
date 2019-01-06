package org.ruminaq.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.ruminaq.model.FileService;
import org.ruminaq.model.model.ruminaq.BaseElement;
import org.ruminaq.model.model.ruminaq.MainTask;
import org.ruminaq.model.model.ruminaq.RuminaqFactory;
import org.ruminaq.model.util.ModelUtil;

public class ModelHandler {
	private static Map<Diagram, MainTask> modelMap = new HashMap<>();

	public static MainTask getModel(final Diagram diagram, final IFeatureProvider iFeatureProvider) {
		if(modelMap.containsKey(diagram)) return modelMap.get(diagram);
		else {
			MainTask mt = null;

			if(diagram.getLink() != null) mt = (MainTask) diagram.getLink().getBusinessObjects().get(0);

			if(mt == null) {
				final MainTask mtToCreate = RuminaqFactory.eINSTANCE.createMainTask();
				mt = mtToCreate;
				ModelUtil.runModelChange(new Runnable() {
					public void run() {
						FileService.saveModelToDiagramFile(mtToCreate, diagram);
						iFeatureProvider.link(diagram, mtToCreate);
					}
				}, iFeatureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain(), "Model Update");
			}

			modelMap.put(diagram, mt);
			return mt;
		}
	}

	public static boolean containsID(MainTask model, String value) {
		for(BaseElement be : model.getTask())
			if(be.getId().equals(value)) return true;

		for(BaseElement be : model.getInputPort())
			if(be.getId().equals(value)) return true;

		for(BaseElement be : model.getOutputPort())
			if(be.getId().equals(value)) return true;

		return false;
	}

}
