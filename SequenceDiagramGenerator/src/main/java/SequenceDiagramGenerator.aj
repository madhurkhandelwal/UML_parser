// TODO change the name of the file/class

// Creates sequence diagram in the target folder of maven
// TODO change it not depend on mvn target folder

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileWriter;
import java.io.File;
import java.util.Stack;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import net.sourceforge.plantuml.SourceStringReader;


class Counter{
    // TODO check equivalent of __doc__ in Java
    private String base;
    private int count;

    protected Counter(){
        // TODO check syntax to call the other Counter constructor
        this.base = "1";
        this.count = 1;
    }

    protected Counter(String base){
        this.base = base;
        this.count = 1;
    }

    Counter getNextCounter(){
        String newBase;
        if (base == ""){
            newBase = String.valueOf(count);
        }
        else{
            newBase = base + "." + String.valueOf(count);
        }
        count += 1;
        return new Counter(newBase);
    }

    // TODO just for fun want to make this protected,
    // so can i avoid inheritenc from Object?
    public String toString(){
        return base;
    }
}

public aspect SequenceDiagramGenerator{
    // TODO check if aspects can have instance variables
    // need to make these attributes non-static
    static private Stack<String> currentMethodStack= new Stack<String>();
    static private Stack<Counter> counterStack= new Stack<Counter>();
    static private String planUMLGrammar="";
    static private boolean start=true;

    pointcut traceMethods():  call(* *.*(..))   // All methods
                              && !call(* java..*(..)) // Don't want java language inbuilt objects
                              && !within(SequenceDiagramGenerator) // Not within our aspect itself, to avoid infinite reursion
                              && !within(Counter);

    pointcut traceConstructors():  execution(*.new(..)) // All constructors
                                   && !within(SequenceDiagramGenerator)
                                   && !within(Counter);

    pointcut createSeqImage():  execution(* Main.main(..));

    // TODO exploer java.io.PrintStream
    void around(String str) : call(void java.io.PrintStream.println(String))
                                && !within(SequenceDiagramGenerator)
                                && !within(Counter)
                                && args(str){
        planUMLGrammar = planUMLGrammar + "note right : " + str + "\n";
        proceed(str);
    }

    after(): createSeqImage(){
        // start = false;
        String note = "note left of aMain #FFAAAA\n<color #118888>Madhur Khandelwal\nmadhurkhandelwal.234@gmail.com \nend note\n";
        planUMLGrammar = planUMLGrammar + note;
        // System.out.println("_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+");
        // System.out.println("@startuml\n" + 
        //           planUMLGrammar +
        //           "\n@enduml");
        // System.out.println("_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+");

        String jarFileLoc = Counter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(jarFileLoc);
        String parentPath = file.getAbsoluteFile().getParent();
        Path path = Paths.get(parentPath, "sequenceDiagram.png");
        String outputPath = path.toString();

        createImage("@startuml\n" + planUMLGrammar + "\n@enduml", outputPath);
    }

    before(): createSeqImage(){
      start = true;
    }


    before() : traceConstructors() {
        if(start){
            Signature s =
            thisJoinPointStaticPart.getSignature();
            String methodName = s.getName();

            // use "new" for constructors.
            if (methodName.equals("") || methodName.equals("<init>")){
                methodName = "new";
            }

            // TODO show constructors in the sequence diagram
            // System.out.println(s.getDeclaringType().getSimpleName() + "."  + methodName + " {");
        }
    }

    after() : traceConstructors() {
        // if(start){
        //    System.out.println("}");
        // }
    }
  

    Object around() : traceMethods() {
    if(start){
        // System.out.println("##################START##################");

        MethodSignature signature = (MethodSignature)thisJoinPoint.getSignature();

        String[] parameterNames = signature.getParameterNames();
        Object[] parameterValues = thisJoinPoint.getArgs();

        //String calleClassName = signature.getDeclaringTypeName();
        String calleClassName;
        if (thisJoinPoint.getTarget() == null){
            calleClassName = signature.getDeclaringTypeName();
            // System.out.println("null aaya tha: " + thisJoinPoint.getTarget());
        }
        else{
            // TODO have diff line for objects of same type
            // System.out.println("no null: " + thisJoinPoint.getTarget());
            calleClassName = thisJoinPoint.getTarget().toString().split("@")[0];
        }
        if ("aeiou".indexOf(Character.toLowerCase((calleClassName.charAt(0)))) < 0){
            calleClassName = "a" + calleClassName;
        }
        else{
            calleClassName = "an" + calleClassName;
        }
        // System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        // System.out.println(calleClassName);
        // System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        String calleMethodName = signature.getName();
        String returnTypeName = signature.getReturnType().getName();


        // TODO check any functionality to explore all methods like 'dir' in python
        // maybe use reflection(why such name) API to iterate over the methods and call them
        // System.out.println("thisJoinPoint getArgs  " + thisJoinPoint.getArgs());
        // System.out.println("thisJoinPoint getKind  " + thisJoinPoint.getKind());
        // System.out.println("thisJoinPoint getSignature  " + thisJoinPoint.getSignature());
        // System.out.println("thisJoinPoint getSourceLocation  " + thisJoinPoint.getSourceLocation());
        // System.out.println("thisJoinPoint getStaticPart  " + thisJoinPoint.getStaticPart());
        // System.out.println("thisJoinPoint getTarget  " + thisJoinPoint.getTarget());
        // System.out.println("thisJoinPoint getThis  " + thisJoinPoint.getThis());
        // System.out.println("thisJoinPoint toLongString  " + thisJoinPoint.toLongString());
        // System.out.println("thisJoinPoint toShortString  " + thisJoinPoint.toShortString());
        // System.out.println("thisJoinPoint toString  " + thisJoinPoint.toString());


        // System.out.println("thisJoinPointStaticPart getKind" + thisJoinPointStaticPart.getKind());
        // System.out.println("thisJoinPointStaticPart getSignature" + thisJoinPointStaticPart.getSignature());
        // System.out.println("thisJoinPointStaticPart getSourceLocation" + thisJoinPointStaticPart.getSourceLocation());
        // System.out.println("thisJoinPointStaticPart toLongString" + thisJoinPointStaticPart.toLongString());
        // System.out.println("thisJoinPointStaticPart toShortString" + thisJoinPointStaticPart.toShortString());
        // System.out.println("thisJoinPointStaticPart toString" + thisJoinPointStaticPart.toString()  );


        // System.out.println("thisEnclosingJoinPointStaticPart getKind" + thisEnclosingJoinPointStaticPart.getKind());
        // System.out.println("thisEnclosingJoinPointStaticPart getSignature" + thisEnclosingJoinPointStaticPart.getSignature());
        // System.out.println("thisEnclosingJoinPointStaticPart getSourceLocation" + thisEnclosingJoinPointStaticPart.getSourceLocation());
        // System.out.println("thisEnclosingJoinPointStaticPart toLongString" + thisEnclosingJoinPointStaticPart.toLongString());
        // System.out.println("thisEnclosingJoinPointStaticPart toShortString" + thisEnclosingJoinPointStaticPart.toShortString());
        // System.out.println("thisEnclosingJoinPointStaticPart toString" + thisEnclosingJoinPointStaticPart.toString()  );

        // TODO 
        String callerClass="";
        if (!currentMethodStack.empty()){
            callerClass = currentMethodStack.peek();
        }

        int i = 0;
        String argString="";
        for (String x: parameterNames){
            if (argString != ""){
                argString = argString + ",";
            }
            argString = argString + x + ":" + parameterValues[i].getClass().getSimpleName();
            i++;
        }

        // System.out.println("");

        // System.out.println("-> " + signature.toString());
        String currentLine1;
        String countString = updateCounter();
        if (countString != "" ){
            currentLine1 = String.format("%s -> %s: %s : %s(%s): %s\n", callerClass, calleClassName, countString,calleMethodName, argString, returnTypeName);
        }else{
            currentLine1 = String.format("%s -> %s: %s(%s): %s\n", callerClass, calleClassName, calleMethodName, argString, returnTypeName);
        }
        String currentLine2 = String.format("activate %s\n", calleClassName);
        // System.out.println("currentLine1: " + currentLine1);

        planUMLGrammar = planUMLGrammar + currentLine1 + currentLine2;
        currentMethodStack.push(calleClassName);
        for (String x: parameterNames){
            // System.out.print("----------" + x + " ");
        }
        // System.out.println("");

        Object result = proceed();

        popCounter();
        String currentLine3 = String.format("%s --> %s\n", calleClassName, callerClass);

        String currentLine4 = String.format("deactivate %s\n", calleClassName);

        planUMLGrammar = planUMLGrammar + currentLine3 + currentLine4;
        currentMethodStack.pop();

        // System.out.println("<- " + signature.toString());
        // System.out.println("##################END###################");

        return result;
    }
    else{
        Object result = proceed();
        return result;
    }
  }

    private static void appendLineToFile(String line, String filePath){
        try{
            FileWriter fw = new FileWriter(filePath, true); //the true will append the new data
            fw.write(line);//appends the string to the file
            fw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void createImage(String plantUmlString, String filePath){
        SourceStringReader ssr = new SourceStringReader(plantUmlString);
        try (FileOutputStream imageFile = new FileOutputStream(filePath)) {
            ssr.generateImage(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String updateCounter(){
        Counter aCounter;
        if (counterStack.empty()){
            aCounter = new Counter();
        }
        else{
            aCounter = counterStack.peek().getNextCounter();
        }
        counterStack.push(aCounter);
        return aCounter.toString();
    }

    private static void popCounter(){
        counterStack.pop();
    }
}
