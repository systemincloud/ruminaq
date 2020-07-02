package org.ruminaq.tasks.randomgenerator.gui.properties;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.model.ruminaq.ModelUtil;
import org.ruminaq.tasks.randomgenerator.gui.PropertySpecificComposite;
import org.ruminaq.tasks.randomgenerator.gui.ValueSaveListener;
import org.ruminaq.tasks.randomgenerator.impl.strategy.RandomGeneratorComplexStrategy;
import org.ruminaq.tasks.randomgenerator.model.randomgenerator.RandomGenerator;
import org.ruminaq.util.NumericUtil;
import org.ruminaq.util.RandomUtil;

public class PropertySpecificComplexComposite
    extends PropertySpecificComposite {

  private Button btnRect;
  private Button btnPolar;

  private CLabel lblCommonA;
  private Text txtCommonA;
  private CLabel lblCommonB;
  private Text txtCommonB;

  private CLabel lblPolarMag;
  private Text txtPolarMag;
  private CLabel lblPolarArg;
  private Text txtPolarArg;

  public PropertySpecificComplexComposite(ValueSaveListener saveListener,
      Composite specificRoot, PictogramElement pe,
      TransactionalEditingDomain ed) {
    super(saveListener, specificRoot, pe, ed);
    initLayout();
    initActions();
    initComponents();
    addStyles();
  }

  private void initLayout() {
    composite = new Composite(this.specificRoot, SWT.NONE);
    GridLayout gl = new GridLayout(4, false);
    gl.horizontalSpacing = 15;
    composite.setLayout(gl);

    btnRect = new Button(composite, SWT.RADIO);
    btnRect
        .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
    btnPolar = new Button(composite, SWT.RADIO);
    btnPolar
        .setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

    lblCommonA = new CLabel(composite, SWT.NONE);
    txtCommonA = new Text(composite, SWT.BORDER);
    GridData lytCommonA = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
        1);
    lytCommonA.minimumWidth = 75;
    lytCommonA.widthHint = 75;
    txtCommonA.setLayoutData(lytCommonA);

    lblPolarMag = new CLabel(composite, SWT.NONE);
    txtPolarMag = new Text(composite, SWT.BORDER);
    GridData lytPolarMag = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
        1);
    lytPolarMag.minimumWidth = 75;
    lytPolarMag.widthHint = 75;
    txtPolarMag.setLayoutData(lytPolarMag);

    lblCommonB = new CLabel(composite, SWT.NONE);
    txtCommonB = new Text(composite, SWT.BORDER);
    GridData lytCommonB = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
        1);
    lytCommonB.minimumWidth = 75;
    lytCommonB.widthHint = 75;
    txtCommonB.setLayoutData(lytCommonB);

    lblPolarArg = new CLabel(composite, SWT.NONE);
    txtPolarArg = new Text(composite, SWT.BORDER);
    GridData lytPolarArg = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
        1);
    lytPolarArg.minimumWidth = 75;
    lytPolarArg.widthHint = 75;
    txtPolarArg.setLayoutData(lytPolarArg);
  }

  private void initComponents() {
    btnRect.setText("Rectangular");
    btnPolar.setText("Polar");
    lblCommonA.setText("a:");
    lblCommonB.setText("b:");
    lblPolarMag.setText("Mag:");
    lblPolarArg.setText("Arg:");
  }

  private void initActions() {
    btnRect.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (((Button) e.widget).getSelection()) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof RandomGenerator) {
                RandomGenerator rg = (RandomGenerator) bo;
                rg.getSpecific().put(
                    RandomGeneratorComplexStrategy.COMPLEX_REPRESENTATION,
                    RandomGeneratorComplexStrategy.RECTANGULAR_REPRESENTATION);
              }
            }
          }, ed, "Change representation");
          setCommon();
        }
      }
    });
    btnPolar.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (((Button) e.widget).getSelection()) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof RandomGenerator) {
                RandomGenerator rg = (RandomGenerator) bo;
                rg.getSpecific().put(
                    RandomGeneratorComplexStrategy.COMPLEX_REPRESENTATION,
                    RandomGeneratorComplexStrategy.POLAR_REPRESENTATION);
              }
            }
          }, ed, "Change representation");
          setPolar();
        }
      }
    });
    txtCommonA.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        Shell shell = txtCommonA.getShell();
        boolean parse = RandomUtil.isRandomAlsoGV(txtCommonA.getText())
            || NumericUtil.isOneDimNumeric(txtCommonA.getText());
        if (parse) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof RandomGenerator) {
                RandomGenerator rg = (RandomGenerator) bo;
                rg.getSpecific().put(RandomGeneratorComplexStrategy.COMPLEX_A,
                    txtCommonA.getText());
              }
            }
          }, ed, "Change dimensions");
        } else
          MessageDialog.openError(shell, "Can't edit value",
              "Don't understant distribution");
      }
    });
    txtCommonA.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          saveListener.setFocus();
      }
    });
    txtCommonB.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        Shell shell = txtCommonB.getShell();
        boolean parse = RandomUtil.isRandomAlsoGV(txtCommonB.getText())
            || NumericUtil.isOneDimNumeric(txtCommonB.getText());
        if (parse) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof RandomGenerator) {
                RandomGenerator rg = (RandomGenerator) bo;
                rg.getSpecific().put(RandomGeneratorComplexStrategy.COMPLEX_B,
                    txtCommonB.getText());
              }
            }
          }, ed, "Change dimensions");
        } else
          MessageDialog.openError(shell, "Can't edit value",
              "Don't understant distribution");
      }
    });
    txtCommonB.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          saveListener.setFocus();
      }
    });
    txtPolarMag.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        Shell shell = txtPolarMag.getShell();
        boolean parse = RandomUtil.isRandomAlsoGV(txtPolarMag.getText())
            || NumericUtil.isOneDimNumeric(txtPolarMag.getText());
        if (parse) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof RandomGenerator) {
                RandomGenerator rg = (RandomGenerator) bo;
                rg.getSpecific().put(RandomGeneratorComplexStrategy.COMPLEX_MAG,
                    txtPolarMag.getText());
              }
            }
          }, ed, "Change dimensions");
        } else
          MessageDialog.openError(shell, "Can't edit value",
              "Don't understant distribution");
      }
    });
    txtPolarMag.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          saveListener.setFocus();
      }
    });
    txtPolarArg.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent event) {
        Shell shell = txtPolarArg.getShell();
        boolean parse = RandomUtil.isRandomAlsoGV(txtPolarArg.getText())
            || NumericUtil.isOneDimNumeric(txtPolarArg.getText());
        if (parse) {
          ModelUtil.runModelChange(new Runnable() {
            public void run() {
              Object bo = Graphiti.getLinkService()
                  .getBusinessObjectForLinkedPictogramElement(pe);
              if (bo == null)
                return;
              if (bo instanceof RandomGenerator) {
                RandomGenerator rg = (RandomGenerator) bo;
                rg.getSpecific().put(RandomGeneratorComplexStrategy.COMPLEX_ARG,
                    txtPolarArg.getText());
              }
            }
          }, ed, "Change dimensions");
        } else
          MessageDialog.openError(shell, "Can't edit value",
              "Don't understant distribution");
      }
    });
    txtPolarArg.addTraverseListener(new TraverseListener() {
      @Override
      public void keyTraversed(TraverseEvent event) {
        if (event.detail == SWT.TRAVERSE_RETURN)
          saveListener.setFocus();
      }
    });
  }

  private void setCommon() {
    txtPolarMag.setEnabled(false);
    txtPolarArg.setEnabled(false);
    txtCommonA.setEnabled(true);
    txtCommonB.setEnabled(true);
  }

  private void setPolar() {
    txtPolarMag.setEnabled(true);
    txtPolarArg.setEnabled(true);
    txtCommonA.setEnabled(false);
    txtCommonB.setEnabled(false);
  }

  private void addStyles() {
    composite
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnRect.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    btnPolar
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblCommonA
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblCommonB
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblPolarMag
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    lblPolarArg
        .setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
  }

  @Override
  public void initValues(EMap<String, String> eMap) {
    String rep = eMap
        .get(RandomGeneratorComplexStrategy.COMPLEX_REPRESENTATION);
    if (rep == null)
      eMap.put(RandomGeneratorComplexStrategy.COMPLEX_REPRESENTATION,
          RandomGeneratorComplexStrategy.DEFAULT_REPRESENTATION);

    String a = eMap.get(RandomGeneratorComplexStrategy.COMPLEX_A);
    if (a == null)
      eMap.put(RandomGeneratorComplexStrategy.COMPLEX_A,
          RandomGeneratorComplexStrategy.COMPLEX_DEFAULT_A);

    String b = eMap.get(RandomGeneratorComplexStrategy.COMPLEX_B);
    if (b == null)
      eMap.put(RandomGeneratorComplexStrategy.COMPLEX_B,
          RandomGeneratorComplexStrategy.COMPLEX_DEFAULT_B);

    String m = eMap.get(RandomGeneratorComplexStrategy.COMPLEX_MAG);
    if (m == null)
      eMap.put(RandomGeneratorComplexStrategy.COMPLEX_MAG,
          RandomGeneratorComplexStrategy.COMPLEX_DEFAULT_MAG);

    String g = eMap.get(RandomGeneratorComplexStrategy.COMPLEX_ARG);
    if (g == null)
      eMap.put(RandomGeneratorComplexStrategy.COMPLEX_ARG,
          RandomGeneratorComplexStrategy.COMPLEX_DEFAULT_ARG);
  }

  @Override
  public void refresh(final EMap<String, String> eMap) {
    String rep = eMap
        .get(RandomGeneratorComplexStrategy.COMPLEX_REPRESENTATION);
    if (rep == null || rep
        .equals(RandomGeneratorComplexStrategy.RECTANGULAR_REPRESENTATION)) {
      btnRect.setSelection(true);
      setCommon();
    } else if (rep
        .equals(RandomGeneratorComplexStrategy.POLAR_REPRESENTATION)) {
      btnPolar.setSelection(true);
      setPolar();
    }

    String a = eMap.get(RandomGeneratorComplexStrategy.COMPLEX_A);
    txtCommonA
        .setText(a != null ? eMap.get(RandomGeneratorComplexStrategy.COMPLEX_A)
            : RandomGeneratorComplexStrategy.COMPLEX_DEFAULT_A);

    String b = eMap.get(RandomGeneratorComplexStrategy.COMPLEX_A);
    txtCommonB
        .setText(b != null ? eMap.get(RandomGeneratorComplexStrategy.COMPLEX_B)
            : RandomGeneratorComplexStrategy.COMPLEX_DEFAULT_B);

    String m = eMap.get(RandomGeneratorComplexStrategy.COMPLEX_MAG);
    txtPolarMag.setText(
        m != null ? eMap.get(RandomGeneratorComplexStrategy.COMPLEX_MAG)
            : RandomGeneratorComplexStrategy.COMPLEX_DEFAULT_MAG);

    String g = eMap.get(RandomGeneratorComplexStrategy.COMPLEX_ARG);
    txtPolarArg.setText(
        g != null ? eMap.get(RandomGeneratorComplexStrategy.COMPLEX_ARG)
            : RandomGeneratorComplexStrategy.COMPLEX_DEFAULT_ARG);
  }
}
