package code.Exercise4;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


// This class visits a compilation unit and
// prints all public enum, classes or interfaces along with their public methods
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {

    private Set<String> getVariables = new HashSet<>();
    private Set<Variable> privateVariables = new HashSet<>();

    public Set<String> getGetVariables() {
        return getVariables;
    }

    public Set<Variable> getPrivateVariables() {
        return privateVariables;
    }

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if(!declaration.isPublic()) return;
        //System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));
        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
        // Printing nested types in the top level
        for(BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof TypeDeclaration) {
                member.accept(this, arg);
            }
        }
    }


    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        for(FieldDeclaration field : declaration.getFields()) {
             NodeList<VariableDeclarator> list = field.getVariables();
             for(VariableDeclarator vd : list) {
                 if(vd.getParentNode().toString().contains("private")) {
                     privateVariables.add(new Variable(String.valueOf(vd.getName()), declaration.getNameAsString(), declaration.getFullyQualifiedName().get().toString()));
                 }
             }
        }
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        if(!declaration.isPublic()) return;
        if(!declaration.getNameAsString().contains("get")||declaration.getNameAsString().contains("test")) return;
        if(declaration.getNameAsString().length() > 3) {
            getVariables.add(declaration.getNameAsString().split("get")[1].toLowerCase());
            //System.out.println("  " + declaration.getDeclarationAsString(true, true));
        }
    }

}
