package org.ruminaq.tasks.randomgenerator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle.Control;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
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
import org.ruminaq.model.model.dt.Bool;
import org.ruminaq.model.model.dt.Complex32;
import org.ruminaq.model.model.dt.Complex64;
import org.ruminaq.model.model.dt.Decimal;
import org.ruminaq.model.model.dt.Float32;
import org.ruminaq.model.model.dt.Float64;
import org.ruminaq.model.model.dt.Int32;
import org.ruminaq.model.model.dt.Int64;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.randomgenerator.extension.RandomGeneratorExtensionManager;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.tasks.randomgenerator.properties.Complex32Property;
import org.ruminaq.util.GlobalUtil;

public class PropertySection implements IPropertySection, ValueSaveListener {

	private Composite   root;
	private CLabel      lblType;
	private Combo       cmbType;

	private CLabel      lblDims;
	private Text        txtDims;

	private CLabel      lblInterval;
	private Text        txtInterval;

	private Composite   specificRoot;
	private StackLayout specificStack;

	private Map<String, PropertySpecificComposite> specificComposites = new HashMap<>();

	private PropertySpecificComposite noSpecific;

	private PictogramElement pe;
	private IDiagramTypeProvider dtp;

	public PropertySection(Composite parent, PictogramElement pe, TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
		this.pe = pe; this.dtp = dtp;
		initLayout(parent);
		initActions(ed);
		initComponents(ed);
		addStyles();
	}

	private void initLayout(Composite parent) {
		((GridData)parent.getLayoutData()).verticalAlignment = SWT.FILL;
		((GridData)parent.getLayoutData()).grabExcessVerticalSpace = true;
		root = new Composite(parent, SWT.NULL);
		root.setLayout(new GridLayout(2, false));

		lblType = new CLabel(root, SWT.NONE);
		cmbType = new Combo(root, SWT.READ_ONLY);
		cmbType.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		lblDims = new CLabel(root, SWT.NONE);
		lblDims.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		txtDims = new Text(root, SWT.BORDER);
		GridData layoutDims = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		layoutDims.minimumWidth = 75;
		layoutDims.widthHint = 75;
		txtDims.setLayoutData(layoutDims);

		lblInterval = new CLabel(root, SWT.NONE);
		lblInterval.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		txtInterval = new Text(root, SWT.BORDER);
		GridData layoutInterval = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		layoutInterval.minimumWidth = 75;
		layoutInterval.widthHint = 75;
		txtInterval.setLayoutData(layoutInterval);

		specificRoot  = new Composite(root, SWT.NONE);
		specificStack = new StackLayout();
		specificRoot.setLayout(specificStack);
		GridData specificLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		specificRoot.setLayoutData(specificLayoutData);
	}

	private void initComponents(TransactionalEditingDomain ed) {
		lblType.setText("Type:");

		List<String> types = new LinkedList<>();
		types.add(Bool     .class.getSimpleName());
		types.add(Complex32.class.getSimpleName());
		types.add(Complex64.class.getSimpleName());
		types.add(Control  .class.getSimpleName());
		types.add(Decimal  .class.getSimpleName());
		types.add(Float32  .class.getSimpleName());
		types.add(Float64  .class.getSimpleName());
		types.add(Int32    .class.getSimpleName());
		types.add(Int64    .class.getSimpleName());
		types.add(org.ruminaq.model.model.dt.Text.class.getSimpleName());
		for(Class<? extends DataType> clazz : RandomGeneratorExtensionManager.INSTANCE.getDataTypes())
			types.add(clazz.getSimpleName());

		cmbType.setItems(types.toArray(new String[types.size()]));

		lblDims    .setText("Dimensions:");
		lblInterval.setText("Interval:");

		this.noSpecific = new PropertySpecificComposite(this, specificRoot, pe, ed) {
			{
				composite = new Composite(this.specificRoot, SWT.NONE);
				composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			}
			@Override public void initValues(EMap<String, String> eMap) { }
			@Override public void refresh(EMap<String, String> eMap)    { }
		};

		specificComposites.put(Complex32.class.getSimpleName(), Complex32Property.createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.put(Complex64.class.getSimpleName(), Complex64Property.createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.put(Control  .class.getSimpleName(), ControlProperty  .createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.put(Decimal  .class.getSimpleName(), DecimalProperty  .createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.put(Float32  .class.getSimpleName(), Float32Property  .createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.put(Float64  .class.getSimpleName(), Float64Property  .createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.put(Int32    .class.getSimpleName(), Int32Property    .createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.put(Int64    .class.getSimpleName(), Int64Property    .createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.put(Text     .class.getSimpleName(), TextProperty     .createSpecificComposite(this, specificRoot, pe, ed));
		specificComposites.putAll(RandomGeneratorExtensionManager.INSTANCE.getComposites(this, specificRoot, pe, ed));
	}

	private void initActions(final TransactionalEditingDomain ed) {
		cmbType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final DataType dt = DataTypeManager.INSTANCE.getDataTypeFromName(cmbType.getText());
				ModelUtil.runModelChange(new Runnable() {
					public void run() {
						Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
						if (bo == null) return;
						if (dt != null) {
							if (bo instanceof RandomGenerator) {
								RandomGenerator rg = (RandomGenerator) bo;
								rg.setDataType(dt);
								while(rg.getOutputPort().get(0).getDataType().size() > 0) rg.getOutputPort().get(0).getDataType().remove(0);
								rg.getOutputPort().get(0).getDataType().add(EcoreUtil.copy(dt));

								PropertySpecificComposite sc = specificComposites.get(ModelUtil.getName(dt.getClass(), false));
								if(sc == null) {
									specificStack.topControl = noSpecific.getComposite();
									txtDims.setEnabled(true);
								} else {
									sc.initValues(rg.getSpecific());
									sc.refresh(rg.getSpecific());
									specificStack.topControl = sc.getComposite();
									txtDims.setEnabled(sc.hasDimensions());
								}
								specificRoot.layout();

								UpdateContext context = new UpdateContext(pe);
								dtp.getFeatureProvider().updateIfPossible(context);
							}
						}
					}
				}, ed, "Model Update");
			}
		});
		txtDims.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent event) {
				Shell shell = txtDims.getShell();
				boolean parse = GlobalUtil.isDimensionsAlsoGVandRand(txtDims.getText());
				if(parse) {
					ModelUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
							if (bo == null) return;
							if (bo instanceof RandomGenerator) {
								RandomGenerator rg = (RandomGenerator) bo;
								rg.setDimensions(txtDims.getText());
							}
						}
					}, ed, "Change dimensions");
				} else MessageDialog.openError(shell, "Can't edit value", "Don't understant dimensions");
			}
		});
		txtDims.addTraverseListener(new TraverseListener() {
			@Override public void keyTraversed(TraverseEvent event) { if(event.detail == SWT.TRAVERSE_RETURN) root.setFocus(); }
		});
		txtInterval.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent event) {
				Shell shell = txtInterval.getShell();
				boolean parse = GlobalUtil.isTimeAlsoGVandRand(txtInterval.getText());
				if(parse) {
					ModelUtil.runModelChange(new Runnable() {
						public void run() {
							Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
							if (bo == null) return;
							if (bo instanceof RandomGenerator) {
								RandomGenerator rg = (RandomGenerator) bo;
								rg.setInterval(txtInterval.getText());
							}
						}
					}, ed, "Change interval");
				} else MessageDialog.openError(shell, "Can't edit value", "Don't understant interval value");
			}
		});
		txtInterval.addTraverseListener(new TraverseListener() {
			@Override public void keyTraversed(TraverseEvent event) { if(event.detail == SWT.TRAVERSE_RETURN) root.setFocus(); }
		});
	}

	private void addStyles() {
		root        .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblType     .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblDims     .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtDims     .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblInterval .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtInterval .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		specificRoot.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
	}

	@Override
	public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null || !(bo instanceof RandomGenerator)) return;
			RandomGenerator generator = (RandomGenerator) bo;

			int i = 0;
			for(String dt : cmbType.getItems()) { if(dt.equals(ModelUtil.getName(generator.getDataType().getClass(), false))) break; i++; }
			cmbType.select(i);

			txtDims.setText(generator.getDimensions());

			txtInterval.setText(generator.getInterval());

			PropertySpecificComposite sc = specificComposites.get(ModelUtil.getName(generator.getDataType().getClass(), false));
			if(sc == null) {
				specificStack.topControl = noSpecific.getComposite();
				txtDims.setEnabled(true);
			} else {
				specificStack.topControl = sc.getComposite();
				sc.refresh(generator.getSpecific());
				txtDims.setEnabled(sc.hasDimensions());
			}
			specificRoot.layout();
		}
	}

	@Override public void setFocus() { root.setFocus();	}
}
