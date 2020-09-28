/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.NamedMember;
import org.osgi.service.component.annotations.Component;
import org.ruminaq.eclipse.wizards.task.AbstractCreateCustomTaskPage;
import org.ruminaq.gui.api.UpdateFeatureExtension;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.gui.features.update.UpdateUserDefinedTaskFeature;
import org.ruminaq.model.DataTypeManager;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.UserDefinedTask;
import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.client.annotations.OutputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.Parameter;
import org.ruminaq.tasks.javatask.gui.UpdateFeatureImpl.UpdateFeature.Filter;
import org.ruminaq.tasks.javatask.gui.wizards.CreateJavaTaskPage;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.util.EclipseUtil;

@Component(property = { "service.ranking:Integer=5" })
public class UpdateFeatureImpl implements UpdateFeatureExtension {

  @Override
  public List<Class<? extends IUpdateFeature>> getFeatures() {
    return Collections.singletonList(UpdateFeature.class);
  }

  @FeatureFilter(Filter.class)
  public static class UpdateFeature extends UpdateUserDefinedTaskFeature {

    public static class Filter extends AbstractUpdateFeatureFilter {
      @Override
      public Class<? extends BaseElement> forBusinessObject() {
        return JavaTask.class;
      }
    }
    
    private NamedMember type = null;
    private String desc = AddFeatureImpl.AddFeature.NOT_CHOSEN;

    public UpdateFeature(IFeatureProvider fp) {
      super(fp);
    }

    @Override
    protected String getResource(Object bo) {
      JavaTask be = (JavaTask) bo;
      return be.getImplementationClass();
    }

    @Override
    public boolean load(String className) {
      IJavaProject project = JavaCore
          .create(ResourcesPlugin.getWorkspace().getRoot()
              .getProject(EclipseUtil.getProjectNameFromDiagram(getDiagram())));
      IJavaSearchScope scope = SearchEngine
          .createJavaSearchScope(new IJavaElement[] { project });
      SearchPattern pattern = SearchPattern.createPattern(className,
          IJavaSearchConstants.TYPE, IJavaSearchConstants.TYPE,
          SearchPattern.R_FULL_MATCH | SearchPattern.R_CASE_SENSITIVE);

      SearchRequestor requestor = new SearchRequestor() {
        @Override
        public void acceptSearchMatch(SearchMatch sm) throws CoreException {
          type = (NamedMember) sm.getElement();
        }
      };

      SearchEngine searchEngine = new SearchEngine();
      try {
        searchEngine.search(pattern,
            new SearchParticipant[] {
                SearchEngine.getDefaultSearchParticipant() },
            scope, requestor, null);
      } catch (CoreException e) {
        return false;
      }

      if (type == null)
        return false;
      this.desc = "".equals(type.getElementName())
          ? AddFeatureImpl.AddFeature.NOT_CHOSEN
          : type.getElementName();

      IAnnotation[] annotations;
      try {
        annotations = type.getAnnotations();
      } catch (JavaModelException e) {
        return false;
      }

      if (annotations == null)
        return false;

      IAnnotation sicInfo = null;
      for (IAnnotation a : annotations) {
        if (a.getElementName().equals(JavaTaskInfo.class.getSimpleName()))
          sicInfo = a;
      }

      return sicInfo != null;
    }

    protected void loadIconDesc() {
      this.iconDesc = desc;
    }

    @Override
    protected void loadInputPorts() {
      IJavaSearchScope scope = SearchEngine
          .createJavaSearchScope(new IJavaElement[] { type });
      SearchPattern pattern = SearchPattern.createPattern(
          InputPortInfo.class.getSimpleName(),
          IJavaSearchConstants.ANNOTATION_TYPE,
          IJavaSearchConstants.ALL_OCCURRENCES,
          SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
      SearchRequestor requestor = new SearchRequestor() {
        @Override
        public void acceptSearchMatch(SearchMatch sm) throws CoreException {
          NamedMember el = (NamedMember) sm.getElement();
          IAnnotation[] annotations;
          try {
            annotations = el.getAnnotations();
          } catch (JavaModelException e) {
            return;
          }
          if (annotations == null)
            return;

          IAnnotation inputPortInfo = null;
          for (IAnnotation a : annotations)
            if (a.getElementName().equals(InputPortInfo.class.getSimpleName()))
              inputPortInfo = a;

          if (inputPortInfo == null)
            return;

          String name = null;
          Object[] dataType = null;
          Boolean asynchronous = null;
          Integer group = null;
          Boolean hold = null;
          Integer queue = null;

          for (IMemberValuePair mvp : inputPortInfo.getMemberValuePairs()) {
            if ("name".equals(mvp.getMemberName()))
              name = (String) mvp.getValue();
            if ("dataType".equals(mvp.getMemberName()))
              dataType = mvp.getValue() instanceof String
                  ? new Object[] { mvp.getValue() }
                  : (Object[]) mvp.getValue();
            if ("asynchronous".equals(mvp.getMemberName()))
              asynchronous = (Boolean) mvp.getValue();
            if ("group".equals(mvp.getMemberName()))
              group = (Integer) mvp.getValue();
            if ("hold".equals(mvp.getMemberName()))
              hold = (Boolean) mvp.getValue();
            if ("queue".equals(mvp.getMemberName()))
              queue = (Integer) mvp.getValue();
          }

          if (asynchronous == null)
            asynchronous = Boolean.FALSE;
          if (group == null)
            group = new Integer(-1);
          if (hold == null)
            hold = new Boolean(false);
          if (queue == null)
            queue = new Integer(1);

          if (queue == 0)
            queue = new Integer(1);
          if (queue < 0)
            queue = new Integer(-1);

          List<DataType> dts = new LinkedList<>();
          for (Object d : dataType) {
            DataType tmp = DataTypeManager.INSTANCE
                .getDataTypeFromName((String) d);
            if (tmp != null)
              dts.add(tmp);
          }
          String queueSize = queue == -1 ? AbstractCreateCustomTaskPage.INF
              : queue.toString();
          inputs.add(
              new FileInternalInputPort(name, dts, asynchronous.booleanValue(),
                  group.intValue(), hold.booleanValue(), queueSize));
        }
      };

      SearchEngine searchEngine = new SearchEngine();
      try {
        searchEngine.search(pattern,
            new SearchParticipant[] {
                SearchEngine.getDefaultSearchParticipant() },
            scope, requestor, null);
      } catch (CoreException e) {
      }
    }

    @Override
    protected void loadOutputPorts() {
      IJavaSearchScope scope = SearchEngine
          .createJavaSearchScope(new IJavaElement[] { type });
      SearchPattern pattern = SearchPattern.createPattern(
          OutputPortInfo.class.getSimpleName(),
          IJavaSearchConstants.ANNOTATION_TYPE,
          IJavaSearchConstants.ALL_OCCURRENCES,
          SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
      SearchRequestor requestor = new SearchRequestor() {
        @Override
        public void acceptSearchMatch(SearchMatch sm) throws CoreException {
          NamedMember el = (NamedMember) sm.getElement();
          IAnnotation[] annotations;
          try {
            annotations = el.getAnnotations();
          } catch (JavaModelException e) {
            return;
          }
          if (annotations == null)
            return;

          IAnnotation outputPortInfo = null;
          for (IAnnotation a : annotations)
            if (a.getElementName().equals(OutputPortInfo.class.getSimpleName()))
              outputPortInfo = a;

          if (outputPortInfo == null)
            return;

          String name = null;
          Object[] dataType = null;

          for (IMemberValuePair mvp : outputPortInfo.getMemberValuePairs()) {
            if ("name".equals(mvp.getMemberName()))
              name = (String) mvp.getValue();
            if ("dataType".equals(mvp.getMemberName()))
              dataType = mvp.getValue() instanceof String
                  ? new Object[] { (String) mvp.getValue() }
                  : (Object[]) mvp.getValue();
          }

          List<DataType> dts = new LinkedList<>();
          for (Object d : dataType) {
            DataType tmp = DataTypeManager.INSTANCE
                .getDataTypeFromName((String) d);
            if (tmp != null)
              dts.add(tmp);
          }
          outputs.add(new FileInternalOutputPort(name, dts));
        }
      };

      SearchEngine searchEngine = new SearchEngine();
      try {
        searchEngine.search(pattern,
            new SearchParticipant[] {
                SearchEngine.getDefaultSearchParticipant() },
            scope, requestor, null);
      } catch (CoreException e) {
      }
    }

    @Override
    protected void loadAtomic() {
      IAnnotation[] annotations;
      try {
        annotations = type.getAnnotations();
      } catch (JavaModelException e) {
        return;
      }

      if (annotations == null)
        return;

      IAnnotation sicInfo = null;
      for (IAnnotation a : annotations)
        if (a.getElementName().equals(JavaTaskInfo.class.getSimpleName()))
          sicInfo = a;

      IMemberValuePair[] mvps = null;
      try {
        mvps = sicInfo.getMemberValuePairs();
      } catch (JavaModelException e) {
      }
      if (mvps == null)
        return;

      for (IMemberValuePair mvp : mvps)
        if (mvp.getMemberName().equals("atomic"))
          atomic = ((Boolean) mvp.getValue()).booleanValue();
    }

    @Override
    protected Map<String, String> getParameters(UserDefinedTask udt) {
      final Map<String, String> ret = new HashMap<>();
      JavaTask jt = (JavaTask) udt;
      if (jt != null) {
        String className = jt.getImplementationClass();

        IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
        SearchPattern pattern = SearchPattern.createPattern(className,
            IJavaSearchConstants.TYPE, IJavaSearchConstants.TYPE,
            SearchPattern.R_FULL_MATCH | SearchPattern.R_CASE_SENSITIVE);
        SearchRequestor requestor = new SearchRequestor() {
          @Override
          public void acceptSearchMatch(SearchMatch sm) throws CoreException {
            type = (NamedMember) sm.getElement();
          }
        };

        SearchEngine searchEngine = new SearchEngine();
        try {
          searchEngine.search(pattern,
              new SearchParticipant[] {
                  SearchEngine.getDefaultSearchParticipant() },
              scope, requestor, null);
        } catch (Exception e) {
          return ret;
        }

        if (type == null)
          return ret;

        final ICompilationUnit cu = type.getCompilationUnit();

        final CompilationUnit acu = CreateJavaTaskPage.parse(cu);

        acu.accept(new ASTVisitor() {
          @Override
          public boolean visit(TypeDeclaration node) {
            for (Object m : node.modifiers()) {
              if (m instanceof NormalAnnotation
                  && ((NormalAnnotation) m).getTypeName().toString()
                      .equals(Parameter.class.getSimpleName())) {
                NormalAnnotation sicParameterA = (NormalAnnotation) m;
                String name = null;
                String defaultValue = "";
                for (Object i : sicParameterA.values()) {
                  if (i instanceof MemberValuePair) {
                    MemberValuePair mvp = (MemberValuePair) i;
                    if ("name".equals(mvp.getName().toString())) {
                      Expression e2 = mvp.getValue();
                      if (e2 instanceof QualifiedName) {
                        QualifiedName qn = (QualifiedName) e2;
                        IBinding b = qn.resolveBinding();
                        if (b instanceof IVariableBinding)
                          name = (String) ((IVariableBinding) b)
                              .getConstantValue();
                      } else if (e2 instanceof StringLiteral)
                        name = ((StringLiteral) e2).getLiteralValue();
                    } else if ("defaultValue"
                        .equals(mvp.getName().toString())) {
                      Expression e2 = mvp.getValue();
                      if (e2 instanceof QualifiedName) {
                        QualifiedName qn = (QualifiedName) e2;
                        IBinding b = qn.resolveBinding();
                        if (b instanceof IVariableBinding)
                          defaultValue = (String) ((IVariableBinding) b)
                              .getConstantValue();
                      } else if (e2 instanceof StringLiteral)
                        defaultValue = ((StringLiteral) e2).getLiteralValue();
                    }
                  }
                  ret.put(name, defaultValue);
                }
              }
            }
            return false;
          }
        });
      }
      return ret;
    }
  }
}
