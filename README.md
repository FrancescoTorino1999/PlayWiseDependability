# 🧩 PlayWiseDependability

**PlayWiseDependability** è un progetto accademico sviluppato presso l’Università degli Studi di Salerno, nell’ambito del corso di **Software Dependability**, con l’obiettivo di applicare in modo concreto i principi di affidabilità, correttezza, sicurezza e verificabilità del software alla piattaforma **PlayWise**.

Originariamente concepita come architettura a microservizi, la piattaforma è stata resa monolitica per semplificare la gestione delle pipeline CI/CD e migliorare la tracciabilità sperimentale.  
Il progetto integra strumenti di **verifica formale**, **testing strutturato**, **mutation analysis**, **analisi delle performance** e **sicurezza automatizzata**, dimostrando come i principi di *dependability engineering* possano essere applicati in un contesto reale.

---

## 🚀 Obiettivi del Progetto

- Integrare strumenti avanzati di analisi e testing per garantire **correttezza, affidabilità e sicurezza** del software.  
- Automatizzare l’intero ciclo di vita tramite **GitHub Actions** e pipeline CI/CD complete.  
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

Ogni commit su `main` attiva una pipeline automatizzata che esegue i seguenti step:

1. **Checkout e Setup**  
   - Configurazione ambiente Java 17 e Node 20  
   - Installazione delle dipendenze per backend e frontend  
2. **Build**  
   - Compilazione dei moduli Spring Boot e React  
   - Creazione degli artefatti Maven  
3. **Testing e Coverage**  
   - Esecuzione test unitari e di integrazione con JUnit 5  
   - Calcolo della copertura con JaCoCo (report XML/HTML)  
4. **Static Analysis e Mutation Testing**  
   - Analisi del codice con SonarCloud  
   - Mutation testing con PITest (threshold ≥ 95%)  
5. **Security Scanning**  
   - Scansione delle dipendenze con Snyk  
   - Rilevamento segreti con GitGuardian  
6. **Containerizzazione e Deploy**  
   - Build dell’immagine Docker del backend  
   - Push automatico su DockerHub  
7. **Artifact Publishing**  
   - Archiviazione dei report (JaCoCo, PITest, SonarCloud, Snyk)

---

## 🧮 Risultati Principali

- ✅ **OpenJML:** verifica formale completata sul modulo genetico `GeneticEngine` (ESC + RAC).  
- ✅ **JaCoCo:** 100% di line coverage e branch coverage sui package principali.  
- ✅ **PITest:** 100% di mutation coverage e test strength.  
- ✅ **Codecov:** analisi dei tempi medi di test e identificazione dei colli di bottiglia prestazionali.  
- ✅ **JMH:** microbenchmark sui metodi a maggiore complessità computazionale.  
- ✅ **CI/CD:** pipeline GitHub Actions con build automatizzato, test, analisi statica e deploy Docker.  
- ✅ **Sicurezza:** vulnerabilità XSS, CSRF e credential exposure rilevate e corrette tramite Snyk e GitGuardian.  

---

## 🔒 Sicurezza e Qualità del Codice

- **Snyk:** rilevazione e correzione di vulnerabilità (XSS, CSRF, password in chiaro, librerie obsolete).  
- **GitGuardian:** eliminazione di segreti e credenziali nei commit.  
- **SonarCloud:** rimozione di code smell, duplicazioni e hotspot di sicurezza.  
- **PITest:** validazione della robustezza dei test contro mutazioni semantiche.  

L’intera pipeline è bloccante: eventuali vulnerabilità, mutazioni non uccise o metriche di qualità inferiori alla soglia interrompono automaticamente la build.

---

## 🐳 Deploy e Containerizzazione

Al termine di ogni esecuzione CI/CD, viene generata un’immagine Docker del backend e pubblicata su **DockerHub**, garantendo:
- ambiente di esecuzione replicabile;
- isolamento delle dipendenze;
- coerenza tra sviluppo, test e distribuzione.

```bash
docker pull francescotorino/playwise-backend:latest
docker run -p 8080:8080 playwise-backend
