# PROMPTS-HU10 — Visualizar y seleccionar restaurante

## Análisis de la HU

**Problema identificado:** La HU10 asume que existen endpoints para consultar restaurantes y repartidores, pero estos NO existen:

| Recurso | Endpoint necesario | ¿Existe? | Servicio |
|---------|-------------------|----------|----------|
| Restaurantes | `GET /restaurants` | ❌ NO | order-service |
| Restaurante por ID | `GET /restaurants/{id}` | ❌ NO | order-service |
| Repartidores | `GET /delivery/repartidores` | ❌ NO | delivery-service |

---

## Propuesta de modificación al SUBTASKS.md

```markdown
### HU10 - Visualizar y seleccionar restaurante

**Subtareas DEV (Backend - order-service):**
* Crear RestauranteResponseDto con id, nombre, coordenadasX, coordenadasY, menu (lista de productos)
* Crear RestauranteController con endpoints:
  - GET /restaurants → lista todos los restaurantes con sus menús
  - GET /restaurants/{id} → retorna un restaurante específico con su menú
* Crear RestauranteService para la lógica de consulta

**Subtareas DEV (Backend - delivery-service):**
* Crear RepartidorListResponseDto con id, nombre, estado, vehiculo, ubicacionX, ubicacionY
* Agregar endpoint GET /delivery/repartidores en AsignacionController
* Retornar lista de repartidores con sus coordenadas actuales

**Subtareas DEV (Frontend):**
* Crear componente MapaComponent con Canvas para visualizar el mapa
* Crear servicio RestauranteService para consultar GET /restaurants
* Crear servicio RepartidorService para consultar GET /delivery/repartidores
* Renderizar posiciones x,y en el mapa con símbolos específicos:
  - 🏪 Restaurantes (círculo rojo)
  - 🛵 Repartidores (triángulo según vehículo y color según estado)
* Al dar click en restaurante, abrir modal con datos y menú
```

---

## Propuesta de modificación a USER_STORIES.md (HU10)

```markdown
## HU10 - Visualizar y seleccionar restaurante

**Como** usuario consumidor \
**Quiero** ver los restaurantes y repartidores disponibles en el mapa y seleccionar un restaurante \
**Para** ver su menú y hacer un pedido

### Criterios de aceptación

```gherkin
Feature: Visualización y selección de un restaurante

Scenario: Usuario ve todos los restaurantes en el mapa
Given existen restaurantes en el sistema
When el usuario accede al mapa
Then debe ver todos los restaurantes con su nombre y ubicación
And debe ver todos los repartidores con su estado y vehículo

Scenario: Usuario selecciona un restaurante y ve su menú
Given el usuario visualiza los restaurantes en el mapa
When selecciona el restaurante "La Parrilla"
Then debe ver el menú de "La Parrilla" con sus productos y precios
And puede agregar productos al carrito

Scenario: No hay restaurantes registrados
Given no existen restaurantes en el sistema
When el usuario accede al mapa
Then debe ver un mensaje indicando que no hay restaurantes disponibles

Scenario: API retorna lista de restaurantes
Given el servicio order-service está activo
When se hace GET /restaurants
Then retorna lista de restaurantes con id, nombre, coordenadas y menú

Scenario: API retorna lista de repartidores
Given el servicio delivery-service está activo
When se hace GET /delivery/repartidores
Then retorna lista de repartidores con id, nombre, estado, vehículo y coordenadas
```
```

---

## Plan de ejecución (Backend primero)

### Paso 1 — Crear endpoint GET /restaurants en order-service
- Archivos: `RestauranteController.java`, `RestauranteResponseDto.java`, `RestauranteService.java`
- Dependencias previas: Ya existe `RestauranteJpaRepository` y `RestauranteEntity`
- Por qué este orden: El frontend necesita este endpoint para mostrar restaurantes

### Paso 2 — Crear endpoint GET /delivery/repartidores en delivery-service
- Archivos: Modificar `AsignacionController.java`, crear `RepartidorListResponseDto.java`
- Dependencias previas: Ya existe `RepartidorRepository` y modelo `Repartidor`
- Por qué este orden: El frontend necesita este endpoint para mostrar repartidores

### Paso 3 — Frontend (fuera de scope de este documento)
- Crear componentes y servicios Angular/React para consumir los endpoints

---

## Prompt Paso 1 — Crear endpoint GET /restaurants

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
- Ya existe `RestauranteEntity.java` en `infrastructure.persistence` con campos: id, nombre, coordenadaX, coordenadaY, menu (JSON de productos)
- Ya existe `RestauranteJpaRepository.java` que extiende JpaRepository<RestauranteEntity, Long>
- La tabla restaurantes tiene datos insertados con menús en formato JSON

### Lo que necesito

1. Crear `RestauranteResponseDto.java` en `infrastructure.web.dto`:
   - id (Long)
   - nombre (String)
   - coordenadaX (Integer)
   - coordenadaY (Integer)
   - menu (List<ProductoMenuDto>)
   
2. Crear `ProductoMenuDto.java` en `infrastructure.web.dto`:
   - id (Long)
   - nombre (String)
   - precio (Double)

3. Crear `RestauranteService.java` en `application.service`:
   - Inyectar RestauranteJpaRepository
   - Método `List<RestauranteResponseDto> getAllRestaurantes()`
   - Método `RestauranteResponseDto getRestauranteById(Long id)`
   - Si no existe el restaurante, lanzar RestauranteNotFoundException

4. Crear `RestauranteController.java` en `infrastructure.web.controller`:
   - @RestController con @RequestMapping("/restaurants")
   - GET / → retorna lista de restaurantes
   - GET /{id} → retorna restaurante específico

### Reglas
- Usar Lombok (@Data, @Builder, etc.)
- El menú está guardado como JSON en la columna `menu`, parsearlo a List<ProductoMenuDto>
- Seguir el mismo estilo del OrderController existente
```

---

## Prompt Paso 2 — Crear endpoint GET /delivery/repartidores

```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech`

### Contexto existente
- Ya existe modelo `Repartidor.java` en `domain.model` con: id, nombre, estado (EstadoRepartidor), vehiculo (Vehiculo), ubicacion (Coordenada)
- Ya existe `RepartidorRepository` (puerto) y `RepartidorPersistenceAdapter` (adaptador)
- Ya existe `AsignacionController.java` con endpoints POST /delivery y PUT /delivery/{id}/state

### Lo que necesito

1. Crear `RepartidorListResponseDto.java` en `infrastructure.web.dto`:
   - id (Long)
   - nombre (String)
   - estado (String) — "ACTIVO", "INACTIVO", "EN_ENTREGA"
   - vehiculo (String) — "BICICLETA", "MOTO", "AUTO"
   - ubicacionX (Integer)
   - ubicacionY (Integer)

2. Agregar método en `AsignacionApplicationService.java`:
   - `List<RepartidorListResponseDto> getAllRepartidores()`

3. Agregar endpoint en `AsignacionController.java`:
   - GET /delivery/repartidores → retorna lista de todos los repartidores con su ubicación actual

### Reglas
- Usar el RepartidorRepository existente para obtener todos
- Mapear Coordenada a ubicacionX/ubicacionY
- Mapear enums a String para el DTO
- Seguir el mismo estilo del controller existente
```

---

## Resumen de archivos a crear/modificar

### order-service

| Archivo | Acción |
|---------|--------|
| `infrastructure/web/dto/ProductoMenuDto.java` | **Crear** |
| `infrastructure/web/dto/RestauranteResponseDto.java` | **Crear** |
| `application/service/RestauranteService.java` | **Crear** |
| `infrastructure/web/controller/RestauranteController.java` | **Crear** |

### delivery-service

| Archivo | Acción |
|---------|--------|
| `infrastructure/web/dto/RepartidorListResponseDto.java` | **Crear** |
| `application/service/AsignacionApplicationService.java` | Modificar |
| `infrastructure/web/controller/AsignacionController.java` | Modificar |

---

## Pruebas sugeridas (curl)

```bash
# Listar todos los restaurantes
curl -s http://localhost:8081/restaurants | jq

# Obtener restaurante específico
curl -s http://localhost:8081/restaurants/1 | jq

# Listar todos los repartidores
curl -s http://localhost:8080/delivery/repartidores | jq
```

---

## Nota importante

Esta HU10 requiere **trabajo backend antes del frontend**. Los endpoints deben existir y estar probados antes de que el equipo de frontend comience a consumirlos.

Dependencias:
1. ✅ HU1-HU6 (delivery-service funcionando)
2. ✅ HU7-HU9 (order-service funcionando)
3. 🔄 **HU10 Backend** ← Estamos aquí
4. ⏳ HU10 Frontend
