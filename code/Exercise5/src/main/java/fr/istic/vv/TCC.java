package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TCC extends VoidVisitorWithDefaults<Void> {

    private static HashMap<String, ClassInfo> classes = new HashMap<String, ClassInfo>();

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {

        ClassInfo classInfo = new ClassInfo();
        classInfo.variables = new HashSet<>();
        classes.put(declaration.getNameAsString(), classInfo);

        List<FieldDeclaration> fields = declaration.getFields();

        for (FieldDeclaration field : fields) {
            field.getVariables().forEach(variable -> {
                classInfo.variables.add(variable.getNameAsString());
            });
        }

        for (MethodDeclaration method : declaration.getMethods()) {
            if(method.isPublic()) {
                MethodInfo methodInfo = new MethodInfo(classInfo);
                methodInfo.variablesUsage = new HashSet<>();

                method.findAll(NameExpr.class).forEach(nameExpr -> {
                    if (classInfo.variables.contains(nameExpr.getNameAsString())) {
                        methodInfo.variablesUsage.add(nameExpr.getNameAsString());
                    }
                });

                method.findAll(MethodCallExpr.class).forEach(methodCallExpr -> {
                    methodInfo.methodsCall.add(methodCallExpr.getNameAsString());
                });
            }

        }
    }

}
