# 🧩 PlayWiseDependability

**PlayWiseDependability** è un progetto accademico sviluppato presso l’**Università degli Studi di Salerno**, nell’ambito del corso di **Software Dependability**, con l’obiettivo di applicare in modo concreto i principi di **affidabilità**, **correttezza**, **sicurezza** e **verificabilità del software** alla piattaforma **PlayWise**.

Originariamente concepita come architettura a microservizi, la piattaforma è stata resa monolitica per semplificare la gestione delle pipeline **CI/CD** e migliorare la tracciabilità sperimentale.  
Il progetto integra strumenti di **verifica formale**, **testing strutturato**, **mutation analysis**, **analisi delle performance** e **sicurezza automatizzata**, dimostrando come i principi di *dependability engineering* possano essere applicati in un contesto reale.

---

## 🚀 Obiettivi del Progetto

- Integrare strumenti avanzati di analisi e testing per garantire **correttezza, affidabilità e sicurezza** del software.  
- Automatizzare l’intero ciclo di vita tramite **GitHub Actions** e pipeline **CI/CD** complete.  
- Misurare e ottimizzare **copertura del codice**, **robustezza dei test** e **prestazioni** dei moduli critici.  
- Dimostrare l’applicazione pratica dei concetti di **Software Dependability** in un progetto accademico reale.

---

## 🧠 Architettura

PlayWiseDependability segue un modello **three-tier monolitico** composto da:

- **Frontend:** React + Vite  
- **Backend:** Spring Boot 3 (Java 17)  
- **Database:** MongoDB (Atlas Cluster)

Tutte le componenti sono containerizzate tramite **Docker**, con immagini pubblicate automaticamente su **DockerHub** al termine di ogni build CI/CD.

---

## 🧩 Tecnologie Principali

| Categoria | Strumento | Descrizione |
|------------|------------|-------------|
| Verifica Formale | **OpenJML** | Specifica e verifica di pre/post-condizioni sul modulo genetico `GeneticEngine`. |
| Testing e Copertura | **JUnit 5**, **JaCoCo**, **Codecov** | Testing automatizzato con copertura 100% di linee e rami, analisi dei tempi medi di test. |
| Mutation Analysis | **PITest** | Misura della robustezza della suite di test, con 100% di mutation coverage e test strength. |
| Benchmark | **JMH** | Microbenchmarking dei metodi più onerosi per l’ottimizzazione delle prestazioni. |
| CI/CD | **GitHub Actions** | Pipeline completa: build → test → analisi statica → mutation testing → sicurezza → deploy Docker. |
| Sicurezza | **Snyk**, **GitGuardian**, **SonarCloud** | Analisi automatica di vulnerabilità, segreti esposti e code smell. |
| Containerizzazione | **Docker / DockerHub** | Distribuzione e ripetibilità dell’ambiente di esecuzione. |

---

## ⚙️ Pipeline CI/CD

Ogni commit su `main` attiva automaticamente una pipeline composta da:

1. **Checkout e Setup**
   - Configurazione ambiente Java 17 e Node 20  
   - Installazione dipendenze per backend e frontend  
2. **Build**
   - Compilazione moduli Spring Boot e React  
   - Creazione artefatti Maven  
3. **Testing e Coverage**
   - Esecuzione test JUnit 5  
   - Calcolo copertura con JaCoCo  
4. **Static Analysis e Mutation Testing**
   - Analisi del codice con SonarCloud  
   - Mutation testing con PITest (threshold ≥ 95%)  
5. **Security Scanning**
   - Scansione dipendenze con Snyk  
   - Rilevamento segreti con GitGuardian  
6. **Containerizzazione e Deploy**
   - Build immagine Docker backend  
   - Push automatico su DockerHub  
7. **Artifact Publishing**
   - Archiviazione dei report (JaCoCo, PITest, SonarCloud, Snyk)

---

## 🧭 Avvio e Riproducibilità del Progetto

Tutti i componenti possono essere avviati e testati localmente replicando le stesse fasi della pipeline CI/CD.

---

### 🖥️ Avvio del Frontend


cd Frontend/playwise-frontend
npm install
npm run dev

L’applicazione sarà disponibile su
👉 http://localhost:5173

### ☕ Avvio del Backend
cd Backend/games-project
mvn compile
mvn clean install


Il backend Spring Boot sarà eseguito su http://localhost:8081

(la porta è configurabile in application.properties).

### ⚙️ Configurazione Locale del Backend

Per eseguire il backend in locale, crea un file di configurazione:

Backend/games-project/src/main/resources/application.properties


e inserisci i seguenti parametri:

spring.application.name=games-project
server.port=8081

# MongoDB Configuration
spring.data.mongodb.uri=YOUR_MONGODB_CONNECTION_STRING
spring.data.mongodb.database=test

# Disable JPA autoconfiguration (since we use Mongo)
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration

# General
spring.main.allow-bean-definition-overriding=true
spring.jpa.enabled=false


🔒 Nota: sostituisci YOUR_MONGODB_CONNECTION_STRING con l’URI del tuo cluster MongoDB (es. da MongoDB Atlas).
Il progetto utilizza Spring Data MongoDB, quindi non è necessario alcun database relazionale o configurazione JPA.

Dopo aver creato il file, avvia il backend con:

cd Backend/games-project
mvn spring-boot:run


Il servizio sarà disponibile su
👉 http://localhost:8081

🧪 Esecuzione dei Test
✅ Test unitari e di integrazione
mvn clean test


📂 Report JaCoCo:
Backend/games-project/target/site/jacoco/index.html

🧬 Mutation Testing
mvn org.pitest:pitest-maven:mutationCoverage -f pom.xml


📂 Report PITest:
Backend/games-project/target/pit-reports/index.html

Tutti questi passaggi vengono eseguiti automaticamente nella pipeline CI/CD insieme alle analisi SonarCloud, Snyk e GitGuardian.

🧩 Verifica Formale con OpenJML
1️⃣ Verifica simbolica (ESC)
openjml -esc -timeout=10 \
-classpath "$(mvn -q dependency:build-classpath \
-Dmdep.outputAbsoluteArtifactFilename=true \
-Dmdep.outputFile=/dev/stdout -DincludeScope=compile):target/classes" \
-sourcepath src/main/java \
src/main/java/com/games/games_project/geneticalgorithm/*.java

2️⃣ Strumentazione per RAC (Runtime Assertion Checking)
openjml -rac -noInternalSpecs -quiet \
-classpath "$(mvn -q dependency:build-classpath \
-Dmdep.outputAbsoluteArtifactFilename=true \
-Dmdep.outputFile=/dev/stdout -DincludeScope=compile):target/classes" \
-sourcepath src/main/java \
-d target/jml-instrumented \
src/main/java/com/games/games_project/geneticalgorithm/*.java

3️⃣ Esecuzione con runtime JML
java -cp "/usr/local/openjml-0.17/jmlruntime.jar:target/jml-instrumented:target/classes:$(mvn -q dependency:build-classpath \
-Dmdep.outputAbsoluteArtifactFilename=true \
-Dmdep.outputFile=/dev/stdout -DincludeScope=compile)" \
com.games.games_project.geneticalgorithm.PegiMain

⚙️ Analisi delle Prestazioni (JMH)
cd Backend/games-project
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt -q
java -cp "target/classes:$(cat cp.txt)" org.openjdk.jmh.Main \
-rf csv -rff target/jmh-results.csv -f 1


### 📂 Risultati:
Backend/games-project/target/jmh-results.csv

### 🐳 Deploy con Docker

Ogni build CI/CD produce e pubblica automaticamente su DockerHub l’immagine del backend:

docker pull francescotorino/playwise-backend:latest
docker run -p 8081:8081 francescotorino/playwise-backend


L’immagine include tutte le dipendenze del backend e garantisce un ambiente esecutivo replicabile, isolato e coerente con la pipeline.

🔒 Sicurezza e Qualità del Codice
Strumento	Obiettivo	Risultato
Snyk	Rilevazione e correzione vulnerabilità (XSS, CSRF, librerie obsolete)	✅ Vulnerabilità corrette
GitGuardian	Rimozione di segreti e credenziali nei commit	✅ Nessun secret residuo
SonarCloud	Analisi statica (code smell, duplicazioni, hotspot di sicurezza)	✅ Quality Gate superato
PITest	Robustezza dei test contro mutazioni semantiche	✅ 100% mutation coverage
🧮 Risultati Principali

✅ OpenJML: verifica formale completata (ESC + RAC)

✅ JaCoCo: 100% line e branch coverage

✅ PITest: 100% mutation coverage

✅ Codecov: analisi tempi medi di test

✅ JMH: microbenchmark dei metodi più onerosi

✅ CI/CD: pipeline automatizzata completa

✅ Sicurezza: vulnerabilità e segreti eliminati

🔄 Pipeline Automatizzate (CI/CD)

Ogni commit su main attiva automaticamente:

Build & Test → Maven + JUnit + JaCoCo

Mutation Testing → PITest

Analisi Statica & Sicurezza → SonarCloud, Snyk, GitGuardian

Containerizzazione → Docker

Deploy Automatico → Push su DockerHub

Report Publishing → JaCoCo, PITest, JMH, SonarCloud, Snyk