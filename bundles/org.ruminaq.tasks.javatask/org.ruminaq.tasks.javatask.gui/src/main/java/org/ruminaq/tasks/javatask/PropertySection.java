package org.ruminaq.tasks.javatask;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ruminaq.consts.Constants;
import org.ruminaq.eclipse.util.ConstantsUtil;
import org.ruminaq.model.util.ModelUtil;
import org.ruminaq.tasks.api.IPropertySection;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.features.UpdateFeature;
import org.ruminaq.tasks.javatask.ui.wizards.CreateJavaTaskListener;
import org.ruminaq.tasks.javatask.ui.wizards.CreateJavaTaskWizard;
import org.ruminaq.util.EclipseUtil;

public class PropertySection implements IPropertySection, CreateJavaTaskListener {

    private Composite root;
    private CLabel    lblClassSelect;
    private Text      txtClassName;
    private Button    btnClassSelect;
    private Button    btnClassNew;

    private PictogramElement pe;
    private TransactionalEditingDomain ed;
    private IDiagramTypeProvider dtp;

    public PropertySection(Composite parent, PictogramElement pe, TransactionalEditingDomain ed, IDiagramTypeProvider dtp) {
        this.pe  = pe;
        this.ed  = ed;
        this.dtp = dtp;

        initLayout(parent);
        initComponents();
        initActions(pe, ed, dtp);
        addStyles();
    }

    private void initLayout(Composite parent) {
        ((GridData) parent.getLayoutData()).verticalAlignment = SWT.FILL;
        ((GridData) parent.getLayoutData()).grabExcessVerticalSpace = true;
        root = new Composite(parent,SWT.NULL);
        root.setLayout(new GridLayout(4, false));

        lblClassSelect = new CLabel(root, SWT.NONE);
        txtClassName   = new Text(root, SWT.BORDER);
        txtClassName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnClassSelect = new Button(root, SWT.NONE);
        btnClassNew    = new Button(root, SWT.NONE);
    }

    private void initComponents() {
        lblClassSelect.setText("Java Task Class:");
        btnClassSelect.setText("Select class");
        btnClassNew   .setText("Create");
    }

    private void save() {
        Shell shell = txtClassName.getShell();
        boolean parse = new UpdateFeature(dtp.getFeatureProvider()).load(txtClassName.getText());
        if(parse) {
            ModelUtil.runModelChange(new Runnable() {
                public void run() {
                    Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
                    if (bo == null) return;
                    if (bo instanceof JavaTask) {
                        JavaTask javaTask = (JavaTask) bo;
                        javaTask.setImplementationClass(txtClassName.getText());
                        UpdateContext context = new UpdateContext(pe);
                        dtp.getFeatureProvider().updateIfPossible(context);
                    }
                }
            }, ed, "Set Java Class");
        } else MessageDialog.openError(shell, "Can't edit value", "Class not found or incorrect.");
    }

    private void initActions(final PictogramElement pe, final TransactionalEditingDomain ed, final IDiagramTypeProvider dtp) {
        txtClassName.addTraverseListener(new TraverseListener() {
            @Override public void keyTraversed(TraverseEvent event) { if(event.detail == SWT.TRAVERSE_RETURN) save(); }
        });
        btnClassSelect.addSelectionListener(new SelectionAdapter() { public void widgetSelected(SelectionEvent evt) {
            Shell shell = txtClassName.getShell();
            final IJavaProject project = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(EclipseUtil.getProjectNameFromDiagram(dtp.getDiagram())));
            try {
                SelectionDialog dialog = JavaUI.createTypeDialog(shell, null,
                                                                 SearchEngine.createJavaSearchScope(new IJavaElement[] { project }),
                                                                 IJavaElementSearchConstants.CONSIDER_CLASSES, false, "",
                                                                 new TypeSelectionExtension() {
                                                                    @Override public ITypeInfoFilterExtension getFilterExtension() {
                                                                        ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
                                                                            @Override public boolean select(ITypeInfoRequestor requestor) {
                                                                                try {
                                                                                    String pag = requestor.getPackageName();
                                                                                    String t   = (pag.equals("") ? "" : pag + ".") + requestor.getTypeName();
                                                                                    IType type = project.findType(t);
                                                                                    if(type == null || !type.exists()) return false;
                                                                                    IAnnotation[] annotations;
                                                                                    annotations = type.getAnnotations();
                                                                                    if(annotations == null) return false;
                                                                                    IAnnotation sicInfo = null;
                                                                                    for(IAnnotation a : annotations)
                                                                                        if(a.getElementName().equals(JavaTaskInfo.class.getSimpleName())) sicInfo = a;
                                                                                    if(sicInfo == null) return false;

                                                                                    IType supertype = type.newSupertypeHierarchy(null).getSuperclass(type);
                                                                                    if(org.ruminaq.tasks.javatask.client.JavaTask.class.getCanonicalName()
                                                                                            .equals(supertype.getFullyQualifiedName())) return true;
                                                                                    return false;
                                                                                }
                                                                                catch (JavaModelException e) { return false; }
                                                                            }
                                                                        };
                                                                        return extension;
                                                                    }
                                                                 });
                if(dialog.open() == SelectionDialog.OK) {
                    Object[] result = dialog.getResult();
                    String className = ((IType) result[0]).getFullyQualifiedName();

                    if(className != null) txtClassName.setText(className);

                    ModelUtil.runModelChange(new Runnable() {
                        public void run() {
                            Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
                            if (bo == null) return;
                            String implementationName = txtClassName.getText();
                            if (implementationName != null) {
                                if (bo instanceof JavaTask) {
                                    JavaTask javaTask = (JavaTask) bo;
                                    javaTask.setImplementationClass(implementationName);
                                    UpdateContext context = new UpdateContext(pe);
                                    dtp.getFeatureProvider().updateIfPossible(context);
                                }
                            }
                        }
                    }, ed, "Set Java Class");
                }
            } catch (Exception ex) {
                    ex.printStackTrace();
            }
        }});
        btnClassNew.addSelectionListener(new SelectionAdapter() { @SuppressWarnings("restriction") public void widgetSelected(SelectionEvent evt) {
             IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(CreateJavaTaskWizard.ID);
             try  {
                 if(descriptor != null) {
                    IWizard wizard = descriptor.createWizard();
                    String folder = ConstantsUtil.isTest(EclipseUtil.getModelPathFromEObject(pe)) ?
                            Constants.TEST_JAVA : Constants.MAIN_JAVA;
                    String projectName = EclipseUtil.getProjectNameFromDiagram(dtp.getDiagram());
                    IStructuredSelection selection = new StructuredSelection(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getFolder(folder)));
                    ((CreateJavaTaskWizard) wizard).init(PlatformUI.getWorkbench(), selection);
                    ((CreateJavaTaskWizard) wizard).setProject(selection);
                    ((CreateJavaTaskWizard) wizard).setListener(PropertySection.this);
                    WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(), wizard);
                    wd.setTitle(wizard.getWindowTitle());
                    wd.open();
                 }
             } catch(CoreException e) {
                 e.printStackTrace();
             }
        }});
    }

    private void addStyles() {
        root          .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        lblClassSelect.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        txtClassName  .setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
    }

    @Override
    public void refresh(PictogramElement pe, TransactionalEditingDomain ed) {
        if (pe != null) {
            Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
            if (bo == null) return;
            String className = ((JavaTask) bo).getImplementationClass();
            txtClassName.setText(className);
        }
    }

    @Override
    public void created(IType type) {
        final String className = type.getFullyQualifiedName();
        ModelUtil.runModelChange(new Runnable() {
            public void run() {
                Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
                if (bo == null) return;
                if (bo instanceof JavaTask) {
                    JavaTask javaTask = (JavaTask) bo;
                    javaTask.setImplementationClass(className);
                    UpdateContext context = new UpdateContext(pe);
                    dtp.getFeatureProvider().updateIfPossible(context);
                }
            }
        }, ed, "Set Java Class");
    }
}
