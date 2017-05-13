# UML Parser
Generate UML Java class/sequence diagram

### Requirments:
1. Java 1.8 or later
2. Apache Maven 3.3.9 or later
3. Python 2.7

### Libraries and tools used
1. Java - Language used for writing the class and sequence diagram generator
2. Javaparser - To get AST(Abstract Tree Syntax) of java code
3. Yuml - For generating class diagram image
4. Plant UML - For generating sequence diagram image
5. Maven - Build management for Java
6. Python - Language used to write a wrapper script to run the parser
7. AspectJ - Aspect oriented programming implementation for Java, used for generating sequence diagram
8. junit - Unit test framework for Java
9. commons-cli - Java library to parse command line arguments



### Steps to run:
1. Clone this repo
2. Run ```./setup.sh``` to create the jar file if not present
3. Run  ```python umlparser <input folder or zip file> <output folder or file>```
	eg. ```python umlparser testData/uml-parser-test-4 .```
	eg. ```python umlparser testData/uml-sequence-test .```

#### NOTE:
1. All java files to be analysed need to be in the default package
2. In order to generate sequence diagram, 'inputPath' should contain the word 'sequence' (case insensitive). Sequence diagram would start by calling Main.main() from the given Java files.
