# PlayWiseDependability

🐳 Guida rapida — Avvio del progetto

Questa guida spiega come scaricare le immagini Docker già pubblicate su Docker Hub e avviare l’ambiente usando il file docker-compose.yml già presente nel repository.

⚙️ Prerequisiti

Assicurati di avere installato:

Docker
👉 Scarica Docker Desktop

Docker Compose (incluso di default in Docker Desktop)

Accesso a Internet

📦 1️⃣ Pull delle immagini

Dal terminale, nella root del progetto (dove si trova docker-compose.yml):

docker pull lvz1999/games-service:latest
docker pull lvz1999/games-frontend:latest


Puoi verificare che siano state scaricate correttamente con:

docker images

🚀 2️⃣ Avvio del progetto

Una volta scaricate le immagini, puoi avviare tutto con:

docker compose up -d


📌 Il flag -d (detached) avvia i container in background.
Puoi anche avviare in foreground per vedere i log in tempo reale:

docker compose up

🔍 3️⃣ Verifica dei container attivi

Per verificare che i servizi siano in esecuzione:

docker ps


Dovresti vedere qualcosa come:

CONTAINER ID   IMAGE                          PORTS
xxxxxx         lvz1999/games-frontend:latest  0.0.0.0:5173->5173/tcp
xxxxxx         lvz1999/games-service:latest   0.0.0.0:8081->8081/tcp

🌐 4️⃣ Accesso ai servizi

Una volta avviati:

Frontend: http://localhost:5173

Backend (API): http://localhost:8081
