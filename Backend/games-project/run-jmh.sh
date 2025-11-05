#!/bin/zsh
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt -q
java -cp "target/classes:$(cat cp.txt)" org.openjdk.jmh.Main -rf csv -rff target/jmh-results.csv -f 1
