package org.ruminaq.tasks.randomgenerator.properties;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.strategy.RandomGeneratorNumericStrategy;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.util.RandomUtil;

public class PropertySpecificNumericComposite extends PropertySpecificComposite {

	private CLabel lblDistribution;
	private Text   txtDistribution;

	public PropertySpecificNumericComposite(ValueSaveListener saveListener,	Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		super(saveListener, specificRoot, pe, ed);
		initLayout();
		initComponents();
		initActions();
		addStyles();
	}

	private void initLayout() {
		composite = new Composite(this.specificRoot, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		lblDistribution = new CLabel(composite, SWT.NONE);
		txtDistribution = new Text  (composite, SWT.BORDER);
		GridData layoutDist = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		layoutDist.minimumWidth = 75; layoutDist.widthHint = 75;
		txtDistribution.setLayoutData(layoutDist);
	}

	private void initComponents() {
		lblDistribution.setText("Value:");
	}

	private void initActions() {
		txtDistribution.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent event) {
				Shell shell = txtDistribution.getShell();
				boolean parse = RandomUtil.isRandomAlsoGV(txtDistribution.getText()) || checkIfValue(txtDistribution.getText());
				if(parse) {
					ModelUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
							if (bo == null) return;
							if (bo instanceof RandomGenerator) {
								RandomGenerator rg = (RandomGenerator) bo;
								rg.getSpecific().put(RandomGeneratorNumericStrategy.NUMERIC_DISTRIBUTION, txtDistribution.getText());
							}
						}
					}, ed, "Change dimensions");
				} else MessageDialog.openError(shell, "Can't edit value", "Don't understant value");
			}
		});
		txtDistribution.addTraverseListener(new TraverseListener() {
			@Override public void keyTraversed(TraverseEvent event) { if(event.detail == SWT.TRAVERSE_RETURN) saveListener.setFocus(); }
		});
	}

	protected boolean checkIfValue(String value) { return false; }

	private void addStyles() {
		composite      .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		lblDistribution.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		txtDistribution.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	@Override
	public void initValues(EMap<String, String> eMap) {
		String nd = eMap.get(RandomGeneratorNumericStrategy.NUMERIC_DISTRIBUTION);
		if(nd == null) eMap.put(RandomGeneratorNumericStrategy.NUMERIC_DISTRIBUTION, RandomGeneratorNumericStrategy.DEFAULT_DISTRIBUTION);
	}

	@Override
	public void refresh(final EMap<String, String> eMap) {
		String tmp = eMap.get(RandomGeneratorNumericStrategy.NUMERIC_DISTRIBUTION);
		txtDistribution.setText(tmp != null ? eMap.get(RandomGeneratorNumericStrategy.NUMERIC_DISTRIBUTION) : RandomGeneratorNumericStrategy.DEFAULT_DISTRIBUTION);
	}
}
