/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.ruminaq.eclipse.EclipseUtil;
import org.ruminaq.gui.model.diagram.RuminaqDiagram;
import org.ruminaq.model.ruminaq.Parameter;
import org.ruminaq.util.WidgetSelectedSelectionListener;

/**
 * Parameters property tab.
 *
 * @author Marek Jagielski
 */
public abstract class AbstractParametersSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  private static final int COLUMN_WIDTH_MINIMAL = 50;

  protected Composite root;

  protected Table table;
  protected TableColumn columnKey;
  protected TableColumn columnValue;

  protected TableEditor tblEdParameters;

  private static String getValue(Parameter p) {
    return Optional.of(p).filter(Parameter::isDefault)
        .map(Parameter::getDefaultValue).orElseGet(p::getValue);
  }

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initActions();
    initComponents();
  }

  protected RuminaqDiagram getRuminaqDiagram() {
    return (RuminaqDiagram) getDiagram();
  }

  protected void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).horizontalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    ((GridData) parent.getLayoutData()).grabExcessHorizontalSpace = true;

    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(1, false));

    table = new Table(root, SWT.FULL_SELECTION | SWT.MULTI | SWT.FILL);
    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    columnKey = new TableColumn(table, SWT.NONE);
    columnValue = new TableColumn(table, SWT.NONE);

    tblEdParameters = new TableEditor(table);
  }

  private void initActions() {
    table.addSelectionListener(
        (WidgetSelectedSelectionListener) (SelectionEvent e) -> {
          disposeEditor();
          createEditor((TableItem) e.item);
        });
  }

  private void disposeEditor() {
    Optional.ofNullable(tblEdParameters.getEditor())
        .ifPresent(Control::dispose);
  }
  
  private void createEditor(TableItem item) {
    Text newEditor = new Text(table, SWT.NONE);
    newEditor.setText(item.getText(1));
    newEditor.addTraverseListener((TraverseEvent event) -> {
      switch (event.detail) {
        case SWT.TRAVERSE_RETURN:
          saveSelectedParameter();
          tblEdParameters.getEditor().dispose();
          break;
        case SWT.TRAVERSE_ESCAPE:
          setActualParameter();
          tblEdParameters.getEditor().dispose();
          break;
        default:
          break;
      }
    });
    newEditor.addModifyListener((ModifyEvent me) -> {
      Text text = (Text) tblEdParameters.getEditor();
      tblEdParameters.getItem().setText(1, text.getText());
    });
    newEditor.selectAll();
    newEditor.setFocus();
    tblEdParameters.setEditor(newEditor, item, 1);
  }

  private void initComponents() {
    table.setHeaderVisible(true);
    table.setLinesVisible(true);

    columnKey.setText("Key");
    columnValue.setText("Value");

    tblEdParameters.horizontalAlignment = SWT.LEFT;
    tblEdParameters.grabHorizontal = true;
    tblEdParameters.minimumWidth = COLUMN_WIDTH_MINIMAL;
  }

  @Override
  public void refresh() {
    if (!table.isDisposed()) {
      table.removeAll();
      getParameters().forEach((Parameter param) -> {
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(new String[] { param.getKey(), param.getValue() });
      });
      EclipseUtil.sortTable(table, 0);
      Stream.of(table.getColumns()).forEach(TableColumn::pack);
      root.layout();
    }
  }

  private void saveSelectedParameter() {
    saveParameter(tblEdParameters.getItem().getText(0),
        tblEdParameters.getItem().getText(1));
  }

  private void setActualParameter() {
    tblEdParameters.getItem().setText(1, getParameters().stream()
        .filter(p -> p.getKey().equals(tblEdParameters.getItem().getText(0)))
        .findFirst().map(AbstractParametersSection::getValue).orElse(""));
  }

  protected abstract Collection<Parameter> getParameters();

  protected abstract void saveParameter(String key, String value);
}
