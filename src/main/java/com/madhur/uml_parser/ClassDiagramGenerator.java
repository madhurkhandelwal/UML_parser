package com.madhur.uml_parser;


import java.io.File;
import java.io.FilenameFilter;

public class ClassDiagramGenerator{
    private String inputFolder, outputFolder;

    public ClassDiagramGenerator(String inputFolder, String outputFolder){
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
    }

    public void generate(){
        System.out.println("Generating class diagram(s)...");
        String[] javaFiles = getAllJavaFiles(inputFolder);

        /*
        lambda expression to get all cus
        for each cu
            get all methods
            get all attributes
            get all implements interfaces
            get all inheritance
            get all associations
            append to uml string
        gerenate UML
        */
    }

    private static String[] getAllJavaFiles(String folder){
        // Return only the .java files present in the folder provided
        // TODO add support to walk through all folders
        folder = folder.replaceFirst("^~", System.getProperty("user.home"));
        File inputFolder = new File(folder);

        // Using anonymous inner class just for fun
        String[] javaFiles =  inputFolder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
                }
            });

        // TODO use logging
        for (String javaFile: javaFiles)
            System.out.println(javaFile);
        return javaFiles;
    }
}
