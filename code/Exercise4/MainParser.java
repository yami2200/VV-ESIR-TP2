package code.Exercise4;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.SourceRoot;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainParser {

    public static void main(String[] args) throws IOException {
        /*
        if (args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }
        */
        String path = "/home/pataubeur/IdeaProjects/commons-lang/src";
        File file = new File(path);
        if (!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

        SourceRoot root = new SourceRoot(file.toPath());
        PublicElementsPrinter printer = new PublicElementsPrinter();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(printer, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        Set<String> getVariables = printer.getGetVariables();
        Set<Variable> privateVariables = printer.getPrivateVariables();
        List<Variable> variablesSansGet = new ArrayList<>();
        for(Variable v : privateVariables) {
            if(!getVariables.contains(v.getNom())) {
                variablesSansGet.add(v);
            }
        }

        File file_export = new File("/home/pataubeur/IdeaProjects/VV-ESIR-TP2/code/Exercise4/export.csv");
        try {
        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file_export);

        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

        // adding header to csv
        String[] header = {"Nom", "Classe", "Paquet"};
        writer.writeNext(header);

        for(Variable v : variablesSansGet) {
            String[] data = {v.getNom(), v.getClasse(), v.getPaquetage()};
            writer.writeNext(data);
        }

        writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
