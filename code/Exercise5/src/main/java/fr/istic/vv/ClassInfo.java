package fr.istic.vv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ClassInfo {

    public HashSet<String> variables;
    public HashMap<String, MethodInfo> methods;
    public String name;
    public int connections;
    public double NP;
    public HashMap<String, HashMap<String, List<String>>> directedConnectedMethods;
    public ClassInfo(String name) {
        this.name = name;
        this.variables = new HashSet<>();
        this.methods = new HashMap<>();
        this.directedConnectedMethods = new HashMap<>();
    }
}
