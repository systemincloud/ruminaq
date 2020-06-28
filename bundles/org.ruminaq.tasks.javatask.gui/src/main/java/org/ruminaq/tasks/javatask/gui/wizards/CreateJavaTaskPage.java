/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/

package org.ruminaq.tasks.javatask.gui.wizards;

import java.util.List;
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
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.ruminaq.eclipse.usertask.model.userdefined.In;
import org.ruminaq.eclipse.usertask.model.userdefined.Module;
import org.ruminaq.eclipse.usertask.model.userdefined.Out;
import org.ruminaq.eclipse.usertask.model.userdefined.Parameter;
import org.ruminaq.eclipse.wizards.task.CreateUserDefinedTaskPage;
import org.ruminaq.tasks.javatask.client.InputPort;
import org.ruminaq.tasks.javatask.client.JavaTask;
import org.ruminaq.tasks.javatask.client.OutputPort;
import org.ruminaq.tasks.javatask.client.annotations.InputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.JavaTaskInfo;
import org.ruminaq.tasks.javatask.client.annotations.OutputPortInfo;
import org.ruminaq.tasks.javatask.client.annotations.SicParameter;
import org.ruminaq.tasks.javatask.client.data.Data;
import org.ruminaq.tasks.javatask.gui.Messages;
import org.ruminaq.tasks.javatask.gui.api.JavaTaskExtension;
import org.ruminaq.tasks.javatask.impl.JavaTaskDataConverter;
import org.ruminaq.util.ServiceUtil;

/**
 * JavaTask wizard page.
 *
 * @author Marek Jagielski
 */
public class CreateJavaTaskPage extends CreateUserDefinedTaskPage {

  private List<Class<? extends Data>> dataTypes = ServiceUtil
      .getServicesAtLatestVersion(CreateJavaTaskPage.class,
          JavaTaskExtension.class)
      .stream().map(JavaTaskExtension::getDataTypes).flatMap(List::stream)
      .collect(Collectors.toList());

  public CreateJavaTaskPage(String pageName) {
    super(pageName);
    setTitle(Messages.createJavaTaskWizardName);
  }

  @Override
  protected List<String> getDataTypes() {
    return dataTypes.stream().map(Class::getSimpleName)
        .collect(Collectors.toList());
  }

  @SuppressWarnings({ "unchecked" })
  public void decorateType(final IType type, final Module module) {
    try {
      final ICompilationUnit cu = type.getCompilationUnit();

      final CompilationUnit acu = parse(cu);
      final AST ast = acu.getAST();
      final ASTRewrite rewriter = ASTRewrite.create(ast);

      imports(ast,
          rewriter.getListRewrite(acu, CompilationUnit.IMPORTS_PROPERTY),
          module);

      List<Parameter> parameters = module.getParameters();
      if (!parameters.isEmpty()) {
        for (Parameter p : parameters) {
          NormalAnnotation sicParameterA = ast.newNormalAnnotation();
          sicParameterA.setTypeName(
              ast.newSimpleName(SicParameter.class.getSimpleName()));

          MemberValuePair mvpName = ast.newMemberValuePair();
          mvpName.setName(ast.newSimpleName("name"));
          mvpName.setValue(ast.newQualifiedName(
              ast.newName(type.getElementName()),
              ast.newSimpleName(p.getName().replace(" ", "_").toUpperCase())));

          sicParameterA.values().add(mvpName);

          if (!p.getDefaultValue().equals("")) {
            MemberValuePair mvpValue = ast.newMemberValuePair();
            mvpValue.setName(ast.newSimpleName("defaultValue"));
            StringLiteral slValue = ast.newStringLiteral();
            slValue.setLiteralValue(p.getDefaultValue());
            mvpValue.setValue(slValue);

            sicParameterA.values().add(mvpValue);
          }

          acu.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration node) {
              rewriter.getListRewrite(node, node.getModifiersProperty())
                  .insertAt(sicParameterA, 0, null);
              return false;
            }
          });
        }
      }
      // ----------------------------------------------------------------

      //
      // @JavaTaskInfo
      //
      boolean defaultAtomic = false;
      boolean defaultGenerator = false;
      boolean defaultExternalSource = false;
      boolean defaultConstant = false;
      try {
        defaultAtomic = (boolean) JavaTaskInfo.class.getMethod("atomic")
            .getDefaultValue();
        defaultGenerator = (boolean) JavaTaskInfo.class.getMethod("generator")
            .getDefaultValue();
        defaultExternalSource = (boolean) JavaTaskInfo.class
            .getMethod("externalSource").getDefaultValue();
        defaultConstant = (boolean) JavaTaskInfo.class.getMethod("constant")
            .getDefaultValue();
      } catch (NoSuchMethodException e) {
      }

      if (module.isAtomic() == defaultAtomic
          && module.isGenerator() == defaultGenerator
          && module.isExternalSource() == defaultExternalSource
          && module.isConstant() == defaultConstant) {
        final MarkerAnnotation javaTaskInfoA = ast.newMarkerAnnotation();
        javaTaskInfoA
            .setTypeName(ast.newSimpleName(JavaTaskInfo.class.getSimpleName()));
        acu.accept(new ASTVisitor() {

          public boolean visit(TypeDeclaration node) {
            rewriter.getListRewrite(node, node.getModifiersProperty())
                .insertAt(javaTaskInfoA, 0, null);
            return false;
          }
        });
      } else {
        final NormalAnnotation javaTaskInfoA = ast.newNormalAnnotation();
        javaTaskInfoA
            .setTypeName(ast.newSimpleName(JavaTaskInfo.class.getSimpleName()));

        if (module.isConstant() != defaultConstant) {
          MemberValuePair mvp = ast.newMemberValuePair();
          mvp.setName(ast.newSimpleName("constant"));
          mvp.setValue(ast.newBooleanLiteral(module.isConstant()));
          javaTaskInfoA.values().add(mvp);
        }
        if (module.isAtomic() != defaultAtomic) {
          MemberValuePair mvp = ast.newMemberValuePair();
          mvp.setName(ast.newSimpleName("atomic"));
          mvp.setValue(ast.newBooleanLiteral(module.isAtomic()));
          javaTaskInfoA.values().add(mvp);
        }
        if (module.isGenerator() != defaultGenerator) {
          MemberValuePair mvp = ast.newMemberValuePair();
          mvp.setName(ast.newSimpleName("generator"));
          mvp.setValue(ast.newBooleanLiteral(module.isGenerator()));
          javaTaskInfoA.values().add(mvp);
        }
        if (module.isExternalSource() != defaultExternalSource) {
          MemberValuePair mvp = ast.newMemberValuePair();
          mvp.setName(ast.newSimpleName("externalSource"));
          mvp.setValue(ast.newBooleanLiteral(module.isExternalSource()));
          javaTaskInfoA.values().add(mvp);
        }

        acu.accept(new ASTVisitor() {
          @Override
          public boolean visit(TypeDeclaration node) {
            rewriter.getListRewrite(node, node.getModifiersProperty())
                .insertAt(javaTaskInfoA, 0, null);
            return false;
          }
        });
      }
      // ----------------------------------------------------------------

      //
      // extends JavaTask
      //
      acu.accept(new ASTVisitor() {

        @Override
        public boolean visit(TypeDeclaration node) {
          SimpleType st = ast
              .newSimpleType(ast.newSimpleName(JavaTask.class.getSimpleName()));
          rewriter.set(node, TypeDeclaration.SUPERCLASS_TYPE_PROPERTY, st,
              null);
          return false;
        }
      });
      // ----------------------------------------------------------------

      //
      // Parameters
      //
      for (

      final Parameter p : module.getParameters()) {
        acu.accept(new ASTVisitor() {
          public boolean visit(TypeDeclaration node) {
            VariableDeclarationFragment fragment = ast
                .newVariableDeclarationFragment();
            String var = p.getName().replace(" ", "_").toUpperCase();
            fragment.setName(ast.newSimpleName(var));
            StringLiteral init = ast.newStringLiteral();
            init.setLiteralValue(p.getName());
            fragment.setInitializer(init);
            FieldDeclaration field = ast.newFieldDeclaration(fragment);
            field.setType(
                ast.newSimpleType(ast.newName(String.class.getSimpleName())));

            List<Modifier> modifs = ast.newModifiers(
                Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL);
            field.modifiers().addAll(modifs);

            rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
                .insertLast(field, null);
            return false;
          }
        });
      }
      if (!module.getParameters().isEmpty()) {
        acu.accept(new ASTVisitor() {
          public boolean visit(TypeDeclaration node) {
            ASTNode newLine = rewriter.createStringPlaceholder("",
                ASTNode.EMPTY_STATEMENT);
            rewriter.getListRewrite(node, node.getBodyDeclarationsProperty())
                .insertLast(newLine, null);
            return false;
          }
        });
      }

      //
      // InputPort
      //
      for (final In in : module.getInputs()) {
        acu.accept(new ASTVisitor() {
          public boolean visit(TypeDeclaration node) {
            VariableDeclarationFragment fragment = ast
                .newVariableDeclarationFragment();
            String var = WordUtils.capitalizeFully(in.getName())
                .replace(" ", "").trim();
            var = Character.toLowerCase(var.charAt(0)) + var.substring(1);
            fragment.setName(ast.newSimpleName(var));
            FieldDeclaration field = ast.newFieldDeclaration(fragment);
            field.setType(ast
                .newSimpleType(ast.newName(InputPort.class.getSimpleName())));

            List<Modifier> modifs = ast.newModifiers(Modifier.PUBLIC);
            field.modifiers().addAll(modifs);

            final NormalAnnotation inputPortInfoA = ast.newNormalAnnotation();
            inputPortInfoA.setTypeName(
                ast.newSimpleName(InputPortInfo.class.getSimpleName()));

            MemberValuePair mvpName = ast.newMemberValuePair();
            mvpName.setName(ast.newSimpleName("name"));
            StringLiteral vName = ast.newStringLiteral();
            vName.setLiteralValue(in.getName());
            mvpName.setValue(vName);
            inputPortInfoA.values().add(mvpName);

            MemberValuePair mvpDt = ast.newMemberValuePair();
            mvpDt.setName(ast.newSimpleName("dataType"));
            TypeLiteral vDt = ast.newTypeLiteral();
            vDt.setType(ast.newSimpleType(ast.newName(in.getDataType())));
            mvpDt.setValue(vDt);
            inputPortInfoA.values().add(mvpDt);

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
      // ----------------------------------------------------------------

      //
      // OutputPort
      //
      for (final Out out : module.getOutputs()) {
        acu.accept(new ASTVisitor() {
          public boolean visit(TypeDeclaration node) {
            VariableDeclarationFragment fragment = ast
                .newVariableDeclarationFragment();
            String var = WordUtils.capitalizeFully(out.getName())
                .replace(" ", "").trim();
            var = Character.toLowerCase(var.charAt(0)) + var.substring(1);
            fragment.setName(ast.newSimpleName(var));
            FieldDeclaration field = ast.newFieldDeclaration(fragment);
            field.setType(ast
                .newSimpleType(ast.newName(OutputPort.class.getSimpleName())));

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
      // ----------------------------------------------------------------

      //
      // Methods
      //
      if (module.isRunnerStart()) {
        acu.accept(new ASTVisitor() {
          @Override
          public boolean visit(TypeDeclaration node) {
            addMethod(ast, rewriter.getListRewrite(node,
                node.getBodyDeclarationsProperty()), "runnerStart");
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
            svd.setType(ast
                .newSimpleType(ast.newName(InputPort.class.getSimpleName())));
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
            addMethod(ast, rewriter.getListRewrite(node,
                node.getBodyDeclarationsProperty()), "generate");
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
            addMethod(ast, rewriter.getListRewrite(node,
                node.getBodyDeclarationsProperty()), "runnerStop");
            return false;
          }
        });
      }
      // ----------------------------------------------------------------

      TextEdit edits = rewriter.rewriteAST();
      Document doc = new Document(cu.getSource());
      edits.apply(doc);
      cu.getBuffer().setContents(doc.get());
      cu.getBuffer().save(null, true);
    } catch (JavaModelException | MalformedTreeException
        | BadLocationException e) {
    }
  }

  private void imports(AST ast, ListRewrite lrw, Module module) {
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
      addImport(ast, lrw, SicParameter.class.getCanonicalName());
    }

    Stream
        .concat(module.getInputs().stream().map(In::getDataType),
            module.getOutputs().stream().map(Out::getDataType))
        .distinct()
        .map(dt -> dataTypes.stream()
            .filter(dti -> dti.getSimpleName().equals(dt)).findFirst())
        .filter(Optional::isPresent).map(Optional::get)
        .map(Class::getCanonicalName)
        .forEach(clazz -> addImport(ast, lrw, clazz));
  }

  private static void addImport(AST ast, ListRewrite lrw,
      String canonicalName) {
    ImportDeclaration id = ast.newImportDeclaration();
    id.setName(ast.newName(canonicalName));
    lrw.insertLast(id, null);
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

  public static CompilationUnit parse(ICompilationUnit unit) {
    ASTParser parser = ASTParser.newParser(AST.JLS14);
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setSource(unit);
    parser.setResolveBindings(true);
    return (CompilationUnit) parser.createAST(null);
  }
}
