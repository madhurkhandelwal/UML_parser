rm UML_parser-*-with-dependencies.jar
mvn -e clean compile assembly:single && cp target/UML_parser-*-jar-with-dependencies.jar .