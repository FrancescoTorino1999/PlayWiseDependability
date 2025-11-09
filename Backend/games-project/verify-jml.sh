openjml -esc -classpath "$(mvn -q dependency:build-classpath -Dmdep.outputAbsoluteArtifactFilename=true -Dmdep.outputFile=/dev/stdout -DincludeScope=compile):target/classes" \
-sourcepath src/main/java \
src/main/java/com/games/games_project/geneticalgorithm/*.java


openjml -rac -classpath "$(mvn -q dependency:build-classpath -Dmdep.outputAbsoluteArtifactFilename=true -Dmdep.outputFile=/dev/stdout -DincludeScope=compile):target/classes" \
-sourcepath src/main/java \
src/main/java/com/games/games_project/geneticalgorithm/*.java
