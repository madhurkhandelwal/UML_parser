#!/usr/bin/env python

import shlex
import subprocess
import sys
import glob
import os
import re
import shutil

paths = glob.glob("UML_parser-*-with-dependencies.jar")
UML_PARSER_JAR = os.environ.get('UML_PARSER', None)
if UML_PARSER_JAR is None:
    paths = glob.glob("UML_parser-*-with-dependencies.jar")
    if paths:
        UML_PARSER_JAR = paths[0]
    else:
        print "Could not locate UML parser jar"
        print "You may try running 'bash setup.sh'"
        exit(-1)


def parseArguments():
    # TODO validate input arguments
    return sys.argv[-2], sys.argv[-1]


def unzip(inPath):
    import zipfile
    zip_ref = zipfile.ZipFile(inPath, 'r')

    folderName = os.path.basename(inPath).split('.')[0]
    outputFolder = os.path.join(os.path.dirname(inPath), folderName)

    print "Unziping at %s..." % outputFolder
    zip_ref.extractall(outputFolder)
    zip_ref.close()
    return outputFolder


def launchClassDiagramGenerator(inPath, outPath):
    if os.path.isdir(outPath):
        outputFileName = "classDiagram.png"
        outputFolder = outPath
    else:
        outputFolder = os.path.dirname(outPath)
        outputFileName = os.path.basename(outPath)
    subprocess.call(shlex.split(("java -jar %s -c -i %s -o %s -n %s") %
                                (UML_PARSER_JAR, inPath, outputFolder,
                                 outputFileName)))
    print "Output file: %s" % (os.path.join(outputFolder, outputFileName))


def launchSequenceDiagramGenerator(inPath, outPath):
    outputFile = compileAndRunAspectJ(inPath, outPath)
    print "Output file: %s" % (outputFile)


def compileAndRunAspectJ(inPath, outPath):
    javaFiles = glob.glob(os.path.join(inPath, "*.java"))
    for javaFile in javaFiles:
        shutil.copy(javaFile,
                    os.path.join(os.path.dirname(os.path.abspath(__file__)),
                                 "SequenceDiagramGenerator",
                                 "src",
                                 "main",
                                 "java"))
    # TODO just compile with mvn and then run it using java
    # currently there is a bug in my SequenceDiagramGenerator.aj code
    # which does stupid things if we directly call Main class from jvm
    # instead of jvm -> junit -> test class -> Main.main()
    retval = subprocess.Popen("mvn clean test",
                              cwd=os.path.join(os.path.dirname(
                                               os.path.abspath(__file__)),
                                               "SequenceDiagramGenerator"),
                              shell=True).wait()
    if retval != 0:
        print "Error compiling!"
        exit(retval)
    for javaFile in javaFiles:
        os.remove(os.path.join(os.path.dirname(os.path.abspath(__file__)),
                               "SequenceDiagramGenerator",
                               "src",
                               "main",
                               "java",
                               os.path.basename(javaFile)))
    outputFileName = "sequenceDiagram.png"

    outputFile = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                              "SequenceDiagramGenerator",
                              "target",
                              outputFileName)
    shutil.copy(outputFile, outPath)
    if os.path.isdir(outPath):
        return os.path.join(outPath, outputFileName)
    else:
        return outPath


if __name__ == '__main__':
    inPath, outPath = parseArguments()
    inPath = os.path.abspath(inPath)
    outPath = os.path.abspath(outPath)

    # Unzip if a zip file is given
    if re.match('.*\.zip', inPath):
        inPath = unzip(inPath)

    # As per the requirements, if sequence is mentioned in the input file,
    #  generate sequence diagram else class diagram
    if re.match('.*sequence.*', inPath, re.I):
        launchSequenceDiagramGenerator(inPath, outPath)
    else:
        launchClassDiagramGenerator(inPath, outPath)
