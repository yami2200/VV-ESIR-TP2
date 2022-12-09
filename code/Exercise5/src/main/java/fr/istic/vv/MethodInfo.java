package fr.istic.vv;

import java.util.HashSet;

public class MethodInfo {
    public String name;
    public HashSet<String> variablesUsage;
    public HashSet<String> methodsCall;
    public ClassInfo classOfMethod;

    boolean isPublic;

    public MethodInfo(String name, ClassInfo classOfMethod, boolean isPublic) {
        this.name = name;
        this.isPublic = isPublic;
        this.variablesUsage = new HashSet<>();
        this.methodsCall = new HashSet<>();
        this.classOfMethod = classOfMethod;
    }
}
