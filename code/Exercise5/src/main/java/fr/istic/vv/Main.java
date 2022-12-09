package fr.istic.vv;

import com.github.javaparser.utils.SourceRoot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Main {

    public static String pathReport;

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        File file = new File(args[0]);
        if(!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

        SourceRoot root = new SourceRoot(file.toPath());
        TCC tccProcessor = new TCC();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(tccProcessor, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        pathReport = "./report/"+java.time.LocalTime.now().getHour()+"-"+java.time.LocalTime.now().getMinute()+"-"+java.time.LocalTime.now().getSecond()+"/";

        try {
            Path path = Paths.get(pathReport+"graphs/");
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
            System.err.println("Report will not be saved");
            System.exit(0);
        }

        generateGraphs();
        createHistogram();
        createTXTReport(file.getAbsoluteFile());
    }

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
}
