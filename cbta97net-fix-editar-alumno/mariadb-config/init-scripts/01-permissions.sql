-- Otorgar todos los permisos a cbta97_api_user sobre la base de datos cbta97net
GRANT ALL PRIVILEGES ON cbta97net.* TO 'cbta97_api_user'@'%';

-- Aplicar cambios de inmediato
FLUSH PRIVILEGES;
