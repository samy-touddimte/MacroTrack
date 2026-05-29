#!/bin/bash

# Script de démarrage pour MacroTrack
echo "Démarrage de MacroTrack..."

# Vérifier si Docker est installé
if ! command -v docker &> /dev/null; then
    echo "Docker n'est pas installé. Veuillez installer Docker et Docker Compose."
    exit 1
fi

echo "Construction et démarrage des services..."
docker compose up --build -d

echo "Attente du démarrage des services..."
sleep 15

echo "Services démarrés !"
echo ""
echo "Frontend : http://localhost:5173"
echo "Backend API : http://localhost:8080/api"
echo "Swagger UI : http://localhost:8080/swagger-ui.html"
echo "Base de données : localhost:5432"
echo ""
echo "Pour arrêter : ./stop.sh"