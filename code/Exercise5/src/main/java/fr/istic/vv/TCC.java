package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.util.*;

public class TCC extends VoidVisitorWithDefaults<Void> {

    public static ArrayList<ClassInfo> classes = new ArrayList<>();

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        if(!declaration.getFullyQualifiedName().isPresent()) return;

        ClassInfo classInfo = new ClassInfo(declaration.getFullyQualifiedName().get());
        classes.add(classInfo);

        List<FieldDeclaration> fields = declaration.getFields();

        for (FieldDeclaration field : fields) {
            HashSet<String> found = new HashSet<>();
            field.getVariables().forEach(variable -> {
                if(!found.contains(variable.getNameAsString())){
                    classInfo.variables.add(variable.getNameAsString());
                    found.add(variable.getNameAsString());
                }
            });
        }

        for (MethodDeclaration method : declaration.getMethods()) {
            if(method.isPublic()) {
                MethodInfo methodInfo = new MethodInfo(method.getNameAsString(), classInfo, method.isPublic());
                methodInfo.variablesUsage = new HashSet<>();

                method.findAll(NameExpr.class).forEach(nameExpr -> {
                    HashSet<String> found = new HashSet<>();
                    if (classInfo.variables.contains(nameExpr.getNameAsString()) && !found.contains(nameExpr.getNameAsString())) {
                        methodInfo.variablesUsage.add(nameExpr.getNameAsString());
                        found.add(nameExpr.getNameAsString());
                    }
                });

                if (methodInfo.isPublic) {
                    HashSet<String> found = new HashSet<>();
                    method.findAll(MethodCallExpr.class).forEach(methodCallExpr -> {
                        if (!found.contains(methodCallExpr.getNameAsString())) {
                            methodInfo.methodsCall.add(methodCallExpr.getNameAsString());
                            found.add(methodCallExpr.getNameAsString());
                        }
                    });
                }

                classInfo.methods.put(method.getNameAsString(), methodInfo);
            }
        }

        int N = (int) classInfo.methods.values().stream().filter(methodInfo -> methodInfo.isPublic).count();
        double NP = N*(N-1.0)/2.0;

        classInfo.NP = NP;
        processUndirectVariablesUsages(classInfo);
        classInfo.connections = getDirectConnections(classInfo);
    }

    public void processUndirectVariablesUsages(ClassInfo classInfo){
        for (MethodInfo methodInfo : classInfo.methods.values()) {
            for (String methodCall : methodInfo.methodsCall) {
                if(classInfo.methods.containsKey(methodCall)){
                    MethodInfo calledMethodInfo = classInfo.methods.get(methodCall);
                    HashSet<MethodInfo> visited = new HashSet<>();
                    visited.add(methodInfo);
                    visited.add(calledMethodInfo);
                    addAllVariablesUsageRecur(classInfo, methodInfo, calledMethodInfo, visited);
                    methodInfo.variablesUsage.addAll(calledMethodInfo.variablesUsage);
                }
            }
        }
    }

    public void addAllVariablesUsageRecur(ClassInfo classInfo, MethodInfo method, MethodInfo currentMethod, HashSet<MethodInfo> visited){
        method.variablesUsage.addAll(currentMethod.variablesUsage);

        for (String methodCall : currentMethod.methodsCall) {
            if(classInfo.methods.containsKey(methodCall) && !visited.contains(classInfo.methods.get(methodCall))){
                MethodInfo calledMethodInfo = classInfo.methods.get(methodCall);
                visited.add(calledMethodInfo);
                addAllVariablesUsageRecur(classInfo, method, calledMethodInfo, visited);
            }
        }
    }

    public int getDirectConnections(ClassInfo classInfo){
        int connections = 0;
        List<MethodInfo> methods = new ArrayList<>(classInfo.methods.values());
        for (int i = 0; i < methods.size(); i++) {
            MethodInfo methodInfo = methods.get(i);
            for (int j = i+1; j < methods.size(); j++) {
                MethodInfo methodInfo2 = methods.get(j);
                if(methodInfo2!=methodInfo){
                    boolean connected = false;
                    for (String variable : methodInfo.variablesUsage) {
                        if(methodInfo2.variablesUsage.contains(variable)){
                            if(!connected) {
                                connections++;
                                connected = true;
                                if(!classInfo.directedConnectedMethods.containsKey(methodInfo.name)) classInfo.directedConnectedMethods.put(methodInfo.name, new HashMap<>());
                                if(!classInfo.directedConnectedMethods.get(methodInfo.name).containsKey(methodInfo2.name)) classInfo.directedConnectedMethods.get(methodInfo.name).put(methodInfo2.name, new ArrayList<>());
                                classInfo.directedConnectedMethods.get(methodInfo.name).get(methodInfo2.name).add(variable);
                            } else {
                                classInfo.directedConnectedMethods.get(methodInfo.name).get(methodInfo2.name).add(variable);
                            }
                        }
                    }
                }
            }
        }
        return connections;
    }

}
