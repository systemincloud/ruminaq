/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.ruminaq.tasks.randomgenerator.impl.strategy;

import java.util.ArrayList;
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
import org.ruminaq.tasks.randomgenerator.Port;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorStrategy;
import org.ruminaq.util.GlobalUtil;
import org.ruminaq.util.RandomUtil;
import org.slf4j.Logger;

public class TextStrategy extends RandomGeneratorStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(TextStrategy.class);

	public enum TextType {
		ALPHANUMERIC,
		ALPHABETIC,
		NUMERIC;
	}

	public enum TextCase {
		ANY,
		LOWER,
		UPPER;
	}

	public static final String TEXT_TYPE           = "TEXT_TYPE";
	public static final String TEXT_CASE           = "TEXT_CASE";
	public static final String TEXT_LENGTH         = "TEXT_LENGTH";
	public static final int    DEFAULT_TEXT_LENGTH = 5;

	private String textType;
	private String textCase;
	private String textLength;
	private int    length = -1;

	public TextStrategy(RandomGeneratorI task, EMap<String, String> eMap) {
		super(task);
		textType   = eMap.get(TEXT_TYPE);
		textCase   = eMap.get(TEXT_CASE);
		textLength = eMap.get(TEXT_LENGTH);
		if(!RandomUtil.containsRandom(task.getParent().replaceVariables(textLength)))
			length = Integer.parseInt(task.getParent().replaceVariables(textLength));
	}

	@Override public void generate(List<Integer> dims) {
		logger.trace("generating Text");
		int length = this.length != -1 ? this.length : Integer.parseInt(RandomUtil.replaceRandoms(task.getParent().replaceVariables(textLength), true, true));

		int n = 1;
		for(Integer i : dims) n *= i;
		List<String> values = new ArrayList<>(n);

		if(TextType.ALPHANUMERIC.toString().equals(textType))
			for(int i = 0; i < n; i++)
				values.add(TextStrategyUtil.generateRandomString(length, TextStrategyUtil.Mode.ALPHANUMERIC, textCase));
		else if(TextType.ALPHABETIC.toString().equals(textType))
			for(int i = 0; i < n; i++)
				values.add(TextStrategyUtil.generateRandomString(length, TextStrategyUtil.Mode.ALPHA, textCase));
		else if(TextType.NUMERIC.toString().equals(textType))
			for(int i = 0; i < n; i++)
				values.add(TextStrategyUtil.generateRandomString(length, TextStrategyUtil.Mode.NUMERIC, textCase));

		task.putData(Port.OUT, new TextI(values, dims));
	}

	public static PropertySpecificComposite createSpecificComposite(ValueSaveListener listener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		return new PropertySpecificComposite(listener, specificRoot, pe, ed) {
			private CLabel lblType;
			private Combo  cmbTextType;
			private CLabel lblAlfaType;
			private Combo  cmbAlfaType;
			private CLabel lblLength;
			private Text   txtLength;
			{
				// initLayout
				composite   = new Composite(this.specificRoot, SWT.NONE);
				composite.setLayout(new GridLayout(4, false));
				lblType     = new CLabel(composite, SWT.NONE);
				cmbTextType = new Combo (composite, SWT.READ_ONLY);
				lblAlfaType = new CLabel(composite, SWT.NONE);
				cmbAlfaType = new Combo (composite, SWT.READ_ONLY);
				lblLength   = new CLabel(composite, SWT.NONE);
				txtLength   = new Text  (composite, SWT.BORDER);
				GridData layoutLength = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				layoutLength.minimumWidth = 75; layoutLength.widthHint = 75;
				txtLength.setLayoutData(layoutLength);

				// initComponents
				lblType    .setText("Type:");
				List<String> types = new LinkedList<>();
				for(TextType v : TextType.values()) types.add(v.toString());
				cmbTextType.setItems(types.toArray(new String[types.size()]));

				lblAlfaType.setText("Case:");
				List<String> cases = new LinkedList<>();
				for(TextCase v : TextCase.values()) cases.add(v.toString());
				cmbAlfaType.setItems(cases.toArray(new String[cases.size()]));

				lblLength  .setText("Length:");

				// initActions
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

				// addStyles
				composite  .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				lblType    .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				lblAlfaType.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				lblLength  .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				txtLength  .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
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
			@Override
			public void initValues(EMap<String, String> eMap) {
				String textType = eMap.get(TextStrategy.TEXT_TYPE);
				if(textType == null) eMap.put(TEXT_TYPE, TextType.ALPHANUMERIC.toString());
				String textCase = eMap.get(TextStrategy.TEXT_CASE);
				if(textCase == null) eMap.put(TEXT_CASE, TextCase.ANY.toString());
				String textLength = eMap.get(TextStrategy.TEXT_LENGTH);
				if(textLength == null) eMap.put(TEXT_LENGTH, Integer.toString(DEFAULT_TEXT_LENGTH));
			}
			@Override public void refresh(final EMap<String, String> eMap) {
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
		};
	}
}
