package com.madhur.uml_parser;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;


public class ClassDiagramGenerator{
    private String inputFolder, outputFolder;
    private HashMap<String, Boolean> isClassOrInterface;
    private final String yumlURLstr = "https://yuml.me/diagram/plain/class/";

    public ClassDiagramGenerator(String inputFolder, String outputFolder){
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        isClassOrInterface = new HashMap<String, Boolean>();
    }

    public void generate() throws IOException {
        String[] javaFiles;
        CompilationUnit cu;
        String yumlString="";

        System.out.println("Generating class diagram(s)...");
        javaFiles = getAllJavaFiles(inputFolder);
        for (String javaFile:javaFiles) {
            System.out.println("Parsing " + javaFile + "...");
            cu = JavaParser.parse(new File(inputFolder, javaFile));

            // Assuming only classes or interfaces are present
            // TODO add support for other cases as well
            List<TypeDeclaration<?>> classList = cu.getTypes();
            for (Node n : classList) {
                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                isClassOrInterface.put(coi.getNameAsString(), coi.isInterface());
                System.out.print("Found class/interface " + coi.getNameAsString() + ": ");

                yumlString = generateYUMLString(coi);
            }
        }
        generateImageFromYUMLString(yumlString);
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
        System.out.println(folder);
        File inputFolder = new File(folder);

        // Using anonymous inner class just for fun
        String[] javaFiles =  inputFolder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
                }
            });

        // TODO use logging
//        for (String javaFile: javaFiles)
//            System.out.println(javaFile);
        return javaFiles;
    }

    private String generateYUMLString(ClassOrInterfaceDeclaration coi){
        String yumlString;
        String interfaceStr="", name, attrs="", methods="";

        interfaceStr = coi.isInterface() ? "<<interface>>" : "";
        name = coi.getNameAsString();

        yumlString = String.format("[%s%s|%s|%s]", interfaceStr, name, attrs, methods);
        System.out.println(yumlString);
        return yumlString;
    }

    private void generateImageFromYUMLString(String yumlString) throws IOException {
        HttpURLConnection con = null;
        FileOutputStream outputFile = null;
        URL finalURL;

        // TODO use try with resources
        try{
            // TODO use some inbuilt method to add 2 paths
            outputFile = new FileOutputStream(outputFolder + "/classDiagram.png");
            String finalURLStr = yumlURLstr + URLEncoder.encode(yumlString, "UTF-8") + ".png";
            // TODO use logging
            //System.out.println(finalURLStr);
            finalURL = new URL(finalURLStr);

            con = (HttpURLConnection) finalURL.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            // TODO handle 301
            if (con.getResponseCode() != 200) {
                throw new RuntimeException("HTTP request failed," +
                                           "return code: " + con.getResponseCode());
            }
            InputStream is = con.getInputStream();

            int numBytes;
            byte[] buffer = new byte[1024]; // TODO check why 1024?
            while ((numBytes = is.read(buffer)) != -1) {
                outputFile.write(buffer, 0, numBytes);
            }
        }catch (IOException ioe){
            // TODO handle it
            throw ioe;
        }finally {
            if (con != null) con.disconnect();
            if (outputFile != null) outputFile.close();
        }
    }
}
