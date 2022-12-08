package fr.istic.vv;

import java.util.HashSet;

public class MethodInfo {
    public HashSet<String> variablesUsage;
    public HashSet<String> methodsCall;
    public ClassInfo classOfMethod;

    public MethodInfo(ClassInfo classOfMethod) {
        variablesUsage = new HashSet<>();
        methodsCall = new HashSet<>();
        classOfMethod = classOfMethod;
    }
}
