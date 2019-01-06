package org.ruminaq.tasks.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.breakpoints.AbstractJavaBreakpointEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.ruminaq.tasks.debug.ui.InternalPortBreakpoint.SuspendPolicy;

@SuppressWarnings("restriction")
public class InternalPortBreakpointEditor extends AbstractJavaBreakpointEditor {

    public static final String HIT_COUNT_NOT_VALID_MSG = "Hit count must be positive integer";

    private InternalPortBreakpoint breakpoint;

    private Composite root;

    private Button    btnHitCount;
    private Text      txtHitCountText;
    private Composite radios;
    private Button    btnSuspendPort;
    private Button    btnSuspendAll;

    enum Properties {
        PROP_HIT_COUNT_ENABLED,
        PROP_HIT_COUNT,
        PROP_SUSPEND_POLICY
    }

    @Override
    public Control createControl(Composite parent) {
        initLayout(parent);
        initActions();

        return root;
    }

    private void initLayout(Composite parent) {
        root            = SWTFactory.createComposite(parent, parent.getFont(), 4, 1, 0, 0, 0);
        btnHitCount     = SWTFactory.createCheckButton(root, processMnemonics("Hit count:"), null, false, 1);
        btnHitCount.setLayoutData(new GridData());
        txtHitCountText = SWTFactory.createSingleText(root, 1);
        GridData gd = (GridData) txtHitCountText.getLayoutData();
        gd.minimumWidth = 50;

        SWTFactory.createLabel(root, "", 1);

        radios = SWTFactory.createComposite(root, root.getFont(), 2, 1, GridData.FILL_HORIZONTAL, 0, 0);
        btnSuspendPort = SWTFactory.createRadioButton(radios, processMnemonics("Suspend Port"), 1);
        btnSuspendPort.setLayoutData(new GridData());
        btnSuspendAll  = SWTFactory.createRadioButton(radios, processMnemonics("Suspend Runner"), 1);
        btnSuspendAll.setLayoutData(new GridData());
    }

    private void initActions() {
        btnHitCount.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent event) {
                boolean enabled = btnHitCount.getSelection();
                txtHitCountText.setEnabled(enabled);
                if(enabled) txtHitCountText.setFocus();
                else        setDirty(Properties.PROP_HIT_COUNT_ENABLED.ordinal());
            }});
        txtHitCountText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)                  { setDirty(Properties.PROP_HIT_COUNT.ordinal()); }});
        btnSuspendPort.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent e) { setDirty(Properties.PROP_SUSPEND_POLICY.ordinal()); }});
        btnSuspendAll.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent e) { setDirty(Properties.PROP_SUSPEND_POLICY.ordinal()); }});
        root.addDisposeListener(new DisposeListener() {
            @Override public void widgetDisposed(DisposeEvent e)   { dispose(); }});
    }


    @Override public void setInput(Object breakpoint) throws CoreException {
        try {
            suppressPropertyChanges(true);
            if (breakpoint instanceof InternalPortBreakpoint) setBreakpoint((InternalPortBreakpoint) breakpoint);
            else                                              setBreakpoint(null);
        } finally { suppressPropertyChanges(false); }
    }

    @Override public Object getInput() { return breakpoint; }

    protected void setBreakpoint(InternalPortBreakpoint bp) throws CoreException {
        this.breakpoint = bp;
        boolean enabled     = false;
        boolean hasHitCount = false;
        String  text        = "";
        boolean suspendPort = true;
        if(breakpoint != null) {
            enabled = true;
            int hitCount = breakpoint.getHitCount();
            if(hitCount > 0) {
                text = new Integer(hitCount).toString();
                hasHitCount = true;
            }
            suspendPort = breakpoint.getSuspendPolicy() == InternalPortBreakpoint.SuspendPolicy.SUSPEND_PORT;
        }
        btnHitCount    .setEnabled(enabled);
        btnHitCount    .setSelection(enabled & hasHitCount);
        txtHitCountText.setEnabled(hasHitCount);
        txtHitCountText.setText(text);
        btnSuspendPort .setEnabled(enabled);
        btnSuspendAll  .setEnabled(enabled);
        btnSuspendPort .setSelection(suspendPort);
        btnSuspendAll  .setSelection(!suspendPort);
        setDirty(false);
    }

    protected InternalPortBreakpoint getBreakpoint() { return breakpoint; }

    @Override public void setFocus() { }

    @Override
    public void doSave() throws CoreException {
        if(breakpoint != null) {
            SuspendPolicy suspendPolicy = InternalPortBreakpoint.SuspendPolicy.SUSPEND_PORT;
            if(btnSuspendAll.getSelection()) suspendPolicy = InternalPortBreakpoint.SuspendPolicy.SUSPEND_RUNNER;
            breakpoint.setSuspendPolicy(suspendPolicy);
            int hitCount = -1;
            if(btnHitCount.getSelection()) {
                try { hitCount = Integer.parseInt(txtHitCountText.getText());
                } catch (NumberFormatException e) { throw new CoreException(new Status(IStatus.ERROR, JDIDebugUIPlugin.getUniqueIdentifier(), IStatus.ERROR, HIT_COUNT_NOT_VALID_MSG, e)); }
            }
            breakpoint.setHitCount(hitCount);
        }
        setDirty(false);
    }

    @Override
    public IStatus getStatus() {
        if(btnHitCount.getSelection()) {
            String hitCountText = txtHitCountText.getText();
            int hitCount = -1;
            try { hitCount = Integer.parseInt(hitCountText);
            } catch (NumberFormatException e1) { return new Status(IStatus.ERROR, JDIDebugUIPlugin.getUniqueIdentifier(), IStatus.ERROR, HIT_COUNT_NOT_VALID_MSG, null); }
            if(hitCount < 1) {return new Status(IStatus.ERROR, JDIDebugUIPlugin.getUniqueIdentifier(), IStatus.ERROR, HIT_COUNT_NOT_VALID_MSG, null); }
        }
        return Status.OK_STATUS;
    }

    protected Button createSusupendPropertyEditor(Composite parent, String text, final int propId) {
        Button button = new Button(parent, SWT.CHECK);
        button.setFont(parent.getFont());
        button.setText(text);
        GridData gd = new GridData(SWT.BEGINNING);
        button.setLayoutData(gd);
        button.addSelectionListener(new SelectionAdapter() {
            @Override public void widgetSelected(SelectionEvent e) { setDirty(propId); }
        });
        return button;
    }
}
