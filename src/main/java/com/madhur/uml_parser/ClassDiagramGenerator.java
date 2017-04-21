package com.madhur.uml_parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class ClassDiagramGenerator{
    private String inputFolder, outputFolder, outputFileName;
    private HashMap<String, Boolean> isInterface; // contains all classes and interface within the proj dir
    private final String yumlURLstr = "https://yuml.me/diagram/plain/class/";
    private HashSet<String> compositionString, implementsSet, extendsSet;
    private HashMap<String, Boolean[]> mulMap;
    private HashMap<String, ArrayList> methodsStringMap;

    public ClassDiagramGenerator(String inputFolder, String outputFolder){
        this(inputFolder, outputFolder, "output.png");
    }

    public ClassDiagramGenerator(String inputFolder, String outputFolder, String outputFileName){
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.outputFileName = outputFileName;
        isInterface = new HashMap<String, Boolean >();
        compositionString = new HashSet<String>();
        implementsSet = new HashSet<String>();
        extendsSet= new HashSet<String>();
        mulMap = new HashMap<String, Boolean[]>();
    }

    private static ArrayList<ClassOrInterfaceDeclaration> getAllClassOrInterfaceDeclarations(String inputFolder) throws IOException{
        String[] javaFiles;
        CompilationUnit cu;

        javaFiles = getAllJavaFiles(inputFolder);
        ArrayList<ClassOrInterfaceDeclaration> coiList = new ArrayList<ClassOrInterfaceDeclaration>();
        for (String javaFile : javaFiles){
            // System.out.println("Parsing java file: " + javaFile + "...");
            cu = JavaParser.parse(new File(inputFolder, javaFile));

            // Assuming only classes or interfaces are present
            List<TypeDeclaration<?>> classOrInterfaceList = cu.getTypes();
            for (Node n : classOrInterfaceList){
                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                coiList.add(coi);
            }
        }
        return coiList;
    }

    public void generate() throws Exception {
        String yumlString="";
        System.out.println("Generating class diagram(s)...");

        ArrayList<ClassOrInterfaceDeclaration>coiList = getAllClassOrInterfaceDeclarations(inputFolder);

        // populate isInterface hash map
        for(ClassOrInterfaceDeclaration coi: coiList){
            isInterface.put(coi.getNameAsString(), coi.isInterface());
        }

        // Get just the class diagrams without any relationships
        for(ClassOrInterfaceDeclaration coi: coiList){
            methodsStringMap = new HashMap<String, ArrayList>(); // reset
            yumlString += getClassDiagramWithoutRelationshipString(coi) + ",";
        }

        // Get the relationships
        for(String temp: compositionString){
            yumlString += "," + temp;
        }
        for(String temp: extendsSet){
            yumlString += "," + temp;
        }
        for(String temp: implementsSet){
            yumlString += "," + temp;
        }
        yumlString += "," + getCompositionString(); // TODO why do i have composition string taken care of twice
        generateImageFromYUMLString(yumlString);
    }

    private static String[] getAllJavaFiles(String folder){
        // Return only the .java files present in the folder provided (which is the only required condition)
        // TODO add support to walk through all folders
        //System.out.println(folder);
        File inputFolder = new File(folder);

        // Using anonymous inner class just for fun
        String[] javaFiles =  inputFolder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
                }
            });

        // TODO use logging
        // for (String javaFile: javaFiles)
        //     System.out.println(javaFile);
        return javaFiles;
    }

    // TODO correct the name of the method
    // TODO store uses info in a DS and then finally generate the yUML string in a separate method
    private void updateUsesString(String classA, String classB){
        String retval="[%s]-.->[%s%s]";
        String interfaceString;

        if (isInterface.containsKey(classB) && !isInterface.get(classA)) {
            if (isInterface.get(classB)) {
                interfaceString = "<<interface>>;";
                compositionString.add(String.format(retval, classA, interfaceString, classB));
            }
            else{
                // As per the requirements, no need to show uses relationship between concrete classes
            }

        }
    }

    private String getClassDiagramWithoutRelationshipString(ClassOrInterfaceDeclaration coi) throws Exception {
        String yumlString;
        String interfaceStr="", classInterfaceName, attrs="";
        List<String> setterGetterAttributes;

        // Name and interface attribute
        interfaceStr = coi.isInterface() ? "<<interface>>;" : "";
        classInterfaceName = coi.getNameAsString();

        // Get Constructor info
        // TODO find a better way
        for (Object bd : ((TypeDeclaration) coi).getMembers()) {
            if (bd instanceof ConstructorDeclaration) {
                ConstructorDeclaration cd = ((ConstructorDeclaration) bd);
                if (cd.getDeclarationAsString().startsWith("public")
                        && !coi.isInterface()) {
                    String constructorAccessModifier = "+";
                    String constructorName = cd.getNameAsString();
                    String constructorParameters = "";
                    
                    constructorParameters = getParametersString(classInterfaceName, cd.getParameters());

                    ArrayList<String> methodStrList = new ArrayList<String>();
                    if (methodsStringMap.containsKey(constructorName)){
                        methodStrList =  methodsStringMap.get(constructorName);
                    }
                    methodStrList.add(String.format("%s%s(%s)", constructorAccessModifier, constructorName, constructorParameters));
                    methodsStringMap.put(constructorName, methodStrList);
                }
                parseMethodBody(classInterfaceName, (BodyDeclaration) bd);
            }
        }

        // Get methods
        for(MethodDeclaration method: coi.getMethods()){
            // We will we showing only public methods
            if (!method.getDeclarationAsString().startsWith("public")) continue;

//            if isMethodGetterSetter(method){
//                setterGetterAttributes.add(mName.substring(3).toLowerCase());
//                continue;
//            }

            String mModifier = "+";
            String mName = method.getNameAsString();
            String mParas= getParametersString(classInterfaceName, method.getParameters());
            String mReturnType= method.getType().toString();

            parseMethodBody(classInterfaceName, (BodyDeclaration) method);

            ArrayList<String> methodStrList = new ArrayList<String>();
            if (methodsStringMap.containsKey(mName)){
                methodStrList =  methodsStringMap.get(mName);
            }
            methodStrList.add( String.format("%s%s(%s):%s", mModifier, mName, mParas, mReturnType));
            methodsStringMap.put(mName, methodStrList);
        }

        // Attributes
        List<FieldDeclaration> fields = coi.getFields();
        for(FieldDeclaration field: fields){
            String fieldString = field.toString();

            // hack to remove comments starting with //
			// TODO take care of comments starting with /*
            if (field.toString().startsWith("//")){
                String lines[] = field.toString().split("\\R");
                for(String line: lines){
                    if(!line.startsWith("//")){
                        fieldString = line;
                        // System.out.println("####");
                        // System.out.println(line);
                        // System.out.println("####");
                        break;
                    }
                }
            }

            // Get modifier
            String fModifier = "";
            String modifier = fieldString.split(" ")[0];
            // System.out.print("~~~~ ");
            // for(String s: fieldString.split(" ")) {
            //      System.out.print(s + " ");
            // }
            // System.out.println("~~~~");

            if (modifier.equals("private")){
                fModifier = "-";
            }else if(modifier.equals("public")){
                fModifier = "+";
            }
            else{
                continue;
            }

            //name
            String fName = fieldString.split(" ")[2].split(";")[0];
            String fType = fieldString.split(" ")[1].split(";")[0]; // TODO take care of generics

            //check for gettersetter
            if(fModifier == "-" && hasGetterSetter(fName, fType)){ //TODO what if fModifier is + and it has setters/ge..
                fModifier = "+";
                removeGettersSettersFromMap(fName);
            }


            //static
            //TODO
            if(!updateCompositionDS(classInterfaceName, fType)) {
                //TODO Mulitplicity
                attrs += fModifier + fName.split(";")[0] + ":" + fType + ";";
            }

        }

        // Generate methodString
        String methodsString="";
        for(String methodName: methodsStringMap.keySet()){
            ArrayList<String> methodStrLst =  methodsStringMap.get(methodName);
            for(String methodStr: methodStrLst){
                methodsString += methodStr + ";";
            }
        }


        if(!attrs.equals("") | !methodsString.equals("")) attrs = "|" + attrs; // TODO why methodString.eq...
        if(!methodsString.equals("")) methodsString = "|" + methodsString;
        yumlString = String.format("[%s%s%s%s]", interfaceStr, classInterfaceName, attrs, methodsString);
        // System.out.println(yumlString);
        updateImplementsSet(coi);
        updateInheritsSet(coi);
        return yumlString;
    }

    private void parseMethodBody(String name, BodyDeclaration body) throws Exception {
        // parse body of method or constructor to find 'uses' relations and update the usesSet
        Object temp;
        if (body instanceof MethodDeclaration){
            temp = (Object)((MethodDeclaration)body).getBody();
        }
        else if (body instanceof ConstructorDeclaration){
            temp = (Object)((ConstructorDeclaration)body).getBody();
        }
        else{
            throw new Exception("Expecting either a method or constructor");
        }
        String methodBody[] = temp.toString().split(" ");
        for (String word : methodBody) {
            if (isInterface.containsKey(word)) {
                updateUsesString(name, word);
            }
        }
    }

    // TODO move yUML logic to a separate method
    private void updateImplementsSet(ClassOrInterfaceDeclaration coi){
        String name = coi.getNameAsString();
        String fmt2 = "[%s]-.-^[%s]";
        if (coi.getImplementedTypes() != null) {
            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) coi
                    .getImplementedTypes();
            for (ClassOrInterfaceType intface : interfaceList) {
                implementsSet.add(String.format("[%s]-.-^[<<interface>>;%s]", name, intface));
            }
        }
    }

    // TODO move yUML logic to a separate method
    private void updateInheritsSet(ClassOrInterfaceDeclaration coi){
        String name = coi.getNameAsString();
        String fmt = "[%s]-^[%s]";
        if (coi.getExtendedTypes() != null ) {
            List<ClassOrInterfaceType> interfaceList = (List<ClassOrInterfaceType>) coi
                    .getExtendedTypes();
            for (ClassOrInterfaceType cType : interfaceList) {
                extendsSet.add(String.format("[%s]-^[%s]", name, cType));
            }
        }
    }

    private void generateImageFromYUMLString(String yumlString) throws IOException {
        // workaround for bug in yUML https://groups.google.com/forum/#!msg/yuml/HdKpszaIyKE/RpjRz_jPzNkJ
        yumlString = yumlString.replace("[]", "\uFF3B*\uFF3D"); // TODO move * to its inception
        yumlString = yumlString.replace("<<", "\uFF1C\uFF1C");
        yumlString = yumlString.replace(">>", "\uFF1E\uFF1E");
        HttpURLConnection con = null;
        FileOutputStream outputFile = null;
        URL finalURL;

        // TODO use try with resources
        try{
            // TODO use some inbuilt method to add 2 paths
            outputFile = new FileOutputStream(outputFolder + "/" + outputFileName);
            String finalURLStr = yumlURLstr + URLEncoder.encode(yumlString, "UTF-8") + ".png";
            // TODO use logging
            // System.out.println(yumlURLstr + yumlString + ".png");
//            System.out.println("\n" + finalURLStr);

//            System.out.println( URLEncoder.encode(yumlString, "UTF-8"));
//            System.out.println(yumlString);

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
            // System.out.println("Output file: " + outputFolder + "/" + outputFileName);
        }catch (IOException ioe){
            // TODO handle it
            throw ioe;
        }finally {
            if (con != null) con.disconnect();
            if (outputFile != null) outputFile.close();
        }
    }

    private String getParametersString(String className, NodeList<Parameter> paras) {
        // Gets the method/constructor parameters in yUML format
        // Also update 'uses' set
        String mParas = "";
        for (Object methodChildNode : paras) {
            Parameter parameter = (Parameter) methodChildNode;
            String pType = parameter.getType().toString();

            String pName = parameter.getChildNodes().get(0).toString(); // don't know why 1st child
            String arg = pName + ":" + pType;
            if (mParas.equals("")) {
                mParas += arg;
            } else {
                mParas += ("," + arg);
            }
            updateUsesString(className, pType);
        }
        return mParas;
    }

    // not verified
    private boolean updateCompositionDS(String cName, String type){
        boolean updated=false;
        boolean many = false;
        String key;
        Boolean mul[] = {false, false};

        // TODO fix this
        // 1. In case of use of our class in any generic declaration
        // assuming its a collection
        // 2. Assuming only one set of <> is used
        if (type.matches(".*<.*>")) {
            type = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
            many = true;
        }
        if (type.matches(".*[.*]")) {
            many = true;
        }
        if(isInterface.containsKey(type) && isInterface.get(type)){
            updateUsesString(cName, type);
        }

        // System.out.print(cName + " -> " + type + ": ");

        if (isInterface.containsKey(type)) {
            if (type.compareTo(cName) >= 0){
                if (many) mul[1] = true;
                key = type + "--" + cName;
            }else{
                if (many) mul[0] = true;
                key = cName + "--" + type;
            }

            if (mulMap.containsKey(key)) {
                Boolean status[] =  mulMap.get(key);
                mul[0] = mul[0] | status[0];
                mul[1] = mul[1] | status[1];
            }
            mulMap.put(key, mul);
            updated = true;
        }
        // System.out.println(updated);
        return updated;
    }

    // not verified
    private String getCompositionString(){
        String retval = "";
        for(String key: mulMap.keySet()){
            String temp[] = key.split("--");
            Boolean val[] = mulMap.get(key);
            String connector = "-";
            connector = (val[1]?"*":"1") + connector + (val[0]?"*":"1");

            for (int i=0; i<temp.length; i++){
                if(isInterface.get(temp[i])){
                    temp[i] = "<<interface>>;" + temp[i];
                }
            }

            retval += String.format("[%s]%s[%s]", temp[0], connector, temp[1]) + ",";
        }
        // TODO remove trailing comma
        return retval;
    }

    private boolean hasGetterSetter(String fName, String fType){
        // TODO check for type
        String getterStr = getGetterStr(fName);
        String setterStr = getSetterStr(fName);

        // Assuming getters and setters are not overloaded
        if((methodsStringMap.containsKey(getterStr) && methodsStringMap.get(getterStr).get(0).toString().startsWith("+"))
                && (methodsStringMap.containsKey(setterStr)  && methodsStringMap.get(getterStr).get(0).toString().startsWith("+"))){
            // System.out.println(" true");
            return true;
        }else{
            // System.out.println(" false");
            return false;
        }
    }

    private void removeGettersSettersFromMap(String fName){
        String getterStr = getGetterStr(fName);
        String setterStr = getSetterStr(fName);

        if(methodsStringMap.containsKey(getterStr)){
            methodsStringMap.remove(getterStr);
        }
        if (methodsStringMap.containsKey(setterStr)){
            methodsStringMap.remove(setterStr);
        }
    }

    private String getGetterStr(String fName){
        return "get" + fName.toUpperCase().substring(0, 1) + fName.substring(1);
    }

    private String getSetterStr(String fName){
        return "set" + fName.toUpperCase().substring(0, 1) + fName.substring(1);
    }
}
