package org.ruminaq.tasks.randomgenerator.properties;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.strategy.TextStrategy;
import org.ruminaq.tasks.randomgenerator.impl.strategy.TextStrategy.TextCase;
import org.ruminaq.tasks.randomgenerator.impl.strategy.TextStrategy.TextType;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.util.GlobalUtil;

public class PropertySpecificTextComposite extends PropertySpecificComposite {

	private CLabel lblType;
	private Combo  cmbTextType;
	private CLabel lblAlfaType;
	private Combo  cmbAlfaType;
	private CLabel lblLength;
	private Text   txtLength;
	
	public PropertySpecificTextComposite(
			ValueSaveListener saveListener,
			Composite specificRoot, 
			PictogramElement pe, 
			TransactionalEditingDomain ed) {
		super(saveListener, specificRoot, pe, ed);
		initLayout();
		initComponents();
		initActions();
		addStyles();
	}

	private void initLayout() {
		composite = new Composite(this.specificRoot, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		lblType = new CLabel(composite, SWT.NONE);
		cmbTextType = new Combo(composite, SWT.READ_ONLY);
		lblAlfaType = new CLabel(composite, SWT.NONE);
		cmbAlfaType = new Combo(composite, SWT.READ_ONLY);
		lblLength = new CLabel(composite, SWT.NONE);
		txtLength = new Text(composite, SWT.BORDER);
		GridData layoutLength = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		layoutLength.minimumWidth = 75; layoutLength.widthHint = 75;
		txtLength.setLayoutData(layoutLength);
	}
	
	private void initComponents() {
		lblType    .setText("Type:");
		List<String> types = new LinkedList<>();
		for(TextType v : TextType.values()) types.add(v.toString());
		cmbTextType.setItems(types.toArray(new String[types.size()]));

		lblAlfaType.setText("Case:");
		List<String> cases = new LinkedList<>();
		for(TextCase v : TextCase.values()) cases.add(v.toString());
		cmbAlfaType.setItems(cases.toArray(new String[cases.size()]));

		lblLength  .setText("Length:");
	}
	
	private void initActions() {
		cmbTextType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(cmbTextType.getText().equals(TextType.NUMERIC.toString())) { lblAlfaType.setVisible(false); cmbAlfaType.setVisible(false); }
				else                                                          { lblAlfaType.setVisible(true);  cmbAlfaType.setVisible(true);  }
				save();
			}
		});
		cmbAlfaType.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent e) { save(); } });
		txtLength.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent event) {
				Shell shell = txtLength.getShell();
				boolean parse = GlobalUtil.isIntegerAlsoGVandRand(txtLength.getText());
				if(parse) {
					ModelUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
							if (bo == null) return;
							if (bo instanceof RandomGenerator) {
								RandomGenerator rg = (RandomGenerator) bo;
								rg.getSpecific().put(TextStrategy.TEXT_LENGTH, txtLength.getText());
							}
						}
					}, ed, "Change dimensions");
				} else MessageDialog.openError(shell, "Can't edit value", "Don't understant lenght");
			}
		});
		txtLength.addTraverseListener(new TraverseListener() {
			@Override public void keyTraversed(TraverseEvent event) { if(event.detail == SWT.TRAVERSE_RETURN) specificRoot.setFocus(); }
		});
	}
	
	private void addStyles() {
		composite  .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblType    .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAlfaType.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblLength  .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtLength  .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	}

	public static final String TEXT_TYPE = "TEXT_TYPE";
	public static final String TEXT_CASE = "TEXT_CASE";
	public static final String TEXT_LENGTH = "TEXT_LENGTH";
	public static final int DEFAULT_TEXT_LENGTH = 5;
	
	@Override
	public void initValues(EMap<String, String> eMap) {
		String textType = eMap.get(TextStrategy.TEXT_TYPE);
		if(textType == null) eMap.put(TEXT_TYPE, TextType.ALPHANUMERIC.toString());
		String textCase = eMap.get(TextStrategy.TEXT_CASE);
		if(textCase == null) eMap.put(TEXT_CASE, TextCase.ANY.toString());
		String textLength = eMap.get(TextStrategy.TEXT_LENGTH);
		if(textLength == null) eMap.put(TEXT_LENGTH, Integer.toString(DEFAULT_TEXT_LENGTH));
	}

	@Override
	public void refresh(EMap<String, String> eMap) {
		String textType = eMap.get(TextStrategy.TEXT_TYPE);
		int j = 0;
		for(String b : cmbTextType.getItems()) { if(b.equals(textType)) break; j++; }
		cmbTextType.select(j);

		if(cmbTextType.getText().equals(TextType.NUMERIC.toString())) { lblAlfaType.setVisible(false); cmbAlfaType.setVisible(false); }
		else                                                          { lblAlfaType.setVisible(true);  cmbAlfaType.setVisible(true);  }

		String textCase = eMap.get(TextStrategy.TEXT_CASE);
		j = 0;
		for(String b : cmbAlfaType.getItems()) { if(b.equals(textCase)) break; j++; }
		cmbAlfaType.select(j);

		txtLength.setText(eMap.get(TextStrategy.TEXT_LENGTH));
	}
	
	private void save() {
		ModelUtil.runModelChange(new Runnable() {
			public void run() {
				Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
				if(bo == null) return;
				if (bo instanceof RandomGenerator) {
					RandomGenerator rg = (RandomGenerator) bo;
					rg.getSpecific().put(TextStrategy.TEXT_TYPE, cmbTextType.getText());
					rg.getSpecific().put(TextStrategy.TEXT_CASE, cmbAlfaType.getText());
				}
			}
		}, ed, "Change random generator");
	}
}
