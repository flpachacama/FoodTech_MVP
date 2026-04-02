# Order Service

Microservicio encargado de gestionar los **pedidos** y los **restaurantes** de la plataforma FoodTech. Cuando se crea un pedido, este servicio se comunica internamente con el `delivery-service` para asignar automáticamente un repartidor disponible.

- **Puerto:** `8081`
- **Base de datos:** PostgreSQL — `foodtech_orders` (tablas: `restaurantes`, `pedidos`)
- **Java:** 17 — Spring Boot 3.x

---

## Levantar con Docker (recomendado)

Desde la carpeta `backend/` del repositorio:

```bash
# Primera vez o luego de cambios en el código
docker compose up -d --build

# Solo reiniciar (sin rebuild)
docker compose restart order-service

# Ver logs en tiempo real
docker compose logs -f order-service

# Bajar todos los servicios
docker compose down

# Bajar y borrar base de datos (reset completo)
docker compose down -v
```

Estado de los contenedores:

```bash
docker compose ps
```

---

## Endpoints

Base URL: `http://localhost:8081`

### Restaurantes

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET`  | `/restaurants` | Lista todos los restaurantes con su menú |
| `GET`  | `/restaurants/{id}` | Obtiene un restaurante por ID |

**Ejemplo — listar restaurantes:**
```bash
curl http://localhost:8081/restaurants
```

**Ejemplo — obtener restaurante por ID:**
```bash
curl http://localhost:8081/restaurants/1
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "La Hamburguesería",
  "coordenadaX": 10,
  "coordenadaY": 20,
  "menu": [
    { "id": 1, "nombre": "Hamburguesa Clásica", "precio": 18000 },
    { "id": 2, "nombre": "Hamburguesa BBQ", "precio": 22000 }
  ]
}
```

---

### Pedidos

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/orders` | Crea un nuevo pedido y asigna repartidor |
| `PUT`  | `/orders/{id}/cancel` | Cancela un pedido existente |
| `PUT`  | `/orders/{id}/deliver` | Marca un pedido como entregado |

**Ejemplo — crear pedido:**
```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "restauranteId": 1,
    "clima": "SOLEADO",
    "productos": [
      { "id": 1, "nombre": "Hamburguesa Clásica", "precio": 18000 }
    ]
  }'
```

**Respuesta 200 OK:**
```json
{
  "pedidoId": 1,
  "estado": "ASIGNADO",
  "repartidorId": 3,
  "nombreRepartidor": "Carlos Mendoza",
  "total": 18000
}
```

**Ejemplo — cancelar pedido:**
```bash
curl -X PUT http://localhost:8081/orders/1/cancel
```

**Ejemplo — marcar como entregado:**
```bash
curl -X PUT http://localhost:8081/orders/1/deliver
```

---

## Consultar la base de datos directamente

```bash
# Listar tablas
docker exec -it foodtech_postgres psql -U foodtech_user -d foodtech_orders -c "\dt"

# Ver restaurantes
docker exec -it foodtech_postgres psql -U foodtech_user -d foodtech_orders -c "SELECT * FROM restaurantes;"

# Ver pedidos
docker exec -it foodtech_postgres psql -U foodtech_user -d foodtech_orders -c "SELECT * FROM pedidos;"
```

---

## Comunicación entre servicios

```
order-service (8081)
    └── POST /delivery  →  delivery-service (8080)
                            asigna repartidor disponible más cercano
```

La URL del delivery-service se configura en `src/main/resources/application.properties`:
```properties
delivery.service.url=http://localhost:8080
```
En Docker, se inyecta como variable de entorno `DELIVERY_SERVICE_URL=http://delivery-service:8080`.

---

## Datos iniciales

Los datos de ejemplo se cargan automáticamente al iniciar desde:
- `src/main/resources/data.sql` — restaurantes de prueba
- `src/main/resources/db/restaurantes.sql` — datos adicionales

Son idempotentes: no insertan si el registro ya existe (`ON CONFLICT DO NOTHING`).
