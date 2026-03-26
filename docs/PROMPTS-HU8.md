# HU8 - Confirmar pedido

## Plan de ejecución

### Paso 1 — DTOs de entrada y salida
- Archivos: src/main/java/com/foodtech/order/infrastructure/web/dto/OrderRequestDto.java, src/main/java/com/foodtech/order/infrastructure/web/dto/OrderResponseDto.java
- Dependencias previas: ninguno (solo crear DTOs)
- Por qué este orden: los DTOs se usan por la capa web y la aplicación; crear primero permite diseñar la API y el service

### Paso 2 — Puerto de entrada (use case) y Application Service
- Archivos: src/main/java/com/foodtech/order/domain/port/input/OrderUseCase.java, src/main/java/com/foodtech/order/application/service/OrderApplicationService.java
- Dependencias previas: `PedidoRepository` (ya existe: com.foodtech.order.domain.port.output.PedidoRepository), DTOs del Paso 1
- Por qué este orden: la capa de aplicación implementa la lógica orquestadora (persistir, llamar a delivery, actualizar estado)

### Paso 3 — Cliente REST hacia delivery-service (infraestructura)
- Archivos: src/main/java/com/foodtech/order/infrastructure/web/client/DeliveryClient.java, src/main/java/com/foodtech/order/infrastructure/config/RestTemplateConfig.java
- Dependencias previas: Paso 2 (service), variables de entorno para URL del servicio delivery
- Por qué este orden: el service necesita un cliente para solicitar la asignación; el cliente se separa en infraestructura

### Paso 4 — Controller y excepciones HTTP
- Archivos: src/main/java/com/foodtech/order/infrastructure/web/controller/OrderController.java, src/main/java/com/foodtech/order/infrastructure/web/exception/OrderExceptionHandler.java
- Dependencias previas: DTOs (Paso 1), OrderApplicationService (Paso 2)
- Por qué este orden: exponer `/orders` una vez que el service y DTOs estén listos

### Paso 5 — Ajustes y pruebas rápidas
- Archivos: modificar `OrderApplicationService.java` (si aplica) y actualizar mensajes de error en `OrderExceptionHandler.java`
- Dependencias previas: todos los pasos anteriores
- Por qué este orden: validar flujo end-to-end y pulir errores antes de commit

## Prompts Copilot

## Prompt Paso 1 — DTOs de entrada y salida
```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
- Clases de dominio: `com.foodtech.order.domain.model.Pedido`, `ProductoPedido`, `EstadoPedido`.
- Repositorios/Adapters: `com.foodtech.order.infrastructure.persistence.adapter.PedidoDataAdapter` y `com.foodtech.order.infrastructure.persistence.PedidoJpaRepository`.

### Lo que necesito
Crear dos DTOs en `com.foodtech.order.infrastructure.web.dto`:
- `OrderRequestDto` con campos: `Long restauranteId`, `List<ProductoPedidoDto> productos`, `String clienteNombre`, `Integer clienteCoordenadasX`, `Integer clienteCoordenadasY`, `String clienteTelefono`.
- `OrderResponseDto` con campos: `Long id`, `Long restauranteId`, `List<ProductoPedidoDto> productos`, `String clienteNombre`, `Integer clienteCoordenadasX`, `Integer clienteCoordenadasY`, `String clienteTelefono`, `Integer tiempoEstimado`, `EstadoPedido estado`.

También crear `ProductoPedidoDto` en el mismo paquete con `Long id`, `String nombre`, `BigDecimal precio`.

### Reglas
- No crear ningún archivo fuera de los mencionados.
- Usar Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`) donde aplique.
- Los nombres de paquetes y clases deben seguir la nomenclatura del proyecto.
```

## Prompt Paso 2 — Puerto de entrada y Application Service
```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
- Dominio: `com.foodtech.order.domain.model.Pedido`, `EstadoPedido`, `ProductoPedido`.
- Puerto salida: `com.foodtech.order.domain.port.output.PedidoRepository` con métodos `save(Pedido)`, `findById(Long)`, `findAll()`.
- Adapter de persistencia: `com.foodtech.order.infrastructure.persistence.adapter.PedidoDataAdapter` que implementa `PedidoRepository`.
- DTOs: `com.foodtech.order.infrastructure.web.dto.OrderRequestDto` y `OrderResponseDto` (del Paso 1).

### Lo que necesito
- Crear `com.foodtech.order.domain.port.input.OrderUseCase` con método `OrderResponseDto createOrder(OrderRequestDto request)`.
- Crear `com.foodtech.order.application.service.OrderApplicationService` que implemente `OrderUseCase` y haga el siguiente flujo en `createOrder`:
  1. Validar campos obligatorios del request (nombre, telefono, coordenadas, restauranteId, productos).
  2. Mapear `OrderRequestDto` -> `Pedido` dominio.
  3. Persistir `Pedido` usando `PedidoRepository.save` (estado inicial PENDIENTE).
  4. Llamar a `DeliveryClient.assignOrder(pedidoId, ...)` (cliente infra del Paso 3) para solicitar asignación.
  5. Actualizar estado del `Pedido` según la respuesta de delivery (ASIGNADO o PENDIENTE) y guardar cambios.
  6. Mapear `Pedido` -> `OrderResponseDto` incluyendo `tiempoEstimado` y `estado`.

### Reglas
- No crear archivos fuera de los mencionados.
- Usar `@RequiredArgsConstructor` (Lombok) para inyectar `PedidoRepository` y `DeliveryClient`.
- Manejar excepciones de cliente REST devolviendo `IllegalStateException` o excepciones custom (se crearán en Paso 4).
```

## Prompt Paso 3 — Cliente REST hacia delivery-service
```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
- El `delivery-service` es otro microservicio que expone un endpoint REST para asignar pedidos (p.ej. `POST /delivery/assign`).
- En este proyecto existen DTOs y `OrderApplicationService` (Paso 2) que dependerá del cliente.

### Lo que necesito
- Crear `com.foodtech.order.infrastructure.web.client.DeliveryClient` como componente Spring con un método `AssignResponse assignOrder(Long pedidoId, OrderAssignRequest)` que llame vía `RestTemplate` o `WebClient` al servicio de delivery.
- Crear `com.foodtech.order.infrastructure.config.RestTemplateConfig` que expone un `@Bean RestTemplate` y lee la URL base de `delivery.service.url` desde variables de entorno (`DELIVERY_SERVICE_URL`).
- Definir clases auxiliares `OrderAssignRequest` y `AssignResponse` (internas o en paquete client) con los campos mínimos: `boolean assigned`, `Integer tiempoEstimado`, `String error`.

### Reglas
- No modificar otros microservicios ni sus properties.
- Usar `@Value("${delivery.service.url:http://localhost:8081}")` para la URL por defecto.
- Manejar errores HTTP lanzando una excepción propia que la capa de aplicación tratará.
```

## Prompt Paso 4 — Controller y excepciones HTTP
```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
- DTOs (Paso 1), `OrderApplicationService` (Paso 2) y `DeliveryClient` (Paso 3).

### Lo que necesito
- Crear `com.foodtech.order.infrastructure.web.controller.OrderController` con endpoint `@PostMapping("/orders")` que:
  - Reciba `OrderRequestDto` en el body, valide (p.ej. `@Valid`) y llame a `orderApplicationService.createOrder(request)`.
  - Retorne `ResponseEntity<OrderResponseDto>` con `201 Created` al crear correctamente.
- Crear `com.foodtech.order.infrastructure.web.exception.OrderExceptionHandler` con `@ControllerAdvice` para traducir excepciones de validación y errores de comunicación con delivery a respuestas HTTP claras (400, 502, 500).

### Reglas
- No tocar otras rutas existentes.
- Usar `@Validated` y `@Valid` en DTOs y controller.
- Seguir convenciones de nombres del proyecto.
```

## Prompt Paso 5 — Ajustes y pruebas rápidas
```
Tengo un microservicio Spring Boot (Java 17) con Arquitectura Hexagonal.
Paquete base: `com.foodtech.order`

### Contexto existente
- Todos los artefactos creados en pasos anteriores.

### Lo que necesito
- Revisar `OrderApplicationService` y añadir logs e instrumentación mínima.
- Añadir pruebas unitarias simples (Mockito) para la lógica de `createOrder` en `OrderApplicationService` simulando: persistencia exitosa + entrega asignada, persistencia + delivery falla (dejar PENDIENTE), validación de entrada.

### Reglas
- Crear solo tests en `src/test/java` relacionados con el service.
- No modificar código del otro microservicio `delivery-service`.
```

---

Guía: copia cada prompt al editor de GitHub Copilot (o a tu IDE) y genera las implementaciones sugeridas. Si alguna parte del HU es ambigua (por ejemplo formato exacto del request al delivery-service), pregúntame antes de generar el código.
