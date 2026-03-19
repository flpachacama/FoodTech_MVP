# Subtareas técnicas enfocadas en desarrollo 

## Visión general 

Se harán 2 microservicios 
    1. Microservicio para ordenes que gestionará pedidos, estados y flujos del cliente (order)
    2. Microservicio para repartidores que gestionará estados, ubicaciones y algoritmo de asignación(delivery)
Se hará una interfaz gráfica que consuma ambos microservicios 
-----------------------------------------------------------------------------

## Tareas técnicas por HU y por fases

### Fase 1 - Modelo base
#### HU1 - Gestionar estado de repartidores

* Crear entidad del Repartidor con atributos como id, nombre, estado, vehiculo y coordenadas (x,y)
* Crear ENUM para los estados del repartidor (ACTIVO, INACTIVO, EN_ENTREGA)
* Crear ENUM para los vehículos (BICICLETA, MOTO, AUTO)
* Implementar capa de acceso a datos para gestionar repartidores
* Insertar datos iniciales de repartidores en la base de datos

#### HU2 - Filtrar repartidores por cercanía
* Implementar función calcularDistancia(restauranteCoordenadas(x,y), repartidorCoordenadas(x,y)) con distancia euclidiana
* Implementar función obtenerCandidatosCercanos()
* Filtrar solo repartidores con estado ACTIVO antes de calcular
* Ordenar lista por distancia ascedente (el de menor distancia primerio)

#### HU3 - Aplicar restricciones por clima
* Crear Enums para el clima (SOLEADO, LLUVIA_SUAVE, LLUVIA_FUERTE)
* Implemementar función aplicarFiltroClima(candidatos, clima) 
* Crear reglas LLUVIA_FUERTE excluye BICICLETA y MOTO, LLUVIA_SUAVE excluye solo BICICLETA

### Fase 2 - Lógica del algortimo
#### HU4 - Calcular prioridad de repartidores
* Definir velocidades de los vehiculos (BICICLETA=15, MOTO=20, AUTO=30)
* Implementar función calcularTiempoEstimado(distancia, vehiculo)
* Ordenar candidatos por tiempo estimado de menor a mayor

#### HU5 - Asignar pedido automáticamente
* Crear endpoint POST /delivery 
* Recibir pedidoId, restauranteId
* Ejecutar lógica de filtrado siguiendo el flujo 
    - Filtrar por estado ACTIVO
    - Filtrar por clima
    - Calcular tiempos
    - Ordenarpor tiempo estimado
    - Tomar al primero
* Si la lista está vacía retornar PENDIENTE
* Si hay candidato -> Asignar repartidor al pedido y actualizar estado

Nota: Para esta versión las asignaciones pendientes quedán en ese estado, en futuras versiones se añadirá un (CRON) para reintentos automáticos cada cierto tiempo. 
#### HU6 - Actualizar estado del repartidor

* Implementar función cambiarEstado(repartidorId, nuevoEstado) en el servicio de repartidores
* Llamar automaticamente al asignar un pedido a un repartidor y cambié a EN_ENTREGA
* Llamar al dar detectar el evento de entregado y colocarl el repartido en ACTIVO
* Llmar al detectar evento de cancelación y colocar al repartidor en ACTIVO
* Exponer endpoint PUT/delivery/{id}/state 

### FASE 3 - Flujo de pedidos (Core del negocio)

#### HU7 - Agregar productos al carrito

* Crear entidad Pedido con id, estado, restauranteId, productos, clienteId, clienteNombre, ClienteCoordenadas(x,y), tiempoEstimado
* Crear Enum EstadoPedido (PENDIENTE, ASIGNADO, ENTREGADO, CANCELADO)
* Crear entidad ProductoPedido con id, nombre, precio
* Crear capa de acceso a datos para gestionar pedidos 
* Crear tabla para pedidos 
* Crear tabla para almacenar Restaurantes 
* Insertar restaurantes con coodernadas, nombre y menus
* Implementar logica del carrito en el frontend 

#### HU8 - Confirmar pedido
* Crear DTO para recibir el pedido con restauranteId, productos[], clienteNombre, clienteCoordenadas(x,y), clienteTelefono
* Crear DTO para la respuesta del pedido con restauranteId, productos[], clienteNombre, clienteCoordenadas(x,y), clienteTelefono, tiempoEstimado, estadoPedido
* Exponer endpoint POST /orders en el servicio de orders
* Al confirmar seguir el siguiente flujo
    - Persistir el pedido 
    - Llamar al servicio de delivery para asignar el pedido
    - Actualizar el estado 
    - Retornar tiempo estimado 
* Comunicación entre servicios via REST 

Nota: Para la comunicación entre servicios, si esta llega a fallar se debe contemplar entre dos opciones politica de reintentos usar un broker de mensajeria como RabbitMQ para manejar estos eventos. 
#### HU9 - Cancelar pedido
* Exponer endpoint PUT /orders/id/cancel
* Validar que el pedido no esté en ENTREGADO
* Cambiar estado a CANCELADO
* Si tenia repartidor asignado seguir el siguiente flujo
    - Llamar al endpoint PUT /delivery/{id}/state del servicio delivery para liberar al repartidor    (cambiar estado a ACTIVO)
    - Retornar confirmación de cancelación

### FASE 4 - Interfaz de usuario consumidor
#### HU10  Visualizar y seleccionar restaurante
* Crear componente MapaComponent con Canvas para visualizar el mapa
* Crear un servicio para consultar los restaurantes
* Renderizar posiciones x,y  en el mapa de repartidores y restaurantes con simbolos especificos
* Al dar click en restaurante, abrir un modal con datos del restaurante y mostrar menú. 

#### HU11 Visualizar pedido asignado
* Crear componente RepartidorPageComponent
* Crear servicio para consultar el estado del pedido y repartidor asignado
* Mostrar datos del cliente y tiempo estimado
* Exponer el endpoint GET/orders/id/active-order en el servicios de orders

### HU12 Marcar como entregado 
* Agregar Botón "Entregar" en el  componente de RepartidosPageComponente
* Al dar click en el botón llamar a PUT/orders/id/delivered en order
* El servicio order cambia el pedido a ENTREGADO y notifica al servicio delivery para cambiar el estado del repartidor a ACTIVOy liberarlo. 

