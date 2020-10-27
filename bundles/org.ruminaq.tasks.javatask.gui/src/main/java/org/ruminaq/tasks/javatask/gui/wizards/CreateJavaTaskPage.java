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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.text.WordUtils;
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
 * @author Marek Jagielski
 */
public class CreateJavaTaskPage extends AbstractCreateUserDefinedTaskPage {

  private static final Logger LOGGER = ModelerLoggerFactory
      .getLogger(CreateJavaTaskPage.class);

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
  protected List<String> getDataTypes() {
    return extensions().map(UserDefinedTaskExtension::getSupportedData)
        .flatMap(List::stream).collect(Collectors.toList());
  }

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
        StringLiteral slValue = ast.newStringLiteral();
        slValue.setLiteralValue(p.getDefaultValue());
        addMemberToAnnotation(ast, parameter, "defaultValue", slValue);
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
          StringLiteral init = ast.newStringLiteral();
          init.setLiteralValue(p.getName());
          fragment.setInitializer(init);
          FieldDeclaration field = ast.newFieldDeclaration(fragment);
          field.setType(stringType(ast));
          addModifiers(ast, field,
              Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL);
          rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
              .insertLast(field, null);
          return false;
        }
      });
    }
    if (!module.getParameters().isEmpty()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
              .insertLast(
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
    return string.replaceAll(" ", "_");
  }

  private static String spacesToUnderscores(String string) {
    return string.toUpperCase(Locale.US);
  }

  private static NormalAnnotation createAnnotation(AST ast,
      Class<?> annotationClass) {
    NormalAnnotation annotation = ast.newNormalAnnotation();
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

  private static void addModifiers(AST ast, FieldDeclaration field, int flags) {
    field.modifiers().addAll(ast.newModifiers(flags));
  }

  private static void javaTaskInfo(AST ast, CompilationUnit acu,
      ASTRewrite rewriter, Module module) {
    boolean defaultAtomic = getDefaultValueFromAnnotation(JavaTaskInfo.class,
        "atomic", Boolean.class).orElse(false);
    boolean defaultGenerator = getDefaultValueFromAnnotation(JavaTaskInfo.class,
        "generator", Boolean.class).orElse(false);
    boolean defaultExternalSource = getDefaultValueFromAnnotation(
        JavaTaskInfo.class, "externalSource", Boolean.class).orElse(false);
    boolean defaultConstant = getDefaultValueFromAnnotation(JavaTaskInfo.class,
        "constant", Boolean.class).orElse(false);

    NormalAnnotation javaTaskInfo = createAnnotation(ast, JavaTaskInfo.class);
    if (module.isConstant() != defaultConstant) {
      addMemberToAnnotation(ast, javaTaskInfo, "constant",
          ast.newBooleanLiteral(module.isConstant()));
    }
    if (module.isAtomic() != defaultAtomic) {
      addMemberToAnnotation(ast, javaTaskInfo, "atomic",
          ast.newBooleanLiteral(module.isAtomic()));
    }
    if (module.isGenerator() != defaultGenerator) {
      addMemberToAnnotation(ast, javaTaskInfo, "generator",
          ast.newBooleanLiteral(module.isGenerator()));
    }
    if (module.isExternalSource() != defaultExternalSource) {
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

  private static <T> Optional<T> getDefaultValueFromAnnotation(
      Class<?> annotationClass, String name, Class<T> type) {
    return Optional.of(annotationClass)
        .map(c -> Result.attempt(() -> c.getMethod(name)))
        .map(r -> r.orElse(null)).filter(Objects::nonNull)
        .map(Method::getDefaultValue).filter(type::isInstance).map(type::cast);
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

  private static void inputPorts(AST ast, CompilationUnit acu,
      ASTRewrite rewriter, Module module) {
    for (final In in : module.getInputs()) {
      acu.accept(new ASTVisitor() {
        public boolean visit(TypeDeclaration node) {
          VariableDeclarationFragment fragment = ast
              .newVariableDeclarationFragment();
          String var = WordUtils.capitalizeFully(in.getName()).replace(" ", "")
              .trim();
          var = Character.toLowerCase(var.charAt(0)) + var.substring(1);
          fragment.setName(ast.newSimpleName(var));
          FieldDeclaration field = ast.newFieldDeclaration(fragment);
          field.setType(
              ast.newSimpleType(ast.newName(InputPort.class.getSimpleName())));

          List<Modifier> modifs = ast.newModifiers(Modifier.PUBLIC);
          field.modifiers().addAll(modifs);

          NormalAnnotation inputPortInfoA = createAnnotation(ast,
              InputPortInfo.class);

          StringLiteral vName = ast.newStringLiteral();
          vName.setLiteralValue(in.getName());
          addMemberToAnnotation(ast, inputPortInfoA, "name", vName);

          TypeLiteral vDt = ast.newTypeLiteral();
          vDt.setType(ast.newSimpleType(ast.newName(in.getDataType())));
          addMemberToAnnotation(ast, inputPortInfoA, "dataType", vDt);

          if (in.isAsynchronous()) {
            MemberValuePair mvpAsync = ast.newMemberValuePair();
            mvpAsync.setName(ast.newSimpleName("asynchronous"));
            mvpAsync.setValue(ast.newBooleanLiteral(true));
            inputPortInfoA.values().add(mvpAsync);
          }

          int grp = in.getGroup();
          if (grp != -1) {
            MemberValuePair mvpAsync = ast.newMemberValuePair();
            mvpAsync.setName(ast.newSimpleName("group"));
            mvpAsync.setValue(ast.newNumberLiteral(Integer.toString(grp)));
            inputPortInfoA.values().add(mvpAsync);
          }

          if (in.isHold()) {
            MemberValuePair mvpAsync = ast.newMemberValuePair();
            mvpAsync.setName(ast.newSimpleName("hold"));
            mvpAsync.setValue(ast.newBooleanLiteral(true));
            inputPortInfoA.values().add(mvpAsync);
          }

          if (in.getQueue() != 1) {
            MemberValuePair mvpAsync = ast.newMemberValuePair();
            mvpAsync.setName(ast.newSimpleName("queue"));
            mvpAsync.setValue(
                ast.newNumberLiteral(Integer.toString(in.getQueue())));
            inputPortInfoA.values().add(mvpAsync);
          }

          field.modifiers().add(0, inputPortInfoA);

          rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
              .insertLast(field, null);
          return false;
        }
      });
    }
  }

  private static void outputPorts(AST ast, CompilationUnit acu,
      ASTRewrite rewriter, Module module) {
    for (final Out out : module.getOutputs()) {
      acu.accept(new ASTVisitor() {
        public boolean visit(TypeDeclaration node) {
          VariableDeclarationFragment fragment = ast
              .newVariableDeclarationFragment();
          String var = WordUtils.capitalizeFully(out.getName()).replace(" ", "")
              .trim();
          var = Character.toLowerCase(var.charAt(0)) + var.substring(1);
          fragment.setName(ast.newSimpleName(var));
          FieldDeclaration field = ast.newFieldDeclaration(fragment);
          field.setType(
              ast.newSimpleType(ast.newName(OutputPort.class.getSimpleName())));

          List<Modifier> modifs = ast.newModifiers(Modifier.PUBLIC);
          field.modifiers().addAll(modifs);

          final NormalAnnotation outputPortInfoA = ast.newNormalAnnotation();
          outputPortInfoA.setTypeName(
              ast.newSimpleName(OutputPortInfo.class.getSimpleName()));

          MemberValuePair mvpName = ast.newMemberValuePair();
          mvpName.setName(ast.newSimpleName("name"));
          StringLiteral vName = ast.newStringLiteral();
          vName.setLiteralValue(out.getName());
          mvpName.setValue(vName);
          outputPortInfoA.values().add(mvpName);

          MemberValuePair mvpDt = ast.newMemberValuePair();
          mvpDt.setName(ast.newSimpleName("dataType"));
          TypeLiteral vDt = ast.newTypeLiteral();
          vDt.setType(ast.newSimpleType(ast.newName(out.getDataType())));
          mvpDt.setValue(vDt);
          outputPortInfoA.values().add(mvpDt);

          field.modifiers().add(0, outputPortInfoA);

          rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
              .insertLast(field, null);
          return false;
        }
      });
    }
  }

  private static void methods(AST ast, CompilationUnit acu, ASTRewrite rewriter,
      Module module) {
    if (module.isRunnerStart()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          addMethod(ast,
              rewriter.getListRewrite(node, node.getBodyDeclarationsProperty()),
              "runnerStart");
          return false;
        }
      });
    }

    if (module.isExecuteAsync()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          MethodDeclaration md = ast.newMethodDeclaration();
          List<Modifier> modifs = ast.newModifiers(Modifier.PUBLIC);
          md.modifiers().addAll(modifs);

          MarkerAnnotation overrideA = ast.newMarkerAnnotation();
          overrideA
              .setTypeName(ast.newSimpleName(Override.class.getSimpleName()));
          md.modifiers().add(0, overrideA);

          md.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
          md.setName(ast.newSimpleName("executeAsync"));
          SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
          svd.setType(
              ast.newSimpleType(ast.newName(InputPort.class.getSimpleName())));
          svd.setName(ast.newSimpleName("asynchIn"));
          md.parameters().add(svd);
          md.setBody(ast.newBlock());

          rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
              .insertLast(md, null);
          return false;
        }
      });
    }
    if (module.isExecuteExtSrc()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          MethodDeclaration md = ast.newMethodDeclaration();
          List<Modifier> modifs = ast.newModifiers(Modifier.PUBLIC);
          md.modifiers().addAll(modifs);

          MarkerAnnotation overrideA = ast.newMarkerAnnotation();
          overrideA
              .setTypeName(ast.newSimpleName(Override.class.getSimpleName()));
          md.modifiers().add(0, overrideA);

          md.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
          md.setName(ast.newSimpleName("executeExtSrc"));
          md.setBody(ast.newBlock());

          rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
              .insertLast(md, null);
          return false;
        }
      });
    }
    if (module.isGenerate()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          addMethod(ast,
              rewriter.getListRewrite(node, node.getBodyDeclarationsProperty()),
              "generate");
          return false;
        }
      });
    }
    if (module.isExecute()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          MethodDeclaration md = ast.newMethodDeclaration();
          List<Modifier> modifs = ast.newModifiers(Modifier.PUBLIC);
          md.modifiers().addAll(modifs);

          MarkerAnnotation overrideA = ast.newMarkerAnnotation();
          overrideA
              .setTypeName(ast.newSimpleName(Override.class.getSimpleName()));
          md.modifiers().add(0, overrideA);

          md.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
          md.setName(ast.newSimpleName("execute"));
          SingleVariableDeclaration svd = ast.newSingleVariableDeclaration();
          svd.setType(ast.newPrimitiveType(PrimitiveType.INT));
          svd.setName(ast.newSimpleName("grp"));
          md.parameters().add(svd);
          md.setBody(ast.newBlock());

          rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
              .insertLast(md, null);
          return false;
        }
      });
    }

    if (module.isRunnerStop()) {
      acu.accept(new ASTVisitor() {
        @Override
        public boolean visit(TypeDeclaration node) {
          addMethod(ast,
              rewriter.getListRewrite(node, node.getBodyDeclarationsProperty()),
              "runnerStop");
          return false;
        }
      });
    }

  }

  private static void addMethod(AST ast, ListRewrite lrw, String name) {
    MethodDeclaration md = ast.newMethodDeclaration();
    List<Modifier> modifs = ast.newModifiers(Modifier.PUBLIC);
    md.modifiers().addAll(modifs);

    MarkerAnnotation overrideA = ast.newMarkerAnnotation();
    overrideA.setTypeName(ast.newSimpleName(Override.class.getSimpleName()));
    md.modifiers().add(0, overrideA);

    md.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
    md.setName(ast.newSimpleName(name));
    md.setBody(ast.newBlock());

    lrw.insertLast(md, null);
  }

  /**
   * Get CompilationUnit from ICompilationUnit. s
   *
   * @param unit ICompilationUnit that can be retrieved from e.g. IType.
   * @return
   */
  public static CompilationUnit parse(ICompilationUnit unit) {
    ASTParser parser = ASTParser.newParser(AST.JLS14);
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setSource(unit);
    parser.setResolveBindings(true);
    return (CompilationUnit) parser.createAST(null);
  }
}
