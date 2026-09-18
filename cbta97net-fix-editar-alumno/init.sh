#!/bin/bash

# Asegurar que el script se detenga si hay algún error crítico
set -e

echo "==================================================="
echo "  Configurador Automatizado para MariaDB (CBTa97)"
echo "==================================================="
echo ""
echo -e "\e[32m[1/4] Creando directorios\e[0m"

if [ ! -d "mariadb-config/init-scripts" ]; then
    mkdir -p "mariadb-config/init-scripts"
    echo "  - Carpetas creadas correctamente"
else
    echo "  - Las carpetas ya existían"
fi

# 2. Crear el archivo de llaves criptográficas para TDE
echo -e "\e[32m[2/4] Generando archivo de llaves (keys.txt)...\e[0m"
cat << 'EOF' > mariadb-config/keys.txt
1;1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef
EOF
echo "  - Archivo keys.txt generado."

# 3. Crear el archivo de configuración del motor de base de datos
echo -e "\e[32m[3/4] Generando archivo de configuracion TDE (tde.cnf)...\e[0m"
cat << 'EOF' > mariadb-config/tde.cnf
[mariadb]
plugin_load_add = file_key_management
file_key_management_filename = /etc/mysql/encryption/keys.txt
file_key_management_encryption_algorithm = AES_CTR
innodb_encrypt_tables = ON
innodb_encrypt_log = ON
innodb_encryption_threads = 4
EOF
echo "  - Archivo tde.cnf generado."

# 4. Crear el script SQL para otorgar permisos a cbta97_api_user
echo -e "\e[32m[4/4] Generando script SQL de permisos (01-permissions.sql)...\e[0m"
cat << 'EOF' > mariadb-config/init-scripts/01-permissions.sql
-- Otorgar todos los permisos a cbta97_api_user sobre la base de datos cbta97net
GRANT ALL PRIVILEGES ON cbta97net.* TO 'cbta97_api_user'@'%';

-- Aplicar cambios de inmediato
FLUSH PRIVILEGES;
EOF
echo "  - Archivo 01-permissions.sql generado"

echo ""
echo "==================================================="
echo " ¡Listo! Todo se ha configurado con exito."
echo "==================================================="
echo ""