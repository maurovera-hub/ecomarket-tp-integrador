-- Inicializacion del usuario de la aplicacion EcoMarket.
-- El entrypoint del contenedor ejecuta este script una unica vez, cuando el
-- volumen de datos esta vacio (es decir, en un clon limpio del repo).
--
-- Creamos el usuario tanto para '%' como para 'localhost' porque, al publicar
-- el puerto, Docker Desktop puede presentar la conexion del host como
-- proveniente de 'localhost', y MySQL no la haria coincidir con '@%'.

CREATE USER IF NOT EXISTS 'ecomarket'@'%' IDENTIFIED BY 'ecomarket';
CREATE USER IF NOT EXISTS 'ecomarket'@'localhost' IDENTIFIED BY 'ecomarket';

GRANT ALL PRIVILEGES ON *.* TO 'ecomarket'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'ecomarket'@'localhost';

FLUSH PRIVILEGES;
