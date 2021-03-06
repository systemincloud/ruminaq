/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui.wizards;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.ruminaq.eclipse.usertask.model.userdefined.CustomParameter;
import org.ruminaq.eclipse.usertask.model.userdefined.In;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.Out;
import org.ruminaq.eclipse.wizards.task.AbstractCreateUserDefinedTaskPage;
import org.ruminaq.gui.api.UserDefinedTaskExtension;
import org.ruminaq.logs.ModelerLoggerFactory;
import org.ruminaq.tasks.javatask.client.InputPort;
import org.ruminaq.tasks.javatask.client.JavaTask;
import org.ruminaq.tasks.javatask.client.OutputPort;
import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.client.annotations.OutputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.Parameter;
import org.ruminaq.tasks.javatask.gui.Messages;
import org.ruminaq.util.Result;
import org.ruminaq.util.ServiceUtil;
import org.slf4j.Logger;

/**
 * JavaTask wizard page.
 * 
 * <p>Extends UI part from AbstractCreateUserDefinedTaskPage. Implements java
 * class generation.
 *
 * <p>example: @JavaTaskInfo public class MyTask extends JavaTask { }
 *
 * @author Marek Jagielski
 */
public class CreateJavaTaskPage extends AbstractCreateUserDefinedTaskPage {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateJavaTaskPage.class);

  private static final boolean DEFAULT_ATOMIC = getDefaultFromJavaTaskInfo(
      "atomic", Boolean.class).orElse(Boolean.FALSE);
  private static final boolean DEFAULT_GENERATOR = getDefaultFromJavaTaskInfo(
      "generator", Boolean.class).orElse(Boolean.FALSE);
  private static final boolean DEFAULT_EXTERNAL_SOURCE = getDefaultFromJavaTaskInfo(
      "externalSource", Boolean.class).orElse(Boolean.FALSE);
  private static final boolean DEFAULT_CONSTANT = getDefaultFromJavaTaskInfo(
      "constant", Boolean.class).orElse(Boolean.FALSE);

  private static final Pattern wordPattern = Pattern.compile("\\b(.)(.*?)\\b");
  private static final int FIRST_GROUP = 1;
  private static final int SECOND_GROUP = 2;

  public CreateJavaTaskPage(String pageName) {
    super(pageName);
    setTitle(Messages.createJavaTaskWizardName);
  }

  private static Stream<UserDefinedTaskExtension> extensions() {
    return ServiceUtil.getServicesAtLatestVersion(CreateJavaTaskPage.class,
        UserDefinedTaskExtension.class,
        () -> Collections.singletonList(JavaTask.class)).stream();
  }

  @Override
  public List<String> getDataTypes() {
    return extensions().map(UserDefinedTaskExtension::getSupportedData)
        .flatMap(List::stream).collect(Collectors.toList());
  }

  /**
   * Make capital first character of each word.
   *
   * @param text can have spaces
   * @return transformed text
   */
  private static String toTitleCase(String text) {
    return wordPattern.matcher(text)
        .replaceAll(m -> m.group(FIRST_GROUP).toUpperCase(Locale.US)
            + m.group(SECOND_GROUP));
  }

  /**
   * Fields are camel case starting from small letter.
   *
   * @param name name to sanitize
   * @return field name
   */
  private static String fieldName(String name) {
    String camelCase = toTitleCase(name).replace(" ", "").trim();
    return Character.toLowerCase(camelCase.charAt(0)) + camelCase.substring(1);
  }

  /**
   * Main method take meta Model defined in wizard page and decorate a jdt IType
   * class that is later saved to file.
   *
   * @param type   to decorate and marshal to file
   * @param module meta model defined in wizard
   */
  protected void decorateType(IType type, Module module) {
    ICompilationUnit cu = type.getCompilationUnit();
    CompilationUnit acu = parse(cu);
    AST ast = acu.getAST();
    ASTRewrite rewriter = ASTRewrite.create(ast);

    imports(ast, acu, rewriter, module);
    parameters(ast, acu, rewriter, module, type.getElementName());
    javaTaskInfo(ast, acu, rewriter, module);
    superClass(ast, acu, rewriter);
    inputPorts(ast, acu, rewriter, module);
    outputPorts(ast, acu, rewriter, module);
    methods(ast, acu, rewriter, module);

    try {
      Document doc = new Document(cu.getSource());
      rewriter.rewriteAST().apply(doc);
      cu.getBuffer().setContents(doc.get());
      cu.getBuffer().save(null, true);
    } catch (JavaModelException | MalformedTreeException
        | BadLocationException e) {
      LOGGER.error("Could not create JavaTask class", e);
    }
  }

  private static void imports(AST ast, CompilationUnit acu, ASTRewrite rewriter,
      Module module) {
    ListRewrite lrw = rewriter.getListRewrite(acu,
        CompilationUnit.IMPORTS_PROPERTY);
    if (!module.getInputs().isEmpty()) {
      addImport(ast, lrw, InputPort.class.getCanonicalName());
    }
    addImport(ast, lrw, JavaTask.class.getCanonicalName());
    if (!module.getOutputs().isEmpty()) {
      addImport(ast, lrw, OutputPort.class.getCanonicalName());
    }
    if (!module.getInputs().isEmpty()) {
      addImport(ast, lrw, InputPortInfo.class.getCanonicalName());
    }
    addImport(ast, lrw, JavaTaskInfo.class.getCanonicalName());
    if (!module.getOutputs().isEmpty()) {
      addImport(ast, lrw, OutputPortInfo.class.getCanonicalName());
    }
    if (!module.getParameters().isEmpty()) {
      addImport(ast, lrw, Parameter.class.getCanonicalName());
    }

    Stream
        .concat(module.getInputs().stream().map(In::getDataType),
            module.getOutputs().stream().map(Out::getDataType))
        .distinct()
        .map(dt -> extensions().map(s -> s.getCannonicalDataName(dt))
            .filter(Objects::nonNull).findFirst())
        .filter(Optional::isPresent).map(Optional::get)
        .forEach(clazz -> addImport(ast, lrw, clazz));
  }

  private static void addImport(AST ast, ListRewrite lrw,
      String canonicalName) {
    ImportDeclaration id = ast.newImportDeclaration();
    id.setName(ast.newName(canonicalName));
    lrw.insertLast(id, null);
  }

  private static void parameters(AST ast, CompilationUnit acu,
      ASTRewrite rewriter, Module module, String type) {
    module.getParameters().stream().map((CustomParameter p) -> {
      NormalAnnotation parameter = createAnnotation(ast, Parameter.class);
      addMemberToAnnotation(ast, parameter, "name",
          ast.newQualifiedName(ast.newName(type), parameterName(ast, p)));
      if (!"".equals(p.getDefaultValue())) {
        addMemberToAnnotation(ast, parameter, "defaultValue",
            stringLiteral(ast, p.getDefaultValue()));
      }
      return parameter;
    }).forEach(a -> acu.accept(new ASTVisitor() {
      @Override
      public boolean visit(TypeDeclaration node) {
        rewriter.getListRewrite(node, node.getModifiersProperty()).insertAt(a,
            0, null);
        return false;
      }
    }));

    for (CustomParameter p : module.getParameters()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          VariableDeclarationFragment fragment = ast
              .newVariableDeclarationFragment();
          fragment.setName(parameterName(ast, p));
          fragment.setInitializer(stringLiteral(ast, p.getName()));
          FieldDeclaration field = ast.newFieldDeclaration(fragment);
          field.setType(stringType(ast));
          addModifiers(ast, field,
              Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL);
          body(rewriter, node).insertLast(field, null);
          return false;
        }
      });
    }
    if (!module.getParameters().isEmpty()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          body(rewriter, node).insertLast(
              rewriter.createStringPlaceholder("", ASTNode.EMPTY_STATEMENT),
              null);
          return false;
        }
      });
    }
  }

  private static SimpleName parameterName(AST ast, CustomParameter p) {
    return ast.newSimpleName(spacesToUnderscores(upperCase(p.getName())));
  }

  private static String upperCase(String string) {
    return string.replace(' ', '_');
  }

  private static String spacesToUnderscores(String string) {
    return string.toUpperCase(Locale.US);
  }

  /**
   * This annotation ends with parenthesis and can have values.
   *
   * @param ast             code tree
   * @param annotationClass annotation name
   * @return annotation
   */
  private static NormalAnnotation createAnnotation(AST ast,
      Class<?> annotationClass) {
    NormalAnnotation annotation = ast.newNormalAnnotation();
    annotation.setTypeName(ast.newSimpleName(annotationClass.getSimpleName()));
    return annotation;
  }

  /**
   * This annotation doesn't end with parenthesis.
   *
   * @param ast             code tree
   * @param annotationClass annotation name
   * @return annotation
   */
  private static MarkerAnnotation createMarkerAnnotation(AST ast,
      Class<?> annotationClass) {
    MarkerAnnotation annotation = ast.newMarkerAnnotation();
    annotation.setTypeName(ast.newSimpleName(annotationClass.getSimpleName()));
    return annotation;
  }

  private static void addMemberToAnnotation(AST ast,
      NormalAnnotation annotation, String key, Expression expression) {
    MemberValuePair mvpName = ast.newMemberValuePair();
    mvpName.setName(ast.newSimpleName(key));
    mvpName.setValue(expression);
    annotation.values().add(mvpName);
  }

  private static Type stringType(AST ast) {
    return ast.newSimpleType(ast.newName(String.class.getSimpleName()));
  }

  private static StringLiteral stringLiteral(AST ast, String value) {
    StringLiteral literal = ast.newStringLiteral();
    literal.setLiteralValue(value);
    return literal;
  }

  private static TypeLiteral simpleTypeLiteral(AST ast, String value) {
    TypeLiteral literal = ast.newTypeLiteral();
    literal.setType(ast.newSimpleType(ast.newName(value)));
    return literal;
  }

  private static void addModifiers(AST ast, FieldDeclaration field, int flags) {
    field.modifiers().addAll(ast.newModifiers(flags));
  }

  private static void javaTaskInfo(AST ast, CompilationUnit acu,
      ASTRewrite rewriter, Module module) {
    if (module.isAtomic() == DEFAULT_ATOMIC
        && module.isGenerator() == DEFAULT_GENERATOR
        && module.isExternalSource() == DEFAULT_EXTERNAL_SOURCE
        && module.isConstant() == DEFAULT_CONSTANT) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          rewriter.getListRewrite(node, node.getModifiersProperty()).insertAt(
              createMarkerAnnotation(ast, JavaTaskInfo.class), 0, null);
          return false;
        }
      });
    } else {
      NormalAnnotation javaTaskInfo = createAnnotation(ast, JavaTaskInfo.class);
      if (module.isConstant() != DEFAULT_CONSTANT) {
        addMemberToAnnotation(ast, javaTaskInfo, "constant",
            ast.newBooleanLiteral(module.isConstant()));
      }
      if (module.isAtomic() != DEFAULT_ATOMIC) {
        addMemberToAnnotation(ast, javaTaskInfo, "atomic",
            ast.newBooleanLiteral(module.isAtomic()));
      }
      if (module.isGenerator() != DEFAULT_GENERATOR) {
        addMemberToAnnotation(ast, javaTaskInfo, "generator",
            ast.newBooleanLiteral(module.isGenerator()));
      }
      if (module.isExternalSource() != DEFAULT_EXTERNAL_SOURCE) {
        addMemberToAnnotation(ast, javaTaskInfo, "externalSource",
            ast.newBooleanLiteral(module.isExternalSource()));
      }
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          rewriter.getListRewrite(node, node.getModifiersProperty())
              .insertAt(javaTaskInfo, 0, null);
          return false;
        }
      });
    }
  }

  private static <T> Optional<T> getDefaultValueFromAnnotation(
      Class<?> annotationClass, String name, Class<T> type) {
    return Optional.of(annotationClass)
        .map(c -> Result.attempt(() -> c.getMethod(name)))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(Method::getDefaultValue).filter(type::isInstance).map(type::cast);
  }

  private static <T> Optional<T> getDefaultFromJavaTaskInfo(String name,
      Class<T> type) {
    return getDefaultValueFromAnnotation(JavaTaskInfo.class, name, type);
  }

  private static void superClass(AST ast, CompilationUnit acu,
      ASTRewrite rewriter) {
    acu.accept(new ASTVisitor() {
      @Override
      public boolean visit(TypeDeclaration node) {
        SimpleType st = ast
            .newSimpleType(ast.newSimpleName(JavaTask.class.getSimpleName()));
        rewriter.set(node, TypeDeclaration.SUPERCLASS_TYPE_PROPERTY, st, null);
        return false;
      }
    });
  }

  private static ListRewrite body(ASTRewrite rewriter, TypeDeclaration node) {
    return rewriter.getListRewrite(node, node.getBodyDeclarationsProperty());
  }

  private static void inputPorts(AST ast, CompilationUnit acu,
      ASTRewrite rewriter, Module module) {
    for (In in : module.getInputs()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          NormalAnnotation annotation = createAnnotation(ast,
              InputPortInfo.class);
          addMemberToAnnotation(ast, annotation, "name",
              stringLiteral(ast, in.getName()));
          addMemberToAnnotation(ast, annotation, "dataType",
              simpleTypeLiteral(ast, in.getDataType()));
          asynchronous(ast, annotation, in);
          group(ast, annotation, in);
          hold(ast, annotation, in);
          queue(ast, annotation, in);
          FieldDeclaration field = createPublicField(ast, in.getName(),
              InputPort.class);
          field.modifiers().add(0, annotation);
          body(rewriter, node).insertLast(field, null);
          return false;
        }
      });
    }
  }

  private static void asynchronous(AST ast, NormalAnnotation annotation,
      In in) {
    if (in.isAsynchronous()) {
      addMemberToAnnotation(ast, annotation, "asynchronous",
          ast.newBooleanLiteral(true));
    }
  }

  private static void group(AST ast, NormalAnnotation annotation, In in) {
    if (in.getGroup() != -1) {
      addMemberToAnnotation(ast, annotation, "group",
          ast.newNumberLiteral(Integer.toString(in.getGroup())));
    }
  }

  private static void hold(AST ast, NormalAnnotation annotation, In in) {
    if (in.isHold()) {
      addMemberToAnnotation(ast, annotation, "hold",
          ast.newBooleanLiteral(true));
    }
  }

  private static void queue(AST ast, NormalAnnotation annotation, In in) {
    if (in.getQueue() != 1) {
      addMemberToAnnotation(ast, annotation, "queue",
          ast.newNumberLiteral(Integer.toString(in.getQueue())));
    }
  }

  private static void outputPorts(AST ast, CompilationUnit acu,
      ASTRewrite rewriter, Module module) {
    for (Out out : module.getOutputs()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          NormalAnnotation annotation = createAnnotation(ast,
              OutputPortInfo.class);
          addMemberToAnnotation(ast, annotation, "name",
              stringLiteral(ast, out.getName()));
          addMemberToAnnotation(ast, annotation, "dataType",
              simpleTypeLiteral(ast, out.getDataType()));
          FieldDeclaration field = createPublicField(ast, out.getName(),
              OutputPort.class);
          field.modifiers().add(0, annotation);
          body(rewriter, node).insertLast(field, null);
          return false;
        }
      });
    }
  }

  private static FieldDeclaration createPublicField(AST ast, String name,
      Class<?> type) {
    VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
    fragment.setName(ast.newSimpleName(fieldName(name)));
    FieldDeclaration field = ast.newFieldDeclaration(fragment);
    field.setType(ast.newSimpleType(ast.newName(type.getSimpleName())));
    field.modifiers().addAll(ast.newModifiers(Modifier.PUBLIC));
    return field;
  }

  private static void methods(AST ast, CompilationUnit acu, ASTRewrite rewriter,
      Module module) {
    if (module.isRunnerStart()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
              .insertLast(createPublicVoidMethod(ast, "runnerStart"), null);
          return false;
        }
      });
    }
    if (module.isExecuteAsync()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          MethodDeclaration md = createPublicVoidMethod(ast, "executeAsync");
          SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
          svd.setType(
              ast.newSimpleType(ast.newName(InputPort.class.getSimpleName())));
          svd.setName(ast.newSimpleName("asynchIn"));
          md.parameters().add(svd);
          body(rewriter, node).insertLast(md, null);
          return false;
        }
      });
    }
    if (module.isExecuteExtSrc()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          MethodDeclaration md = createPublicVoidMethod(ast, "executeExtSrc");
          body(rewriter, node).insertLast(md, null);
          return false;
        }
      });
    }
    if (module.isGenerate()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          body(rewriter, node)
              .insertLast(createPublicVoidMethod(ast, "generate"), null);
          return false;
        }
      });
    }
    if (module.isExecute()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          MethodDeclaration md = createPublicVoidMethod(ast, "execute");
          SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
          svd.setType(ast.newPrimitiveType(PrimitiveType.INT));
          svd.setName(ast.newSimpleName("grp"));
          md.parameters().add(svd);
          body(rewriter, node).insertLast(md, null);
          return false;
        }
      });
    }
    if (module.isRunnerStop()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          body(rewriter, node)
              .insertLast(createPublicVoidMethod(ast, "runnerStop"), null);
          return false;
        }
      });
    }
  }

  private static MethodDeclaration createPublicVoidMethod(AST ast,
      String name) {
    MethodDeclaration md = ast.newMethodDeclaration();
    List<Modifier> modifs = ast.newModifiers(Modifier.PUBLIC);
    md.modifiers().addAll(modifs);
    md.modifiers().add(0, override(ast));

    md.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
    md.setName(ast.newSimpleName(name));
    md.setBody(ast.newBlock());

    return md;
  }

  private static MarkerAnnotation override(AST ast) {
    MarkerAnnotation annotation = ast.newMarkerAnnotation();
    annotation.setTypeName(ast.newSimpleName(Override.class.getSimpleName()));
    return annotation;
  }

  /**
   * Get CompilationUnit from ICompilationUnit.
   *
   * @param unit ICompilationUnit that can be retrieved from e.g. IType.
   * @return parsed CompilationUnit
   */
  public static CompilationUnit parse(ICompilationUnit unit) {
    ASTParser parser = ASTParser.newParser(AST.JLS15);
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setSource(unit);
    parser.setResolveBindings(true);
    return (CompilationUnit) parser.createAST(null);
  }
}
