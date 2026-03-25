# Delivery Service - Quick Start (Docker)

Este README explica cómo levantar únicamente el microservicio `delivery-service` junto con su base de datos PostgreSQL usando Docker Compose, cómo consultar la tabla `repartidores` y ejemplos concretos de peticiones HTTP (POST y PUT) para probar el servicio.

Ubicación del compose (raíz del repo): `backend/docker-compose.yml` — contiene servicios `postgres`, `delivery-service` y `order-service`.

Requisitos
- Docker y Docker Compose instalados.

Levantar el stack (Postgres + delivery)
1. Desde la carpeta raíz del repositorio:

```bash
cd backend
docker compose up --build -d
```

2. Ver logs del servicio delivery:

```bash
docker compose logs -f delivery-service
```

Verificar estado de los contenedores:

```bash
docker compose ps
```

Consultar la tabla `repartidores` desde el host (si tienes `psql`):

```bash
PGPASSWORD=foodtech_pass psql -h localhost -p 5432 -U foodtech_user -d foodtech_db -c "SELECT id, nombre, estado, vehiculo, x, y FROM repartidores ORDER BY id;"
```

O ejecutar `psql` dentro del contenedor Postgres:

```bash
docker compose exec postgres psql -U foodtech_user -d foodtech_db -c "SELECT id, nombre, estado, vehiculo, x, y FROM repartidores ORDER BY id;"
```

Ejemplos de peticiones (base URL: `http://localhost:8080`)

1) Asignar repartidor a un pedido (POST /delivery)

Request (JSON):

```json
{ "pedidoId": 1, "restauranteX": 99, "restauranteY": 45, "clima": "SOLEADO" }
```

curl:

```bash
curl -sS -X POST http://localhost:8080/delivery \
  -H "Content-Type: application/json" \
  -d '{"pedidoId":1,"restauranteX":99,"restauranteY":45,"clima":"SOLEADO"}'
```

Respuesta 200 OK (ejemplo):

```json
{
  "pedidoId": 1,
  "estado": "ASIGNADO",
  "repartidorId": 2,
  "nombreRepartidor": "Ana Rodr\u00edguez"
}
```

2) Procesar evento externo para un repartidor (PUT /delivery/{id}/state)

Request (JSON):

```json
{ "evento": "ENTREGADO" }
```

curl (ejemplo con id 11):

```bash
curl -sS -X PUT http://localhost:8080/delivery/11/state \
  -H "Content-Type: application/json" \
  -d '{"evento":"ENTREGADO"}'
```

Respuesta 200 OK (ejemplo):

```json
{
  "id": 11,
  "nombre": "Carlos Mendoza",
  "estado": "ACTIVO",
  "vehiculo": "MOTO",
  "x": 25,
  "y": 40
}
```

Notas rápidas
- Los datos de ejemplo provienen de `src/main/resources/data.sql` que se carga al iniciar la aplicación.
- Si el endpoint devuelve 400/404/500 revisa los logs (`docker compose logs delivery-service`) y la tabla `repartidores`.

¿Quieres que genere una colección de Postman/Insomnia con estos ejemplos?