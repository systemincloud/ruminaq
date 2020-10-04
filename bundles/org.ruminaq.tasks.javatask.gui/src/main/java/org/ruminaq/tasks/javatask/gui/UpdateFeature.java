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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTVisitor;
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
import org.ruminaq.eclipse.wizards.task.AbstractCreateCustomTaskPage;
import org.ruminaq.gui.features.FeatureFilter;
import org.ruminaq.gui.features.update.AbstractUpdateFeatureFilter;
import org.ruminaq.gui.features.update.UpdateUserDefinedTaskFeature;
import org.ruminaq.model.DataTypeManager;
import org.ruminaq.model.ruminaq.BaseElement;
import org.ruminaq.model.ruminaq.DataType;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.model.ruminaq.UserDefinedTask;
import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.client.annotations.OutputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.Parameter;
import org.ruminaq.tasks.javatask.gui.wizards.CreateJavaTaskPage;
import org.ruminaq.tasks.javatask.model.javatask.JavaTask;
import org.ruminaq.util.EclipseUtil;
import org.ruminaq.util.Result;

/**
 * JavaTask UpdateFeature.
 *
 * @author Marek Jagielski
 */
@FeatureFilter(UpdateFeature.Filter.class)
public class UpdateFeature extends UpdateUserDefinedTaskFeature {

  public static class Filter extends AbstractUpdateFeatureFilter {
    @Override
    public Class<? extends BaseElement> forBusinessObject() {
      return JavaTask.class;
    }
  }

  private NamedMember type;

  public UpdateFeature(IFeatureProvider fp) {
    super(fp);
  }

  private static Optional<IAnnotation> toAnnotation(SearchMatch sm,
      Class<?> annotationType) {
    return Optional.of(sm).map(SearchMatch::getElement)
        .filter(NamedMember.class::isInstance).map(NamedMember.class::cast)
        .map(nm -> nm.getAnnotation(annotationType.getSimpleName()));
  }

  private static Optional<Object> annotationValue(IAnnotation annotation,
      String name) {
    return Optional.of(annotation)
        .map(a -> Result.attempt(a::getMemberValuePairs))
        .flatMap(r -> Optional.ofNullable(r.orElse(null))).map(Stream::of)
        .orElseGet(Stream::empty)
        .filter(mvp -> mvp.getMemberName().equals(name)).findFirst()
        .map(IMemberValuePair::getValue);
  }

  private static Optional<Object> annotationValue(SearchMatch sm,
      Class<?> annotationType, String name) {
    return toAnnotation(sm, annotationType)
        .flatMap(a -> annotationValue(a, name));
  }

  private static <T> Optional<T> annotationValueCasted(IAnnotation annotation,
      String name, Class<T> type) {
    return annotationValue(annotation, name).filter(type::isInstance)
        .map(type::cast);
  }

  private static <T> Optional<T> annotationValueCasted(SearchMatch sm,
      Class<?> annotationType, String name, Class<T> type) {
    return toAnnotation(sm, annotationType)
        .flatMap(a -> annotationValueCasted(a, name, type));
  }

  private static List<DataType> annotationValueArray(SearchMatch sm,
      Class<?> annotationType, String name) {
    return Stream
        .of(annotationValue(sm, annotationType, name)
            .filter(Predicate.not(Object[].class::isInstance))
            .map(v -> new Object[] { v })
            .orElseGet(() -> annotationValue(sm, annotationType, name)
                .map(Object[].class::cast).orElse(new Object[0])))
        .map(String.class::cast)
        .map(DataTypeManager.INSTANCE::getDataTypeFromName)
        .filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  protected String getResource(Task task) {
    return Optional.of(task).filter(JavaTask.class::isInstance)
        .map(JavaTask.class::cast).map(JavaTask::getImplementationClass)
        .orElse("");
  }

  @Override
  public boolean load(String className) {
    boolean loaded = Result.attempt(() -> {
      new SearchEngine().search(
          SearchPattern.createPattern(className, IJavaSearchConstants.TYPE,
              IJavaSearchConstants.TYPE,
              SearchPattern.R_FULL_MATCH | SearchPattern.R_CASE_SENSITIVE),
          new SearchParticipant[] {
              SearchEngine.getDefaultSearchParticipant() },
          SearchEngine.createJavaSearchScope(new IJavaElement[] { JavaCore
              .create(ResourcesPlugin.getWorkspace().getRoot().getProject(
                  EclipseUtil.getProjectNameFromDiagram(getDiagram()))) }),
          new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch sm) {
              UpdateFeature.this.type = Optional.of(sm)
                  .map(SearchMatch::getElement)
                  .filter(NamedMember.class::isInstance)
                  .map(NamedMember.class::cast).orElse(null);
            }
          }, null);
      return true;
    }).orElse(false);

    return Optional.ofNullable(type).filter(t -> loaded)
        .map(t -> Result.attempt(t::getAnnotations))
        .flatMap(r -> Optional.ofNullable(r.orElse(null))).map(Stream::of)
        .orElseGet(Stream::empty).map(IAnnotation::getElementName)
        .anyMatch(JavaTaskInfo.class.getSimpleName()::equals);
  }

  @Override
  protected String iconDesc() {
    return Optional.ofNullable(type).map(NamedMember::getElementName)
        .filter(Predicate.not(""::equals))
        .orElse(AddFeatureImpl.AddFeature.NOT_CHOSEN);
  }

  @Override
  protected List<FileInternalInputPort> inputPorts() {
    return Result.attempt(() -> {
      List<FileInternalInputPort> inputs = new LinkedList<>();
      new SearchEngine().search(
          SearchPattern.createPattern(InputPortInfo.class.getSimpleName(),
              IJavaSearchConstants.ANNOTATION_TYPE,
              IJavaSearchConstants.ALL_OCCURRENCES,
              SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE),
          new SearchParticipant[] {
              SearchEngine.getDefaultSearchParticipant() },
          SearchEngine.createJavaSearchScope(new IJavaElement[] { type }),
          new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch sm) {
              inputs.add(new FileInternalInputPort(
                  annotationValueCasted(sm, InputPortInfo.class, "name",
                      String.class).orElse(""),
                  annotationValueArray(sm, InputPortInfo.class, "dataType"),
                  annotationValueCasted(sm, InputPortInfo.class, "asynchronous",
                      Boolean.class).orElse(Boolean.FALSE).booleanValue(),
                  annotationValueCasted(sm, InputPortInfo.class, "group",
                      Integer.class).orElse(-1).intValue(),
                  annotationValueCasted(sm, InputPortInfo.class, "hold",
                      Boolean.class).orElse(Boolean.FALSE).booleanValue(),
                  Optional
                      .of(annotationValueCasted(sm, InputPortInfo.class,
                          "queue", Integer.class).filter(i -> i != 0)
                              .filter(i -> i >= -1).orElse(1))
                      .filter(q -> q != -1).map(q -> q.toString())
                      .orElse(AbstractCreateCustomTaskPage.INF)));
            }
          }, null);
      return inputs;
    }).orElse(Collections.emptyList());
  }

  @Override
  protected List<FileInternalOutputPort> outputPorts() {
    return Result.attempt(() -> {
      List<FileInternalOutputPort> outputs = new LinkedList<>();
      new SearchEngine().search(
          SearchPattern.createPattern(OutputPortInfo.class.getSimpleName(),
              IJavaSearchConstants.ANNOTATION_TYPE,
              IJavaSearchConstants.ALL_OCCURRENCES,
              SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE),
          new SearchParticipant[] {
              SearchEngine.getDefaultSearchParticipant() },
          SearchEngine.createJavaSearchScope(new IJavaElement[] { type }),
          new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch sm) throws CoreException {
              outputs.add(new FileInternalOutputPort(
                  annotationValueCasted(sm, OutputPortInfo.class, "name",
                      String.class).orElse(""),
                  annotationValueArray(sm, OutputPortInfo.class, "dataType")));
            }
          },

          null);
      return outputs;
    }).orElse(Collections.emptyList());
  }

  @Override
  protected boolean isAtomic() {
    return Optional.ofNullable(type)
        .map(t -> t.getAnnotation(JavaTaskInfo.class.getSimpleName()))
        .flatMap(a -> annotationValueCasted(a, "atomic", Boolean.class))
        .orElse(Boolean.TRUE);
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
        public void acceptSearchMatch(SearchMatch sm) {
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

      CreateJavaTaskPage.parse(type.getCompilationUnit())
          .accept(new ASTVisitor() {
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
