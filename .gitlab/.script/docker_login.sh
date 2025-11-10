#!/bin/bash

# --- Prüfen, ob Argumente übergeben wurden ---
if [ $# -lt 3 ]; then
  echo "Usage: $0 <docker-registry> <user> <password>"
  exit 1
fi

DOCKER_REGISTRY="$1"
USER="$2"
PASSWORD="$3"

# --- Docker Login an docker registry ---
echo "🔑 Docker Login mit User $USER ..."

echo "${PASSWORD}" | docker login "${DOCKER_REGISTRY}" -u "${USER}" --password-stdin

if [ $? -eq 0 ]; then
  echo "✅ Docker Login war erfolgreich."
else
  echo "❌ Fehler beim Docker Login."
  exit 2
fi
