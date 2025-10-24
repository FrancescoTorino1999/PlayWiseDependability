#!/bin/bash

echo "🧹 Pulizia ambiente Docker in corso..."

# Ferma e rimuove tutti i container
docker compose down -v --remove-orphans
docker stop $(docker ps -aq) 2>/dev/null
docker rm -f $(docker ps -aq) 2>/dev/null

# Rimuove tutte le immagini locali
docker rmi -f $(docker images -q) 2>/dev/null

# Rimuove eventuali volumi e reti non utilizzati
docker system prune -af --volumes

echo "✅ Ambiente pulito!"

echo "🏗️ Costruzione delle immagini..."
docker compose build

echo "🚀 Push delle immagini su Docker Hub..."
docker compose push

echo "⬇️ Pull delle ultime versioni da Docker Hub..."
docker pull lvz1999/games-service:latest
docker pull lvz1999/games-frontend:latest

echo "🔧 Avvio dei container..."
docker compose up -d

echo "✅ Tutto pronto!"
docker ps
