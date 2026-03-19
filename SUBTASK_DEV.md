# Subtareas técnicas enfocadas en desarrollo 

## Visión general 

Se harán 2 microservicios 
    1. Microservicio para ordenes que gestionará pedidos, estados y flujos del cliente
    2. Microservicio para repartidores que gestionará estados, ubicaciones y algoritmo de asignación
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

