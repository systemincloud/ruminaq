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
package org.ruminaq.gui.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.consts.Constants;
import org.ruminaq.gui.label.DirectEditLabelFeature;
import org.ruminaq.model.model.ruminaq.BaseElement;
import org.ruminaq.model.model.ruminaq.Task;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.tasks.TaskProvider;
import org.ruminaq.tasks.TasksManagerHandlerImpl;
import org.ruminaq.tasks.api.TaskManagerHandler;
import org.ruminaq.tasks.api.TasksExtensionHandler;

public class PropertyElementSection extends GFPropertySection implements ITabbedPropertyConstants {

	@Reference
	private TaskManagerHandler taskManager;

	private String    created = null;
	private Composite parent;

	private Composite composite;

	private CLabel    lblId;
	private Text      txtId;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		this.parent = parent;
	}

	private void create(Composite parent) {
		initLayout(parent);
		initActions();
		initComponents();
		addStyles();

		Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
		if (bo != null && bo instanceof Task) {
			taskManager.addToGeneralTab(
					composite,
					(Task) bo,
					getDiagramContainer().getDiagramBehavior().getEditingDomain());
		}
	}

	private void initLayout(Composite parent) {
		((GridData)parent.getLayoutData()).verticalAlignment = SWT.FILL;
		((GridData)parent.getLayoutData()).grabExcessVerticalSpace = true;

		composite = new Composite(parent,SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		lblId = new CLabel(composite, SWT.NONE);

		txtId = new Text(composite, SWT.BORDER);
		txtId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

	private void initActions() {
		txtId.addTraverseListener(new TraverseListener() {
			@Override public void keyTraversed(TraverseEvent event) {
		        if(event.detail == SWT.TRAVERSE_RETURN) {
					Shell shell = txtId.getShell();

					if (txtId.getText().length() < 1) {
						MessageDialog.openError(shell, "Can not edit value", "Please enter any text as element id.");
						return;
					} else if (txtId.getText().contains("\n")) {
						MessageDialog.openError(shell, "Can not edit value", "Line breakes are not allowed in class names.");
						return;
					} else if (DirectEditLabelFeature.hasId(getDiagram(), getSelectedPictogramElement(), txtId.getText())) {
						MessageDialog.openError(shell, "Can not edit value", "Model has already id " + txtId.getText() + ".");
						return;
					}

					TransactionalEditingDomain editingDomain = getDiagramContainer().getDiagramBehavior().getEditingDomain();

					ModelUtil.runModelChange(new Runnable() {
						@Override
                        public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
							if (bo == null) return;
							String id = txtId.getText();
							if (id != null) {
								if (bo instanceof BaseElement) {
									BaseElement element = (BaseElement) bo;
									element.setId(id);

									for(EObject o : getSelectedPictogramElement().getLink().getBusinessObjects()) {
										if(o instanceof ContainerShape && Graphiti.getPeService().getPropertyValue((PropertyContainer) o, Constants.LABEL_PROPERTY) != null) {
											UpdateContext context = new UpdateContext((ContainerShape) o);
											getDiagramTypeProvider().getFeatureProvider().updateIfPossible(context);
											break;
										}
									}
								}
							}
						}
					}, editingDomain, "Model Update");
				}
			}
		});
	}

	private void initComponents() {
		lblId.setText("Id:");
	}

	private void addStyles() {
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblId    .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtId    .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	}

	@Override
	public void refresh() {
		if(!getId(getSelectedPictogramElement()).equals(created)) {
			for(Control control : parent.getChildren()) control.dispose();
			create(parent);
			parent.layout();
			this.created = getId(getSelectedPictogramElement());
		}

		Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(getSelectedPictogramElement());
		if(bo == null) return;
		String name = ((BaseElement) bo).getId();
		txtId.setText(name == null ? "" : name);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
	}

	private String getId(PictogramElement pe) {
		if(pe == null) return null;
        EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
        if(bo instanceof BaseElement) {
        	BaseElement be = (BaseElement) bo;
        	return be.getId();
        }
		return null;
	}
}
