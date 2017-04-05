package com.madhur.uml_parser;

public class SeqDiagramGenerator {
    private String inputFolder, outputFolder;

    public SeqDiagramGenerator(String inputFolder, String outputFolder){
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
    }

    public void generate(){
        System.out.println("Generating sequence diagram(s)...");
    }
}
