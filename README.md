# Inventory Management System (IMS)

Sistema full-stack para gestionar tiendas, catalogo de productos e inventario, con flujo de pedidos y reviews de clientes.

## Caracteristicas
- Alta de tiendas, productos y stock por tienda
- Busqueda y filtro por categoria/nombre
- Gestion de inventario con edicion/eliminacion
- Flujo de pedidos con validacion de stock y datos de cliente
- Reviews de productos por tienda

## Tecnologias
**Backend**
- Java 17, Spring Boot 3
- Spring Web, Data JPA, Validation, Actuator
- MySQL (inventario y pedidos)
- MongoDB (reviews)
- Maven

**Frontend**
- HTML, CSS, JavaScript
- Bootstrap 5, Font Awesome

## Estructura
- `back-end/`: API REST en Spring Boot
- `front-end/`: UI estatica que consume la API
- `insert_data.sql`: datos de ejemplo para MySQL
- `reviews.json`: datos de ejemplo para MongoDB

## Configuracion
Variables de entorno requeridas por el backend (ver `back-end/src/main/resources/application.properties`):
- `MYSQL_HOST`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
- `MG_HOST`
- `MG_USER`
- `MG_PASS`

La API usa las bases:
- MySQL: `inventory`
- MongoDB: `reviews`

## Ejecucion local
1) Levanta MySQL y MongoDB y configura las variables de entorno.  
2) Backend:
```bash
cd back-end
./mvnw spring-boot:run
```
3) Frontend: abre `front-end/index.html` en el navegador (o sirvelo con cualquier servidor estatico).

API base: `http://localhost:8080`

## Carga de datos (opcional)
- MySQL: ejecuta `insert_data.sql` en la base `inventory`.
- MongoDB: importa `reviews.json` en la base `reviews`.
