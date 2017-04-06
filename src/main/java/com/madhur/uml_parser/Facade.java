package com.madhur.uml_parser;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

// TODO check whether its anti-pattern to use 'import something.*', if not, why so?

// TODO check style guide for Java, something similar to PEP8 maybe

public class Facade
{
    private String inputFolder, outputFolder;
    private boolean classDiagram=false, seqDiagram=false;

    private Facade(){
    }

    public static void main( String[] args ){
        Facade f = new Facade();
        f.parseArguments(args);

        // TODO there has to be a better way of expanding ~
        f.inputFolder = f.inputFolder.replaceFirst("^~", System.getProperty("user.home"));
        f.outputFolder = f.outputFolder.replaceFirst("^~", System.getProperty("user.home"));

        if (f.classDiagram){
            f.genClassDiagram();
        }
        if (f.seqDiagram){
            f.genSeqDiagram();
        }
    }

    private void parseArguments(String[] args)
    {
        Options options = new Options();

        Option classDiagramOpt = new Option("c", "Generate class diagram(s)");
        options.addOption( classDiagramOpt );

        Option seqDiagramOpt = new Option("s", "Generate sequence diagram(s)");
        options.addOption( seqDiagramOpt );

        Option inputOpt = new Option("i", "inputFolder", true, "input folder path");
        inputOpt.setRequired(true);
        options.addOption(inputOpt);

        Option outputOpt = new Option("o", "outputFolder", true, "output folder path");
        outputOpt.setRequired(true);
        options.addOption(outputOpt);


        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        try {
            line = parser.parse( options, args );

            // TODO remove below lines out ot th try catch construct
            if( line.hasOption( "c" ) ) {
                classDiagram = true;
            }
            if( line.hasOption( "s" ) ) {
                seqDiagram = true;
            }
            inputFolder = line.getOptionValue( "inputFolder" );
            outputFolder = line.getOptionValue( "outputFolder" );
        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            // TODO exit
        }
        // TODO use logger
        System.out.println(String.format("Outpath: %s\nInput Folder: %s\nClass Diagram: %s\nSeq Diagram: %s",
                                         outputFolder, inputFolder, classDiagram, seqDiagram));
    }

    private void genClassDiagram(){
        ClassDiagramGenerator cdg =  new ClassDiagramGenerator(inputFolder, outputFolder);
        try {
            cdg.generate();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO exit?
        }
    }

    private void genSeqDiagram(){
        SeqDiagramGenerator sdg =  new SeqDiagramGenerator(inputFolder, outputFolder);
        sdg.generate();
    }
}
