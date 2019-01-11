#!/bin/bash
set MAVEN_OPTS="-Xmx1488m"
# mvn exec:java -Pstart
# mvn package
mvn jetty:run 
