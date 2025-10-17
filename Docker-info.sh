docker compose build
docker compose push
docker pull lvz1999/games-service:latest
docker pull lvz1999/games-frontend:latest
docker compose up -d
