/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.gui.properties;

import java.text.Collator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public abstract class AbstractParametersSection extends GFPropertySection
    implements ITabbedPropertyConstants {

  protected Composite root;

  protected Table tblParameters;
  protected TableColumn tblclParametersKey;
  protected TableColumn tblclParametersValue;

  protected TableEditor tblEdParameters;

  /**
   * @wbp.parser.entryPoint
   */
  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage tabbedPropertySheetPage) {
    super.createControls(parent, tabbedPropertySheetPage);

    initLayout(parent);
    initActions();
    initComponents();
    addStyles();
  }

  private void initLayout(Composite parent) {
    ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).horizontalAlignment = SWT.FILL;
    ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
    ((GridData) parent.getLayoutData()).grabExcessHorizontalSpace = true;

    root = new Composite(parent, SWT.NULL);
    root.setLayout(new GridLayout(1, false));

    tblParameters = new Table(root, SWT.FULL_SELECTION | SWT.MULTI | SWT.FILL);
    tblParameters
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    tblclParametersKey = new TableColumn(tblParameters, SWT.NONE);
    tblclParametersValue = new TableColumn(tblParameters, SWT.NONE);

    tblEdParameters = new TableEditor(tblParameters);
  }

  protected boolean isDefault() {
    return false;
  }

  private void initActions() {
    tblParameters.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Control oldEditor = tblEdParameters.getEditor();
        if (oldEditor != null)
          oldEditor.dispose();

        TableItem item = (TableItem) e.item;
        if (item == null)
          return;

        final Text newEditor = new Text(tblParameters, SWT.NONE);
        newEditor.setText(item.getText(1));
        newEditor.addTraverseListener(new TraverseListener() {
          @Override
          public void keyTraversed(TraverseEvent event) {
            switch (event.detail) {
              case SWT.TRAVERSE_RETURN:
                String key = tblEdParameters.getItem().getText(0);
                String newValue = tblEdParameters.getItem().getText(1);
                saveParameter(key, newValue);
                ((Text) tblEdParameters.getEditor()).dispose();
                break;
              case SWT.TRAVERSE_ESCAPE:
                String actual = getActualParams()
                    .get(tblEdParameters.getItem().getText(0));
                String tmp = actual != null ? actual : "";
                tblEdParameters.getItem().setText(1, tmp);
                ((Text) tblEdParameters.getEditor()).dispose();
                break;
              default:
                break;
            }
          }
        });
        newEditor.addModifyListener(new ModifyListener() {
          @Override
          public void modifyText(ModifyEvent me) {
            Text text = (Text) tblEdParameters.getEditor();
            tblEdParameters.getItem().setText(1, text.getText());
          }
        });
        newEditor.selectAll();
        newEditor.setFocus();
        tblEdParameters.setEditor(newEditor, item, 1);
      }
    });
  }

  private void initComponents() {
    tblParameters.setHeaderVisible(true);
    tblParameters.setLinesVisible(true);

    tblclParametersKey.setText("Key");
    tblclParametersValue.setText("Value");

    for (TableColumn tc : tblParameters.getColumns())
      tc.pack();

    tblEdParameters.horizontalAlignment = SWT.LEFT;
    tblEdParameters.grabHorizontal = true;
    tblEdParameters.minimumWidth = 50;
  }

  private void addStyles() {
    root.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void refresh() {
    if (tblParameters.isDisposed())
      return;
    tblParameters.removeAll();
    Map<String, String> params = getActualParams();
//		Map<String, String> defaultParams = getDefaultParams();
    for (Entry<String, String> param : params.entrySet()) {
      TableItem item = new TableItem(tblParameters, SWT.NONE);
      item.setText(new String[] { param.getKey(), param.getValue() });
    }
    sortParameters();
    for (TableColumn tc : tblParameters.getColumns())
      tc.pack();
    root.layout();
  }

  private void sortParameters() {
    TableItem[] items = tblParameters.getItems();
    Collator collator = Collator.getInstance(Locale.getDefault());
    for (int i = 1; i < items.length; i++) {
      String value1 = items[i].getText(0);
      for (int j = 0; j < i; j++) {
        String value2 = items[j].getText(0);
        if (collator.compare(value1, value2) < 0) {
          String[] values = { items[i].getText(0), items[i].getText(1) };
          items[i].dispose();
          TableItem item = new TableItem(tblParameters, SWT.NONE, j);
          item.setText(values);
          items = tblParameters.getItems();
          break;
        }
      }
    }
  }

  protected abstract Map<String, String> getActualParams();

  protected Map<String, String> getDefaultParams() {
    return null;
  }

  protected abstract void saveParameter(String key, String value);
}
