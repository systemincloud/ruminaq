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
package org.ruminaq.tasks.constant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.component.annotations.Reference;
import org.ruminaq.model.DataTypeManager;
import org.ruminaq.model.dt.Bool;
import org.ruminaq.model.dt.Complex32;
import org.ruminaq.model.dt.Complex64;
import org.ruminaq.model.dt.Control;
import org.ruminaq.model.dt.Decimal;
import org.ruminaq.model.dt.Float32;
import org.ruminaq.model.dt.Float64;
import org.ruminaq.model.dt.Int32;
import org.ruminaq.model.dt.Int64;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.constant.api.ConstantExtensionHandler;
import org.ruminaq.tasks.constant.api.PropertyValueComposite;
import org.ruminaq.tasks.constant.api.ValueSaveListener;
import org.ruminaq.tasks.constant.model.constant.Constant;
import org.ruminaq.tasks.constant.properties.BoolPropertyValue;
import org.ruminaq.tasks.constant.properties.ComplexPropertyValue;
import org.ruminaq.tasks.constant.properties.ControlPropertyValue;
import org.ruminaq.tasks.constant.properties.NumericPropertyValue;
import org.ruminaq.tasks.constant.properties.TextPropertyValue;

public class PropertySection implements IPropertySection, ValueSaveListener {

  @Reference
  private ConstantExtensionHandler extensions;

  private Composite root;
  private CLabel lblType;
  private Combo cmbType;

  private CLabel lblValue;
  private Composite valueRoot;
  private StackLayout valueStack;

  private Map<String, PropertyValueComposite> valueComposites = new HashMap<>();

  private PropertyValueComposite noValue;

  private PictogramElement pe;
  private IDiagramTypeProvider dtp;

  public PropertySection(Composite parent, PictogramElement pe,
      TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
    this.pe = pe;
    this.dtp = dtp;
    initLayout(parent);
    initActions(ed);
    initComponents(ed);
    addStyles();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(2, false));

    lblType = new CLabel(root, SWT.NONE);
    cmbType = new Combo(root, SWT.READ_ONLY);
    cmbType.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

    GridData layoutDims = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
    layoutDims.minimumWidth = 75;
    layoutDims.widthHint = 75;

    lblValue = new CLabel(root, SWT.NONE);
    lblValue.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

    valueRoot = new Composite(root, SWT.NONE);
    valueStack = new StackLayout();
    valueRoot.setLayout(valueStack);
    GridData valueLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
        1);
    valueRoot.setLayoutData(valueLayoutData);
  }

  private void initComponents(TransactionalEditingDomain ed) {
    lblType.setText("Type:");

    List<String> types = new LinkedList<>();
    types.add(Bool.class.getSimpleName());
    types.add(Complex32.class.getSimpleName());
    types.add(Complex64.class.getSimpleName());
    types.add(Control.class.getSimpleName());
    types.add(Decimal.class.getSimpleName());
    types.add(Float32.class.getSimpleName());
    types.add(Float64.class.getSimpleName());
    types.add(Int32.class.getSimpleName());
    types.add(Int64.class.getSimpleName());
    types.add(org.ruminaq.model.dt.Text.class.getSimpleName());
    for (Class<? extends DataType> clazz : extensions.getDataTypes()) {
      types.add(clazz.getSimpleName());
    }

    cmbType.setItems(types.toArray(new String[types.size()]));

    lblValue.setText("Value:");

    this.noValue = new PropertyValueComposite(this, valueRoot, pe, ed) {
      {
        composite = new Composite(this.valueRoot, SWT.NONE);
        composite.setBackground(
            Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
      }

      @Override
      public String getValue() {
        return "";
      }

      @Override
      public void refresh(String value) {
      }
    };

    valueComposites.put(Bool.class.getSimpleName(),
        new BoolPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Complex32.class.getSimpleName(),
        new ComplexPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Complex64.class.getSimpleName(),
        new ComplexPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Control.class.getSimpleName(),
        new ControlPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Int32.class.getSimpleName(),
        new NumericPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Int64.class.getSimpleName(),
        new NumericPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Float32.class.getSimpleName(),
        new NumericPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Float64.class.getSimpleName(),
        new NumericPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Decimal.class.getSimpleName(),
        new NumericPropertyValue(this, valueRoot, pe, ed));
    valueComposites.put(Text.class.getSimpleName(),
        new TextPropertyValue(this, valueRoot, pe, ed));

    valueComposites
        .putAll(extensions.getValueComposites(this, valueRoot, pe, ed));
  }

  private void initActions(final TransactionalEditingDomain ed) {
    cmbType.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        final DataType dt = DataTypeManager.INSTANCE
            .getDataTypeFromName(cmbType.getText());
        if (dt != null) {
          ModelUtil.runModelChange(() -> {
            Object bo = Graphiti.getLinkService()
                .getBusinessObjectForLinkedPictogramElement(pe);
            if (bo instanceof Constant) {
              Constant constant = (Constant) bo;
              constant.setDataType(dt);
              while (constant.getOutputPort().get(0).getDataType().size() > 0) {
                constant.getOutputPort().get(0).getDataType().remove(0);
              }
              constant.getOutputPort().get(0).getDataType()
                  .add(EcoreUtil.copy(dt));

              PropertyValueComposite vc = valueComposites
                  .get(ModelUtil.getName(dt.getClass(), false));
              if (vc == null) {
                valueStack.topControl = noValue.getComposite();
                constant.setValue(noValue.getValue());
              } else {
                vc.refresh(constant.getValue());
                constant.setValue(vc.getValue());
                valueStack.topControl = vc.getComposite();
              }
              valueRoot.layout();

              update();
            }
          }, ed, "Change constant value");
        }
      }
    });
  }

  private void addStyles() {
    root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblType.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblValue
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
    if (pe != null) {
      Object bo = Graphiti.getLinkService()
          .getBusinessObjectForLinkedPictogramElement(pe);
      if (!(bo instanceof Constant)) {
        return;
      }
      Constant constant = (Constant) bo;

      int i = 0;
      for (String dt : cmbType.getItems()) {
        if (dt.equals(
            ModelUtil.getName(constant.getDataType().getClass(), false))) {
          break;
        }
        i++;
      }
      cmbType.select(i);

      String value = constant.getValue();
      PropertyValueComposite vc = valueComposites
          .get(ModelUtil.getName(constant.getDataType().getClass(), false));
      if (vc != null) {
        vc.refresh(value);
        valueStack.topControl = vc.getComposite();
      }
      valueRoot.layout();
    }
  }

  @Override
  public void setFocus() {
    root.setFocus();
  }

  @Override
  public void update() {
    UpdateContext context = new UpdateContext(pe);
    dtp.getFeatureProvider().updateIfPossible(context);
  }
}
