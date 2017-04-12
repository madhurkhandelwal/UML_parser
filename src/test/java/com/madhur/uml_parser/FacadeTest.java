package com.madhur.uml_parser;

import junit.framework.*;

public class FacadeTest extends TestCase{
	protected void setUp(){
		//TODO
	}

	public void testMain(){
		//TODO
	}

	public void testGenClassDiagram(){
		Facade.genClassDiagram("/Users/mak/Dropbox/SJSU/spring17/202/project/UML_parser/testData/uml-parser-test-1",
				"/Users/mak/Downloads/temp/diagrams",
                "1.png");
		System.out.println("--------------------------------------------------------------");
		Facade.genClassDiagram("/Users/mak/Dropbox/SJSU/spring17/202/project/UML_parser/testData/uml-parser-test-2",
				"/Users/mak/Downloads/temp/diagrams",
                "2.png");
        System.out.println("--------------------------------------------------------------");
		Facade.genClassDiagram("/Users/mak/Dropbox/SJSU/spring17/202/project/UML_parser/testData/uml-parser-test-3",
				"/Users/mak/Downloads/temp/diagrams",
                "3.png");
        System.out.println("--------------------------------------------------------------");
		Facade.genClassDiagram("/Users/mak/Dropbox/SJSU/spring17/202/project/UML_parser/testData/uml-parser-test-4",
				"/Users/mak/Downloads/temp/diagrams",
                "4.png");
		System.out.println("--------------------------------------------------------------");
        Facade.genClassDiagram("/Users/mak/Dropbox/SJSU/spring17/202/project/UML_parser/testData/uml-parser-test-5",
                "/Users/mak/Downloads/temp/diagrams",
                "5.png");
	}
}
