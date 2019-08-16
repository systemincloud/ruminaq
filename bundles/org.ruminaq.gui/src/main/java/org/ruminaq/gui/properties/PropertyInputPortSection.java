/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.consts.Constants;
import org.ruminaq.model.ruminaq.InputPort;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.util.GlobalUtil;
import org.ruminaq.util.NumericUtil;

public class PropertyInputPortSection extends GFPropertySection
    implements ITabbedPropertyConstants {

	private Composite root;
	private Button btnAsync;
	private Button btnHoldLast;
	private Label lblQueueSize;
	private Text txtQueueSize;
	private CLabel lblGroup;
	private Spinner spnGroup;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControls(Composite parent,
	    TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		initLayout(parent);
		initComponents();
		initActions();
		addStyles();
	}

	private void initLayout(Composite parent) {
		((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
		((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;

		root = new Composite(parent, SWT.NULL);
		root.setLayout(new GridLayout(2, false));

		btnAsync = new Button(root, SWT.CHECK);
		btnHoldLast = new Button(root, SWT.CHECK);
		lblQueueSize = new Label(root, SWT.NONE);
		txtQueueSize = new Text(root, SWT.BORDER);
		GridData lytQueue = new GridData();
		lytQueue.minimumWidth = 40;
		lytQueue.widthHint = 40;
		txtQueueSize.setLayoutData(lytQueue);

		lblGroup = new CLabel(root, SWT.NONE);
		spnGroup = new Spinner(root, SWT.BORDER);
		GridData lytGroup = new GridData();
		lytGroup.minimumWidth = 80;
		lytGroup.widthHint = 80;
		spnGroup.setLayoutData(lytGroup);
	}

	private void initComponents() {
		btnAsync.setText("Asynchronous");
		btnHoldLast.setText("Hold last");
		lblQueueSize.setText("Queue size:");
		lblGroup.setText("Group:");
		spnGroup.setMinimum(-1);
		spnGroup.setMaximum(Integer.MAX_VALUE);
	}

	private void initActions() {
		btnAsync.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ModelUtil.runModelChange(new Runnable() {
					@Override
					public void run() {
						PictogramElement pe = getSelectedPictogramElement();
						if (pe == null)
							return;
						Object bo = Graphiti.getLinkService()
						    .getBusinessObjectForLinkedPictogramElement(pe);
						if (bo == null)
							return;
						if (bo instanceof InputPort) {
							InputPort p = (InputPort) bo;
							p.setAsynchronous(btnAsync.getSelection());
							UpdateContext context = new UpdateContext(
							    getSelectedPictogramElement());
							getDiagramTypeProvider().getFeatureProvider()
							    .updateIfPossible(context);
							btnHoldLast.setEnabled(!btnAsync.getSelection());
							spnGroup.setEnabled(!btnAsync.getSelection());
						}
					}
				}, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
				    "Model Update");
			}
		});
		btnHoldLast.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ModelUtil.runModelChange(new Runnable() {
					@Override
					public void run() {
						PictogramElement pe = getSelectedPictogramElement();
						if (pe == null)
							return;
						Object bo = Graphiti.getLinkService()
						    .getBusinessObjectForLinkedPictogramElement(pe);
						if (bo == null)
							return;
						if (bo instanceof InputPort) {
							InputPort p = (InputPort) bo;
							p.setHoldLast(btnHoldLast.getSelection());
						}
					}
				}, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
				    "Model Update");
			}
		});
		txtQueueSize.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent event) {
				Shell shell = txtQueueSize.getShell();
				boolean parse = (NumericUtil
				    .isOneDimPositiveInteger(txtQueueSize.getText())
				    && Integer.parseInt(txtQueueSize.getText()) != 0)
				    || GlobalUtil.isGlobalVariable(txtQueueSize.getText())
				    || Constants.INF.equals(txtQueueSize.getText());
				PictogramElement pe = getSelectedPictogramElement();
				if (pe == null)
					return;
				Object bo = Graphiti.getLinkService()
				    .getBusinessObjectForLinkedPictogramElement(pe);
				if (bo == null || !(bo instanceof InputPort))
					return;
				final InputPort ip = (InputPort) bo;
				if (parse) {
					ModelUtil.runModelChange(new Runnable() {
						@Override
						public void run() {
							ip.setQueueSize(txtQueueSize.getText());
						}
					}, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
					    "Change queque size");
				} else {
					MessageDialog.openError(shell, "Can't edit value",
					    "Don't understant value");
					txtQueueSize.setText(ip.getQueueSize());
				}
			}
		});
		txtQueueSize.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN)
					btnAsync.setFocus();
			}
		});
		spnGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ModelUtil.runModelChange(new Runnable() {
					@Override
					public void run() {
						PictogramElement pe = getSelectedPictogramElement();
						if (pe == null)
							return;
						Object bo = Graphiti.getLinkService()
						    .getBusinessObjectForLinkedPictogramElement(pe);
						if (bo == null)
							return;
						if (bo instanceof InputPort) {
							InputPort p = (InputPort) bo;
							p.setGroup(spnGroup.getSelection());
						}
					}
				}, getDiagramContainer().getDiagramBehavior().getEditingDomain(),
				    "Model Update");
				refresh();
			}
		});
	}

	private void addStyles() {
		root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblQueueSize
		    .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		txtQueueSize
		    .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblGroup
		    .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		spnGroup
		    .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe == null)
			return;
		Object bo = Graphiti.getLinkService()
		    .getBusinessObjectForLinkedPictogramElement(pe);
		if (bo == null)
			return;
		final InputPort p = (InputPort) bo;

		btnAsync.setSelection(p.isAsynchronous());
		btnHoldLast.setSelection(p.isHoldLast());
		txtQueueSize.setText(p.getQueueSize());

		spnGroup.setSelection(p.getGroup());

		btnHoldLast.setEnabled(!btnAsync.getSelection());
		spnGroup.setEnabled(!btnAsync.getSelection());
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
	}
}
