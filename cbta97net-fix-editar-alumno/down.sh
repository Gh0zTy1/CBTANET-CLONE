# para todos los contenedores, redes y volumenes 
docker compose -f docker-compose.prod.yml down
echo -e "\e[32m[*] Contenedores detenidos \e[0m"

# delete volumes
docker volume rm cbta97net-prod-v1_cbta97-db-data
docker volume rm cbta97net-prod-v1_keycloak-db-data
echo -e "\e[32m[*] Volúmenes eliminados \e[0m"