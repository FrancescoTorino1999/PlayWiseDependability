#!/bin/bash
set -e

echo "---- $(date) ----"
echo "Preparazione ambiente JML..."

# Se siamo dentro src/jml, risali di due livelli per tornare alla root del progetto
if [[ "$(basename $(pwd))" == "jml" ]]; then
  PROJECT_ROOT="$(dirname $(dirname $(pwd)))"
else
  PROJECT_ROOT="$(pwd)"
fi

SRC_BASE="$PROJECT_ROOT/src/main/java"
JML_BASE="$PROJECT_ROOT/src/jml"

SERVICE_SRC="$SRC_BASE/com/games/games_project/service/impl/PegiClassifierServiceImpl.java"
CONTROLLER_SRC="$SRC_BASE/com/games/games_project/controller/PegiClassifierController.java"

SERVICE_JML="$JML_BASE/com/games/games_project/service/impl/PegiClassifierServiceImpl_JML.java"
CONTROLLER_JML="$JML_BASE/com/games/games_project/controller/PegiClassifierController_JML.java"

mkdir -p "$(dirname "$SERVICE_JML")"
mkdir -p "$(dirname "$CONTROLLER_JML")"

echo "Copio e pulisco i file Java in versione JML..."

# Rimuove le annotazioni Spring
if [ -f "$SERVICE_SRC" ]; then
  sed '/^@Service/d;/^@RestController/d;/^@RequestMapping/d;/^@Autowired/d;/^@PostMapping/d;/^@GetMapping/d;/^@RequestBody/d;/^@PathVariable/d' "$SERVICE_SRC" > "$SERVICE_JML"
else
  echo "❌ File non trovato: $SERVICE_SRC"
  exit 1
fi

if [ -f "$CONTROLLER_SRC" ]; then
  sed '/^@Service/d;/^@RestController/d;/^@RequestMapping/d;/^@Autowired/d;/^@PostMapping/d;/^@GetMapping/d;/^@RequestBody/d;/^@PathVariable/d' "$CONTROLLER_SRC" > "$CONTROLLER_JML"
else
  echo "❌ File non trovato: $CONTROLLER_SRC"
  exit 1
fi

echo "Costruisco classpath Maven..."
CP="$(mvn -q dependency:build-classpath -Dmdep.outputAbsoluteArtifactFilename=true -Dmdep.outputFile=/dev/stdout -DincludeScope=compile)"
CP="$CP:target/classes"

echo "Avvio verifica OpenJML ESC..."
cd "$JML_BASE"
openjml -esc -classpath "$CP" -sourcepath . \
  com/games/games_project/service/impl/PegiClassifierServiceImpl_JML.java \
  com/games/games_project/controller/PegiClassifierController_JML.java

echo "Verifica ESC completata."
echo "Compilo con OpenJML RAC..."
openjml -rac -classpath "$CP" -sourcepath . \
  com/games/games_project/service/impl/PegiClassifierServiceImpl_JML.java \
  com/games/games_project/controller/PegiClassifierController_JML.java

echo "Verifica JML completata con successo."
cd "$PROJECT_ROOT"
