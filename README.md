# UML Parser
Generate UML Java class/sequence diagram

### Requirments:
1. Java 1.8 or later
2. Apache Maven 3.3.9 or later
3. Python 2.7

### Steps:
1. Clone this repo
2. Run  ```./umlparser <input folder or zip file> <output folder or file>```

#### NOTE:
1. All java files to be analysed need to be in the default package
2. In order to generate sequence diagram, 'inputPath' should contain the word 'sequence' (case insensitive). Sequence diagram would start by calling Main.main() from the given Java files.
