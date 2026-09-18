#!/bin/bash

# Detener el script si ocurre algún error
set -e

# Ejecutar script de inicialización
./init.sh

# Construir imágenes mostrando el progreso detallado en tiempo real
if [ "$1" = "--no-cache" ]; then
    echo "Construyendo imágenes de Docker sin caché (modo detallado)..."
    docker compose --progress=plain -f docker-compose.prod.yml build --no-cache
else
    echo "Construyendo imágenes de Docker (modo detallado)..."
    docker compose --progress=plain -f docker-compose.prod.yml build
fi

# Levantar servicios
echo "Levantando contenedores..."
docker compose -f docker-compose.prod.yml up -d

echo "¡Despliegue completado con éxito!"
