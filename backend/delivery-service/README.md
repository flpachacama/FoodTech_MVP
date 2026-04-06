# Delivery Service

Microservicio encargado de gestionar los **repartidores** y la **asignación inteligente** de pedidos basada en proximidad y condiciones climáticas.

- **Puerto:** `8080`
- **Base de datos:** PostgreSQL — `foodtech_db` (tabla: `repartidores`)
- **Java:** 17 — Spring Boot 3.x
- **Arquitectura:** Hexagonal (Ports & Adapters)

---

## Levantar con Docker (recomendado)

Desde la carpeta `backend/` del repositorio:

```bash
# Primera vez o luego de cambios
docker compose up -d --build

# Ver logs
docker compose logs -f delivery-service

# Reiniciar
docker compose restart delivery-service
```

---

## Endpoints

Base URL: `http://localhost:8080`

### Repartidores (consulta)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/delivers` | Lista todos los repartidores |
| `GET` | `/delivers/{id}` | Obtiene un repartidor por ID |
| `GET` | `/delivery/fooders` | Lista todos los repartidores (alias) |

**Ejemplo — listar repartidores:**
```bash
curl http://localhost:8080/delivers
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Carlos Mendoza",
    "estado": "ACTIVO",
    "vehiculo": "MOTO",
    "x": 25,
    "y": 40
  }
]
```

### Asignación de pedidos

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/delivery` | Asigna repartidor a un pedido |
| `PUT` | `/delivery/{id}/state` | Actualiza estado del repartidor |

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

