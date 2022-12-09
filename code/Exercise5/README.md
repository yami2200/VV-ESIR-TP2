# Code of your exercise

## All reports made with the application :

- [CLI](./report/Cli/report.md)
- [Collections](./report/Collections/report.md)
- [Lang](./report/Lang/report.md)
- [Maths](./report/Maths/report.md)

# Report

```java

    /**
     * Generate histogram of TCC of all classes
     */
    public static void createHistogram(){
        // Create histogram of the number of methods per class with JFreeChart
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (ClassInfo classInfo : TCC.classes) {
            if(!(classInfo.NP<1)){
                dataset.addValue((classInfo.connections/classInfo.NP), "Number of methods", classInfo.name);
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "TCC Histogram",
                "Class", "TCC",
                dataset, PlotOrientation.VERTICAL,
                false, true, false);

        int width = 640;    /* Width of the image */
        int height = 480;   /* Height of the image */
        File BarChart = new File( pathReport+"histogram.jpeg" );
        // save barchart to image file
        try {
            ChartUtils.saveChartAsJPEG( BarChart , barChart , width , height );
        } catch (IOException e) {
            System.err.println("Failed to create histogram!" + e.getMessage());
            System.err.println("Report will not be saved");
            System.exit(0);
        }
    }

    /**
     * Generate dependency graph for each class
     */
    public static void generateGraphs(){
        for(ClassInfo classInfo : TCC.classes){
            String graph = "digraph G {\n";
            for(MethodInfo methodInfo : classInfo.methods.values()){
                if(methodInfo.isPublic) graph+=methodInfo.name+";\n";
            }
            for (String method1 : classInfo.directedConnectedMethods.keySet()) {
                for (String method2 : classInfo.directedConnectedMethods.get(method1).keySet()) {
                    String label =" [label=\" " ;
                    for (int i = 0; i < classInfo.directedConnectedMethods.get(method1).get(method2).size(); i++) {
                        label+=classInfo.directedConnectedMethods.get(method1).get(method2).get(i)+(i==classInfo.directedConnectedMethods.get(method1).get(method2).size()-1?"":", ");
                    }
                    label+="\", dir=none]";
                    graph+=method1+" -> "+method2+label+";\n";
                }
            }
            graph+="}";

            try {
                Path path = Paths.get(pathReport+"graphs/"+classInfo.name+".dot");
                Files.write(path, graph.getBytes());
            } catch (IOException e) {
                System.err.println("Failed to create graph!" + e.getMessage());
                System.err.println("Report will not be saved");
                System.exit(0);
            }
        }

    }

    /**
     * Create a TXT report of the TCC analysis
     * @param path the path to the source code
     */
    public static void createTXTReport(File path){
        String text = "# Report TCC\n## Path = "+path.getAbsolutePath()+"\n\n";
        text += "> \n" +
                "> Histogram :\n" +
                "> \n" +
                "> ![Histogram](./histogram.jpeg)\n\n";
        text += "|Class|TCC|\n";
        text += "|---|---|\n";
        for(ClassInfo classInfo : TCC.classes){
            if(!(classInfo.NP<1)){
                text+="|["+classInfo.name+"](./graphs/"+classInfo.name+".dot)|"+classInfo.connections+"/"+(int)classInfo.NP+" = "+(classInfo.connections/classInfo.NP)+"|\n";
            }
        }
        try {
            Files.write(Paths.get(pathReport+"report.md"), text.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to create report.md!" + e.getMessage());
            System.err.println("Report will not be saved");
            System.exit(0);
        }
    }
    
```

# TCC

```java
 /**
     * This method is called when a class is visited
     * @param declaration Class declaration
     * @param arg Void
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        if(!declaration.getFullyQualifiedName().isPresent()) return;

        // Create class information object
        ClassInfo classInfo = new ClassInfo(declaration.getFullyQualifiedName().get());
        classes.add(classInfo);

        List<FieldDeclaration> fields = declaration.getFields();

        // get all attributes of the class
        for (FieldDeclaration field : fields) {
            HashSet<String> found = new HashSet<>();
            field.getVariables().forEach(variable -> {
                if(!found.contains(variable.getNameAsString())){
                    classInfo.variables.add(variable.getNameAsString());
                    found.add(variable.getNameAsString());
                }
            });
        }

        // get all methods of the class
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

        // process TCC
        int N = (int) classInfo.methods.values().stream().filter(methodInfo -> methodInfo.isPublic).count();
        double NP = N*(N-1.0)/2.0;

        classInfo.NP = NP;
        processUndirectVariablesUsages(classInfo);
        classInfo.connections = getDirectConnections(classInfo);
    }

    /**
     * Add to each method the variables used by methods called by this method (deep search)
     * @param classInfo the class to process
     */
    private void processUndirectVariablesUsages(ClassInfo classInfo){
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

    /**
     * Add to the method the variables used by the currentmethod and the methods called by the currentmethod (deep search)
     * @param classInfo class to process
     * @param method add variables usages found, to this method
     * @param currentMethod current method to process
     * @param visited list of already visited methods
     */
    private void addAllVariablesUsageRecur(ClassInfo classInfo, MethodInfo method, MethodInfo currentMethod, HashSet<MethodInfo> visited){
        method.variablesUsage.addAll(currentMethod.variablesUsage);

        for (String methodCall : currentMethod.methodsCall) {
            if(classInfo.methods.containsKey(methodCall) && !visited.contains(classInfo.methods.get(methodCall))){
                MethodInfo calledMethodInfo = classInfo.methods.get(methodCall);
                visited.add(calledMethodInfo);
                addAllVariablesUsageRecur(classInfo, method, calledMethodInfo, visited);
            }
        }
    }

    /**
     * Get the direct connections between methods with attributes usage
     * @param classInfo the class to process
     * @return the direct connections
     */
    private int getDirectConnections(ClassInfo classInfo){
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

```