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
import org.ruminaq.gui.features.update.AbstractUpdateUserDefinedTaskFeature;
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
public class UpdateFeature extends AbstractUpdateUserDefinedTaskFeature {

  private static final int QUEUE_DEFAULT_SIZE = 1;
  private static final int QUEUE_INFINITE = -1;
  private static final int DEFAULT_GROUP = -1;

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

  private static Stream<NormalAnnotation> annotationsOnType(
      TypeDeclaration type, Class<?> clazz) {
    List<?> modifiers = Collections.synchronizedList(type.modifiers());
    return modifiers.stream().filter(NormalAnnotation.class::isInstance)
        .map(NormalAnnotation.class::cast).filter(
            na -> clazz.getSimpleName().equals(na.getTypeName().toString()));
  }

  private static Optional<MemberValuePair> memberValuePairFromAnnotation(
      NormalAnnotation na, String name) {
    List<?> parameterValues = Collections.synchronizedList(na.values());
    return parameterValues.stream().filter(MemberValuePair.class::isInstance)
        .map(MemberValuePair.class::cast)
        .filter(mvp -> name.equals(mvp.getName().toString())).findFirst();
  }

  private static String annotationValue(NormalAnnotation na, String name) {
    return memberValuePairFromAnnotation(na, name)
        .map(MemberValuePair::getValue).filter(QualifiedName.class::isInstance)
        .map(QualifiedName.class::cast).map(QualifiedName::resolveBinding)
        .filter(IVariableBinding.class::isInstance)
        .map(IVariableBinding.class::cast)
        .map(IVariableBinding::getConstantValue).map(String.class::isInstance)
        .map(String.class::cast)
        .orElseGet(() -> memberValuePairFromAnnotation(na, name)
            .map(MemberValuePair::getValue)
            .filter(StringLiteral.class::isInstance)
            .map(StringLiteral.class::cast).map(StringLiteral::getLiteralValue)
            .orElse(""));
  }

  @Override
  protected String getResource(Task task) {
    return Optional.of(task).filter(JavaTask.class::isInstance)
        .map(JavaTask.class::cast).map(JavaTask::getImplementationClass)
        .orElse("");
  }

  @Override
  public boolean load(String className) {
    SearchPattern pattern = SearchPattern.createPattern(className,
        IJavaSearchConstants.TYPE, IJavaSearchConstants.TYPE,
        SearchPattern.R_FULL_MATCH | SearchPattern.R_CASE_SENSITIVE);
    SearchParticipant[] participants = new SearchParticipant[] {
        SearchEngine.getDefaultSearchParticipant() };
    IJavaSearchScope scope = SearchEngine
        .createJavaSearchScope(new IJavaElement[] {
            JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(
                EclipseUtil.getProjectNameFromDiagram(getDiagram()))) });
    SearchRequestor searchRequestor = new SearchRequestor() {
      @Override
      public void acceptSearchMatch(SearchMatch sm) {
        UpdateFeature.this.type = Optional.of(sm).map(SearchMatch::getElement)
            .filter(NamedMember.class::isInstance).map(NamedMember.class::cast)
            .orElse(null);
      }
    };
    boolean loaded = Result.attempt(() -> {
      new SearchEngine().search(pattern, participants, scope, searchRequestor,
          null);
      return Boolean.TRUE;
    }).orElse(Boolean.FALSE);

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
    SearchPattern pattern = SearchPattern.createPattern(
        InputPortInfo.class.getSimpleName(),
        IJavaSearchConstants.ANNOTATION_TYPE,
        IJavaSearchConstants.ALL_OCCURRENCES,
        SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
    SearchParticipant[] participants = new SearchParticipant[] {
        SearchEngine.getDefaultSearchParticipant() };
    IJavaSearchScope scope = SearchEngine
        .createJavaSearchScope(new IJavaElement[] { type });
    List<FileInternalInputPort> inputs = new LinkedList<>();
    SearchRequestor searchRequestor = new SearchRequestor() {
      @Override
      public void acceptSearchMatch(SearchMatch sm) {
        inputs.add(new FileInternalInputPort(
            annotationValueCasted(sm, InputPortInfo.class, "name", String.class)
                .orElse(""),
            annotationValueArray(sm, InputPortInfo.class, "dataType"),
            annotationValueCasted(sm, InputPortInfo.class, "asynchronous",
                Boolean.class).orElse(Boolean.FALSE).booleanValue(),
            annotationValueCasted(sm, InputPortInfo.class, "group",
                Integer.class).orElseGet(() -> DEFAULT_GROUP).intValue(),
            annotationValueCasted(sm, InputPortInfo.class, "hold",
                Boolean.class).orElse(Boolean.FALSE).booleanValue(),
            Optional
                .of(annotationValueCasted(sm, InputPortInfo.class, "queue",
                    Integer.class).filter(i -> i != 0)
                        .filter(i -> i >= QUEUE_INFINITE)
                        .orElseGet(() -> QUEUE_DEFAULT_SIZE))
                .filter(q -> q != QUEUE_INFINITE).map(Object::toString)
                .orElse(AbstractCreateCustomTaskPage.INF)));
      }
    };
    return Result.attempt(() -> {
      new SearchEngine().search(pattern, participants, scope, searchRequestor,
          null);
      return inputs;
    }).orElse(Collections.emptyList());
  }

  @Override
  protected List<FileInternalOutputPort> outputPorts() {
    SearchPattern pattern = SearchPattern.createPattern(
        OutputPortInfo.class.getSimpleName(),
        IJavaSearchConstants.ANNOTATION_TYPE,
        IJavaSearchConstants.ALL_OCCURRENCES,
        SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
    SearchParticipant[] participants = new SearchParticipant[] {
        SearchEngine.getDefaultSearchParticipant() };
    IJavaSearchScope scope = SearchEngine
        .createJavaSearchScope(new IJavaElement[] { type });
    List<FileInternalOutputPort> outputs = new LinkedList<>();
    SearchRequestor searchRequestor = new SearchRequestor() {
      @Override
      public void acceptSearchMatch(SearchMatch sm) throws CoreException {
        outputs.add(new FileInternalOutputPort(
            annotationValueCasted(sm, OutputPortInfo.class, "name",
                String.class).orElse(""),
            annotationValueArray(sm, OutputPortInfo.class, "dataType")));
      }
    };
    return Result.attempt(() -> {
      new SearchEngine().search(pattern, participants, scope, searchRequestor,
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
    Map<String, String> ret = new HashMap<>();
    Optional.ofNullable(type).map(NamedMember::getCompilationUnit)
        .ifPresent(cu -> CreateJavaTaskPage.parse(cu).accept(new ASTVisitor() {
          @Override
          public boolean visit(TypeDeclaration type) {
            return annotationsOnType(type, Parameter.class).peek(na -> {
              String name = annotationValue(na, "name");
              String defaultValue = annotationValue(na, "defaultValue");
              ret.put(name, defaultValue);
            }).findAny().isPresent();
          }
        }));
    return ret;
  }
}
