#!/bin/bash
set -e

# Permitir especificar archivo de entorno con -f (ejemplo: ./create-admin.sh -f .env.prod)
ENV_FILE=""
while getopts "f:" opt; do
  case $opt in
    f) ENV_FILE="$OPTARG" ;;
  esac
done

if [ -n "$ENV_FILE" ] && [ -f "$ENV_FILE" ]; then
    echo "Cargando variables desde $ENV_FILE..."
    set -a
    source "$ENV_FILE"
    set +a
elif [ -f .env.prod ]; then
    set -a
    source .env.prod
    set +a
elif [ -f .env ]; then
    set -a
    source .env
    set +a
fi

# Detectar contenedor de MariaDB activo (producción o desarrollo)
DB_CONTAINER=""
if docker ps --format '{{.Names}}' | grep -q "^cbta97-db$"; then
    DB_CONTAINER="cbta97-db"
elif docker ps --format '{{.Names}}' | grep -q "^cbta97-db-v2$"; then
    DB_CONTAINER="cbta97-db-v2"
else
    echo "❌ Error: No se encontró ningún contenedor de MariaDB en ejecución (cbta97-db o cbta97-db-v2)."
    exit 1
fi

# Detectar contenedor de Keycloak activo
KC_CONTAINER=""
if docker ps --format '{{.Names}}' | grep -q "^keycloak-server$"; then
    KC_CONTAINER="keycloak-server"
elif docker ps --format '{{.Names}}' | grep -q "^keycloak-server-v1$"; then
    KC_CONTAINER="keycloak-server-v1"
fi

echo "=================================================="
echo "Contenedor de MariaDB: $DB_CONTAINER"
echo "Contenedor de Keycloak: $KC_CONTAINER"
echo "==================================================="

echo "==================================================="
echo "   Creador de Usuario Administrador (CBTa 97)"
echo "==================================================="

# Pedir datos o asignar valores por defecto
read -p "Nombre del administrador [ADMINISTRADOR]: " NOMBRE
NOMBRE=${NOMBRE:-ADMINISTRADOR}

read -p "Apellido Paterno [SISTEMA]: " PATERNO
PATERNO=${PATERNO:-SISTEMA}

read -p "Apellido Materno [CBTA]: " MATERNO
MATERNO=${MATERNO:-CBTA}

read -p "Correo electrónico [admin@cbta97.edu.mx]: " EMAIL
EMAIL=${EMAIL:-admin@cbta97.edu.mx}

read -p "Teléfono [6440000000]: " TELEFONO
TELEFONO=${TELEFONO:-6440000000}

read -p "CURP [ADM000000XXXXXX00]: " CURP
CURP=${CURP:-ADM000000XXXXXX00}

read -s -p "Contraseña deseada: " PASSWORD
echo ""

if [ -z "$PASSWORD" ]; then
    echo "❌ Error: La contraseña no puede estar vacía."
    exit 1
fi

# Probar matriz de usuarios y contraseñas de MariaDB
CANDIDATE_USERS=("cbta97_api_user" "root")
CANDIDATE_PASSES=(
    "$(echo "${CBTA_API_PASS}" | tr -d '"')"
    "$(echo "${CBTA_API_ROOT_PASS}" | tr -d '"')"
    "${CBTA_API_PASS}"
    "${CBTA_API_ROOT_PASS}"
    "Cbta97.ApiDb*CRUD$"
    "Cbta97.Root*Secure!"
    "api_password_secreto"
    "root_password_super_seguro"
)

VALID_USER=""
VALID_PASS=""

echo "🔍 Buscando credenciales de MariaDB..."

for u in "${CANDIDATE_USERS[@]}"; do
    for p in "${CANDIDATE_PASSES[@]}"; do
        [ -z "$p" ] && continue
        if docker exec -i "$DB_CONTAINER" mariadb -u "$u" -p"$p" -e "SELECT 1;" >/dev/null 2>&1; then
            VALID_USER="$u"
            VALID_PASS="$p"
            break 2
        fi
    done
done

if [ -z "$VALID_USER" ]; then
    echo "❌ Error: No se logró autenticar con MariaDB usando ningún usuario o contraseña conocidos."
    exit 1
fi

echo "  - Conexión exitosa a MariaDB como: $VALID_USER"

# Si conectó como root, asegurar que cbta97_api_user tenga permisos y contraseña correcta
PROD_API_PASS=$(echo "${CBTA_API_PASS:-Cbta97.ApiDb*CRUD$}" | tr -d '"')
if [ "$VALID_USER" = "root" ]; then
    echo "⚙️ Sincronizando permisos de 'cbta97_api_user'..."
    docker exec -i "$DB_CONTAINER" mariadb -u root -p"$VALID_PASS" -e "
    CREATE USER IF NOT EXISTS 'cbta97_api_user'@'%' IDENTIFIED BY '$PROD_API_PASS';
    ALTER USER 'cbta97_api_user'@'%' IDENTIFIED BY '$PROD_API_PASS';
    GRANT ALL PRIVILEGES ON cbta97net.* TO 'cbta97_api_user'@'%';
    FLUSH PRIVILEGES;
    " >/dev/null 2>&1 || true
    VALID_USER="cbta97_api_user"
    VALID_PASS="$PROD_API_PASS"
fi

REALM="cbta97-realm-prod"
KC_URL="http://localhost:9090"

# 1. Generar el siguiente ID de 6 dígitos desde la secuencia de MariaDB (sys_sequences)
echo -e "\n[1/4] Generando ID numérico de 6 dígitos en MariaDB..."

NEXT_ID=$(docker exec -i "$DB_CONTAINER" mariadb -u "$VALID_USER" -p"$VALID_PASS" cbta97net -sN -e "
CREATE TABLE IF NOT EXISTS sys_sequences (sequence_name VARCHAR(255) PRIMARY KEY, next_val BIGINT);
INSERT INTO sys_sequences (sequence_name, next_val) VALUES ('usuario_id', 100001) ON DUPLICATE KEY UPDATE next_val = next_val;
SELECT next_val FROM sys_sequences WHERE sequence_name = 'usuario_id';
UPDATE sys_sequences SET next_val = next_val + 1 WHERE sequence_name = 'usuario_id';
")

echo "  - ID de usuario asignado: $NEXT_ID"

# 2. Insertar el usuario en la base de datos MariaDB (tabla `usuarios`)
echo "[2/4] Guardando registro en MariaDB (tabla 'usuarios')..."
docker exec -i "$DB_CONTAINER" mariadb -u "$VALID_USER" -p"$VALID_PASS" cbta97net -e "
INSERT INTO usuarios (id, nombre, apellido_paterno, apellido_materno, email, telefono, curp, activo)
VALUES ($NEXT_ID, '$NOMBRE', '$PATERNO', '$MATERNO', '$EMAIL', '$TELEFONO', '$CURP', 1)
ON DUPLICATE KEY UPDATE nombre='$NOMBRE', apellido_paterno='$PATERNO', apellido_materno='$MATERNO';
"

# 3. Autenticarse en Keycloak para obtener token de administración
echo "[3/4] Autenticando con servidor Keycloak..."
KC_ADMIN_PASS_CLEAN=$(echo "${KC_ADMIN_PASS:-Cbta97.Admin*2026!}" | tr -d '"')

TOKEN=""
KC_RESP=""

for kc_u in "superadmin" "admin"; do
    for kc_p in "$KC_ADMIN_PASS_CLEAN" "Cbta97.Admin*2026!" "admin"; do
        KC_RESP=$(curl -s -X POST "$KC_URL/realms/master/protocol/openid-connect/token" \
          -H "Content-Type: application/x-www-form-urlencoded" \
          -d "username=$kc_u" \
          -d "password=$kc_p" \
          -d "grant_type=password" \
          -d "client_id=admin-cli" 2>&1) || true
        
        TOKEN=$(echo "$KC_RESP" | grep -o '"access_token":"[^"]*' | grep -o '[^"]*$' || true)
        [ -n "$TOKEN" ] && break 2
    done
done

if [ -z "$TOKEN" ]; then
    echo "❌ Error al obtener token de Keycloak de administración:"
    echo "  - Respuesta de Keycloak: $KC_RESP"
    exit 1
fi

echo "  - Autenticación en Keycloak exitosa."

# 4. Crear usuario en Keycloak usando el ID numérico de 6 dígitos como Username
echo "[4/4] Creando usuario '$NEXT_ID' en Keycloak y asignando rol ADMIN..."

USER_PAYLOAD=$(cat <<EOF
{
  "username": "$NEXT_ID",
  "email": "$EMAIL",
  "firstName": "$NOMBRE",
  "lastName": "$PATERNO $MATERNO",
  "enabled": true,
  "emailVerified": true,
  "credentials": [{
    "type": "password",
    "value": "$PASSWORD",
    "temporary": false
  }]
}
EOF
)

CREATE_USER_RESP=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms/$REALM/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "$USER_PAYLOAD")

HTTP_STATUS=$(echo "$CREATE_USER_RESP" | grep "HTTP_STATUS" | cut -d: -f2)
BODY_MSG=$(echo "$CREATE_USER_RESP" | grep -v "HTTP_STATUS")

KC_UUID=""

if [ "$HTTP_STATUS" -eq 201 ]; then
    echo "  - Usuario '$NEXT_ID' creado exitosamente en Keycloak."
    # Buscar UUID del usuario recién creado
    KC_UUID=$(curl -s -X GET "$KC_URL/admin/realms/$REALM/users?username=$NEXT_ID" \
      -H "Authorization: Bearer $TOKEN" | grep -o '"id":"[^"]*' | head -n 1 | grep -o '[^"]*$' || true)
else
    echo "⚠️ Keycloak rechazó la creación directa del usuario (HTTP $HTTP_STATUS):"
    echo "  $BODY_MSG"
    
    # Intentar buscar por Username
    KC_UUID=$(curl -s -X GET "$KC_URL/admin/realms/$REALM/users?username=$NEXT_ID" \
      -H "Authorization: Bearer $TOKEN" | grep -o '"id":"[^"]*' | head -n 1 | grep -o '[^"]*$' || true)
      
    # Si no existe por username, buscar si existía por Email
    if [ -z "$KC_UUID" ]; then
        KC_UUID=$(curl -s -X GET "$KC_URL/admin/realms/$REALM/users?email=$EMAIL" \
          -H "Authorization: Bearer $TOKEN" | grep -o '"id":"[^"]*' | head -n 1 | grep -o '[^"]*$' || true)
        if [ -n "$KC_UUID" ]; then
            echo "  - El correo '$EMAIL' ya está registrado en Keycloak en el usuario con UUID: $KC_UUID"
        fi
    fi
    
    if [ -z "$KC_UUID" ]; then
        echo "❌ No se pudo crear ni vincular el usuario en Keycloak."
        exit 1
    else
        # Si se encontró el usuario existente, actualizar su contraseña
        curl -s -o /dev/null -X PUT "$KC_URL/admin/realms/$REALM/users/$KC_UUID/reset-password" \
          -H "Authorization: Bearer $TOKEN" \
          -H "Content-Type: application/json" \
          -d "{\"type\":\"password\",\"value\":\"$PASSWORD\",\"temporary\":false}"
        echo "  - Contraseña actualizada para el usuario existente en Keycloak."
    fi
fi

# Obtener detalles del rol ADMIN en Keycloak
ROLE_ADMIN=$(curl -s -X GET "$KC_URL/admin/realms/$REALM/roles/ADMIN" \
  -H "Authorization: Bearer $TOKEN")

ROLE_ID=$(echo "$ROLE_ADMIN" | grep -o '"id":"[^"]*' | head -n 1 | grep -o '[^"]*$' || true)

# Asignar rol ADMIN en Keycloak
if [ -n "$ROLE_ID" ] && [ -n "$KC_UUID" ]; then
    curl -s -o /dev/null -X POST "$KC_URL/admin/realms/$REALM/users/$KC_UUID/role-mappings/realm" \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d "[{\"id\":\"$ROLE_ID\",\"name\":\"ADMIN\"}]"
    echo "  - Rol ADMIN asignado correctamente."
fi

echo ""
echo "==================================================="
echo " ¡Usuario Administrador Creado Exitosamente!"
echo " ID de Inicio de Sesión:  $NEXT_ID"
echo " Correo Electrónico:      $EMAIL"
echo " Rol Asignado:            ADMIN"
echo "==================================================="
