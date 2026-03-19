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
* Implementar función calcularDistancia(x1,y1,x2,y2) con distancia euclidiana
* Implementar función obtenerCandidatosCercanos()
* Filtrar solo repartidores con estado ACTIVO antes de calcular
* Ordenar lista por distancia descendente

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

#### HU6 - Actualizar estado del repartidor

* Implementar función cambiarEstado(repartidorId, nuevoEstado) en el servicio de repartidores
* Llamar automaticamente al asignar un pedido a un repartidor y cambié a EN_ENTREGA
* Llamar al dar detectar el evento de entregado y colocarl el repartido en ACTIVO
* Llmar al detectar evento de cancelación y colocar al repartidor en ACTIVO
* Exponer endpoint PUT/delivery/id/estado 

### FASE 3 - Flujo de pedidos (Core del negocio)

#### HU7 - Generar pedido

* Crear entidad Pedido con id, estado, restauranteId, productos, clienteId, clienteNombre, ClienteCoordenadas(x,y), tiempoEstimado
* Crear Enum EstadoPedido (PENDIENTE, ASIGNADO, ENTREGADO, CANCELADO)
* Crear entidad ProductoPedido con id, nombre, precio
* Crear capa de acceso a datos para gestionar pedidos 
* Crear tabla para pedidos 
* Crear tabla para almacenar Restaurantes 
* Insertar restaurantes con coodernadas, nombre y menus
* Implementar logica del carrito en el frontend 

