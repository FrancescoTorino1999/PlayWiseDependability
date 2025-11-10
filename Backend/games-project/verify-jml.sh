openjml -esc -timeout=10 \
-classpath "$(mvn -q dependency:build-classpath -Dmdep.outputAbsoluteArtifactFilename=true \
-Dmdep.outputFile=/dev/stdout -DincludeScope=compile):target/classes" \
-sourcepath src/main/java \
src/main/java/com/games/games_project/geneticalgorithm/*.java


mvn compile

openjml -rac -noInternalSpecs -quiet \
-classpath "$(mvn -q dependency:build-classpath -Dmdep.outputAbsoluteArtifactFilename=true \
-Dmdep.outputFile=/dev/stdout -DincludeScope=compile):target/classes" \
-sourcepath src/main/java \
-d target/jml-instrumented \
src/main/java/com/games/games_project/geneticalgorithm/*.java


java -cp "/usr/local/openjml-0.17/jmlruntime.jar:target/jml-instrumented:target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputAbsoluteArtifactFilename=true \
-Dmdep.outputFile=/dev/stdout -DincludeScope=compile)" \
com.games.games_project.geneticalgorithm.PegiMain




