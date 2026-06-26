# EcoMarket

Plataforma de comercio electrónico sostenible — Trabajo Práctico Integrador de **Taller de Construcción de Software (INF243)**, Universidad Siglo 21.

## Integrante

| Nombre | Matrícula |
|---|---|
| Mauro Vera | SOF01586 |

**Materia:** Taller de Construcción de Software (INF243)
**Profesor:** Pablo Martín Maldonado
**Fecha de entrega:** 29/06/2026 — Entrega Final (Semana 2)

## Descripción

EcoMarket es una aplicación web full-stack que permite gestionar un catálogo de productos ecológicos, administrar un carrito de compras y confirmar pedidos, con historial de órdenes. El proyecto está compuesto por:

- **Backend:** Spring Boot 3.5 (Java 21) + Spring Data JPA + MySQL 8.4, expuesto como API REST.
- **Frontend:** React 18 + Vite, con componentes funcionales y hooks (`useState`, `useEffect`).
- **Persistencia:** MySQL corriendo en contenedor Docker.
- **Testing de API:** colección de Postman incluida en `/postman`.

## Tecnologías utilizadas

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 3.5, Spring Data JPA, Hibernate, MySQL Driver |
| Base de datos | MySQL 8.4 (Docker) |
| Frontend | React 18, Vite, Axios |
| Testing backend | JUnit 5, Mockito, MockMvc, H2 (tests de integración) |
| Testing frontend | Vitest, React Testing Library |
| Testing de API | Postman |

## Estructura del repositorio

```
ecomarket-tp-integrador/
├── backend/                 # API REST Spring Boot
│   └── src/main/java/com/ecomarket/
│       ├── config/          # CORS, carga de datos iniciales
│       ├── controller/      # Endpoints REST
│       ├── service/         # Lógica de negocio
│       ├── repository/      # Acceso a datos (Spring Data JPA)
│       ├── model/           # Entidades JPA
│       ├── dto/             # Objetos de transferencia (records)
│       ├── mapper/          # Conversión entidad ↔ DTO
│       └── exception/       # Manejo centralizado de errores
├── frontend/                # SPA React (Vite)
│   └── src/
│       ├── api/             # Cliente HTTP (Axios)
│       ├── components/      # Componentes funcionales + tests
│       └── utils/           # Utilidades (formato de moneda, etc.)
├── docker-compose.yml        # Servicio MySQL
├── docker/mysql-init.sql      # Inicialización de usuario/base
└── postman/                  # Colección de Postman exportada
```

## Requisitos previos

- Java 21 (o superior) y Maven
- Node.js 18+ y npm
- Docker y Docker Compose

## Cómo levantar el proyecto en local

### 1. Clonar el repositorio

```bash
git clone https://github.com/maurovera-hub/ecomarket-tp-integrador.git
cd ecomarket-tp-integrador
```

### 2. Levantar la base de datos (MySQL vía Docker)

```bash
docker compose up -d
```

Esto inicia un contenedor MySQL 8.4 en el puerto `3307` del host (mapeado al 3306 interno del contenedor), con la base y el usuario de la aplicación ya creados.

> **Nota:** si en tu máquina ya tenés un MySQL local corriendo en el puerto 3306, no hay conflicto: el contenedor se publica en el 3307 para evitarlo.

### 3. Levantar el backend

```bash
cd backend
mvn spring-boot:run
```

El backend queda disponible en `http://localhost:8080`. Al iniciar, carga automáticamente productos de ejemplo en el catálogo.

### 4. Levantar el frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

El frontend queda disponible en `http://localhost:5173`.

### 5. Usar la aplicación

Abrí `http://localhost:5173` en el navegador. Desde ahí podés:
- Navegar el catálogo de productos
- Agregar productos al carrito y ver el total calculado
- Confirmar un pedido con un mensaje personalizado
- Consultar el historial de órdenes

## Endpoints principales de la API

| Recurso | Método | Endpoint | Descripción |
|---|---|---|---|
| Productos | GET | `/api/productos` | Listar catálogo completo |
| Productos | GET | `/api/productos/{id}` | Detalle de un producto |
| Productos | POST | `/api/productos` | Crear producto |
| Productos | PUT | `/api/productos/{id}` | Modificar producto |
| Productos | DELETE | `/api/productos/{id}` | Eliminar producto |
| Carrito | GET | `/api/carrito` | Ver carrito actual con total |
| Carrito | POST | `/api/carrito/items` | Agregar ítem al carrito |
| Carrito | PUT | `/api/carrito/items/{itemId}` | Modificar cantidad de un ítem |
| Carrito | DELETE | `/api/carrito/items/{itemId}` | Quitar ítem del carrito |
| Carrito | DELETE | `/api/carrito` | Vaciar el carrito |
| Órdenes | POST | `/api/ordenes` | Confirmar pedido (con mensaje opcional) |
| Órdenes | GET | `/api/ordenes` | Historial de órdenes |
| Órdenes | GET | `/api/ordenes/{id}` | Detalle de una orden |

La colección completa, con ejemplos de request/response, está en `postman/EcoMarket.postman_collection.json`.

## Testing

El proyecto cuenta con 67 tests automatizados, todos en verde:

| Suite | Cantidad |
|---|---|
| Backend — unitarios (JUnit 5 + Mockito) | 22 |
| Backend — integración (MockMvc + H2) | 24 |
| Frontend — componentes (Vitest + RTL) | 21 |

Para correr los tests:

```bash
# Backend
cd backend
mvn test

# Frontend
cd frontend
npm test
```

## Decisiones de diseño

- **Arquitectura en capas:** Controller → Service → Repository, con DTOs (`records`) para no exponer las entidades JPA directamente en la API.
- **Modelo de datos:** `Producto`, `Carrito`/`ItemCarrito` y `Orden`/`ItemOrden`. La orden guarda una copia (snapshot) del nombre y precio del producto al momento de confirmarla, para que el historial no se altere si el producto se edita o elimina después.
- **Carrito único:** dado que el alcance del TP no incluye autenticación de usuarios, se maneja un carrito único que se crea on-demand y se vacía al confirmar el pedido.
- **Manejo de errores:** centralizado con `@RestControllerAdvice`, devolviendo respuestas JSON consistentes con códigos HTTP apropiados (404 para recursos no encontrados, 400 para errores de negocio como confirmar un carrito vacío).
- **Frontend:** componentes funcionales con `useState`/`useEffect`, flujo de datos unidireccional (props hacia los hijos, callbacks hacia el padre), con manejo visible de estados de carga y error.

## Uso de inteligencia artificial

Este proyecto fue desarrollado con asistencia de **Claude (Anthropic)**, utilizando Claude Code para la generación de código de backend y frontend, y Claude (claude.ai) para la planificación del trabajo, la resolución de problemas de configuración de entorno (Docker, autenticación de Git) y la redacción de esta documentación.

El detalle de qué partes fueron generadas con asistencia de IA, y de qué manera, se encuentra documentado en el informe técnico entregado junto a este proyecto, sección "Uso de Inteligencia Artificial".

## Documentación adicional

El informe técnico completo (arquitectura, modelo de datos, diseño de API, integración, conclusiones) se entrega junto a este repositorio como archivo PDF, fuera del control de versiones.
