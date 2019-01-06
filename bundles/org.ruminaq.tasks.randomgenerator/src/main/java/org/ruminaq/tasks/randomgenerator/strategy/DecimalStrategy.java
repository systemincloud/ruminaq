package org.ruminaq.tasks.randomgenerator.strategy;

import java.math.BigDecimal;
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
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.tasks.randomgenerator.Port;
import org.ruminaq.tasks.randomgenerator.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.RandomGeneratorI;
import org.ruminaq.util.GlobalUtil;
import org.ruminaq.util.NumericUtil;
import org.ruminaq.util.RandomUtil;
import org.slf4j.Logger;

public class DecimalStrategy extends RandomGeneratorNumericStrategy {

	private final Logger logger = RunnerLoggerFactory.getLogger(Int32Strategy.class);

	public static final String DECIMAL_NUMBER         = "DECIMAL_NUMBER";
	public static final int    DEFAULT_DECIMAL_NUMBER = 2;

	private String textScale;
	private int    scale = -1;

	public DecimalStrategy(RandomGeneratorI task, EMap<String, String> eMap, List<Integer> dims) {
		super(task, eMap, dims);
		this.textScale = eMap.get(DECIMAL_NUMBER);
		if(this.textScale == null) this.textScale = Integer.toString(DEFAULT_DECIMAL_NUMBER);
		if(!RandomUtil.containsRandom(task.getParent().replaceVariables(textScale)))
			this.scale = Integer.parseInt(task.getParent().replaceVariables(textScale));
	}

	@Override public void generateRandom(List<Integer> dims) {
		logger.trace("generating Int32");

		int n = 1;
		for(Integer i : dims) n *= i;
		List<BigDecimal> values = new ArrayList<>(n);

		int scale = this.scale != -1 ? this.scale : Integer.parseInt(RandomUtil.replaceRandoms(task.getParent().replaceVariables(textScale), true, true));

		for(int i = 0; i < n; i++)
			values.add(new BigDecimal(distribution.getNext()).setScale(scale, BigDecimal.ROUND_HALF_UP));

		task.putData(Port.OUT, new DecimalI(values, dims));
	}

	public static PropertySpecificComposite createSpecificComposite(ValueSaveListener listener, Composite specificRoot, PictogramElement pe, TransactionalEditingDomain ed) {
		return new PropertySpecificNumericComposite(listener, specificRoot, pe, ed) {
			private CLabel lblDecimalNumber;
			private Text   txtDecimalNumber;
			{
				// initLayout
				lblDecimalNumber = new CLabel(composite, SWT.NONE);
				txtDecimalNumber = new Text  (composite, SWT.BORDER);
				GridData layoutDecimalNumber = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
				layoutDecimalNumber.minimumWidth = 75; layoutDecimalNumber.widthHint = 75;
				txtDecimalNumber.setLayoutData(layoutDecimalNumber);

				// initComponents
				lblDecimalNumber.setText("Number of Decimals:");

				// initActions
				txtDecimalNumber.addFocusListener(new FocusAdapter() {
					@Override public void focusLost(FocusEvent event) {
						Shell shell = txtDecimalNumber.getShell();
						boolean parse = GlobalUtil.isIntegerAlsoGVandRand(txtDecimalNumber.getText());
						if(parse) {
							ModelUtil.runModelChange(new Runnable() {
								public void run() {
									Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
									if (bo == null) return;
									if (bo instanceof RandomGenerator) {
										RandomGenerator rg = (RandomGenerator) bo;
										rg.getSpecific().put(DecimalStrategy.DECIMAL_NUMBER, txtDecimalNumber.getText());
									}
								}
							}, ed, "Change dimensions");
						} else MessageDialog.openError(shell, "Can't edit value", "Don't understant dimensions");
					}
				});
				txtDecimalNumber.addTraverseListener(new TraverseListener() {
					@Override public void keyTraversed(TraverseEvent event) { if(event.detail == SWT.TRAVERSE_RETURN) saveListener.setFocus(); }
				});

				// addStyles
				lblDecimalNumber.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
				txtDecimalNumber.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			}
			@Override
			public void initValues(EMap<String, String> eMap) {
				super.refresh(eMap);
				String number = eMap.get(DecimalStrategy.DECIMAL_NUMBER);
				if(number == null) eMap.put(DECIMAL_NUMBER, Integer.toString(DEFAULT_DECIMAL_NUMBER));
			}
			@Override public void refresh(final EMap<String, String> eMap) {
				super.refresh(eMap);
				txtDecimalNumber.setText(eMap.get(DecimalStrategy.DECIMAL_NUMBER));
			}
			@Override protected boolean checkIfValue(String value) { return NumericUtil.isMultiDimsNumeric(value); }
		};
	}

	@Override protected boolean isValue(String value) { return NumericUtil.isMultiDimsNumeric(value); }

	@Override protected DataI getDataOfValue(String value, List<Integer> dims) {
		String[] vs = NumericUtil.getMutliDimsValues(value);
		int n = 1;
		for(Integer i : dims) n *= i;
		List<BigDecimal> values = new LinkedList<>();

		if(vs.length == 1) {
			for(int i = 0; i < n; i++)
				values.add(new BigDecimal(vs[0]));
			return new DecimalI(values, dims);
		} else {
			List<Integer> dims2 = NumericUtil.getMutliDimsNumericDimensions(value);
			for(int i = 0; i < vs.length; i++)
				values.add(new BigDecimal(vs[i]));
			return new DecimalI(values, dims2);
		}
	}
}
