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
* Crear 