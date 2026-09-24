#!/bin/sh
set -eu
mkdir -p respaldos
docker compose exec -T postgres pg_dump -U controlescolar controlescolar > respaldos/postgres.sql
docker compose exec -T mongo mongodump --db controlescolar --archive > respaldos/mongo.archive
echo "Respaldo en respaldos/"
