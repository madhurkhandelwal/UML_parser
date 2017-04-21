#!/bin/bash
./setup.sh
rm testOuput/*
./umlparser testData/uml-parser-test-1 testOuput/uml-parser-test-1.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser testData/uml-parser-test-2 testOuput/uml-parser-test-2.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser testData/uml-parser-test-3 testOuput/uml-parser-test-3.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser testData/uml-parser-test-4 testOuput/uml-parser-test-4.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser testData/uml-parser-test-5 testOuput/uml-parser-test-5.png
echo "--------------------------------------------------------------------------------"
echo "--------------------------------------------------------------------------------"
./umlparser testData/uml-sequence-test testOuput/uml-sequence-test.png
rm UML_parser-*-with-dependencies.jar