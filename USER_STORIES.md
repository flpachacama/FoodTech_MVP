# FASE 1 - Bases del proyecto (Modelo y reglas base)

-----------------------------------------------------------------------------
## HU1 - Gestionar estado de repartidores

**Como** sistema de asignación\
**Quiero** manejar los estados de los repartidores (ACTIVO, INACTIVO, EN_ENTREGA) \
**Para** saber cuáles están disponibles para asignarlos a un domicilio y descartar los que no. 

### Criterios de aceptación

```gherkin
Feature: Estado de repartidores

Scenario: Repartidor activo está disponible
Given un repartidor en estado ACTIVO
When el sistema verifica su disponibilidad
Then el repartidor debe ser considerado para asignación

Scenario: Repartidor en esta EN_ENTREGA no está disponible
Given un repartidor en estado EN_ENTREGA
When el sistema  valida su disponibilidad
Then el repartidor no debe ser considerado para asignación

Scenario: Repartidor INACTIVO no aparece como candidato
Given el repartidor tiene estado INACTIVO
When el sistema busca candidatos para un nuevo pedido
Then el repartidor no debe aparecer en la lista de candidatos

```
-----------------------------------------------------------------------------
## HU2 - Filtrar repartidores por cercanía 

**Como** sistema de asignación \
**Quiero** validar la distancia de todos repartidores con respecto al restaurante \
**Para** obtener un lista ordenada de los repartidores 

### Criterios de aceptación 

```gherkin 
Feature: Filtrado por distancia

Scenario: Repartidores en el mapa
Given repartidores con coordenadas y estado ACTIVO
When el sistema calcula la distacia al restaurante de todos los repartidores
Then se obtiene un lista ordenada de más cercanos a más lejanos 

Scenario: No hay repartidores ACTIVOS
Given todos los repartidores con estado INACTIVO o EN_ENTREGA
When el sistema intenta calcula la distacia
Then se obtiene un lista vacia
```

-----------------------------------------------------------------------------
## HU3 - Aplicar restricciones por clima

**Como** sistema de asignación \
**Quiero** aplicar reglas según el clima \
**Para** excluir transportes no aptos para el pedido

### Criterios de aceptación

```gherkin 
Feature: Restricciones por clima

Scenario: Lluvia fuerte excluye bicicleta y moto
Given clima Lluvia_Fuerte
And repartidores con transporte bicicleta y moto
When el sistema evalúa candidatos
Then estos repartidores no deben ser considerados

Scenario: Lluvia excluye bici
Given clima Lluvia (LLUVIA_FUERTE,LLUVIA_SUAVE)
And repartidores con transporte bicicleta 
When el sistema evalúa candidatos
Then estos repartidores no deben ser considerados
```
-----------------------------------------------------------------------------
# FASE 2 - Lógica del algoritmo

-----------------------------------------------------------------------------
## HU4 - Calcular prioridad de repartidores

**Como** sistema de asignación \
**Quiero** priorizar repartidores por distancia y velocidad del transporte, \
**Para** seleccionar el más eficiente

### Criterios de aceptación

```gherkin
Feature: Priorización de repartidores

Scenario: Selección por mejor combinación
Given múltiples repartidores candidatos
When el sistema evalúa distancia y tipo de transporte
Then debe asignar mayor prioridad el más eficiente

Scenario: Todos los candidatos tiene el mismo tiempo estimado
Given dos repartidores con el mismo tiempo estimado 
When el sistema prioriza 
Then se elige cualquiera de los dos sin error
```
-----------------------------------------------------------------------------
## HU5 - Asignar pedido automáticamente

**Como** sistema de asignación \
**Quiero** asignar el pedido al repartidor con menor tiempo estimado de llegada \
**Para** minimizar el tiempo de entraga al usuario 

### Criterios de aceptación

```gherkin
Feature: Asignación de pedidos

Scenario: Asignación exitosa
Given un pedido tiene estado pendiente
And repartidores están disponibles
When el sistema ejecuta el algoritmo
Then debe asignar el pedido al mejor candidato

Scenario: Sin repartidores disponibles
Given no hay repartidores válidos
When el sistema intenta asignar
Then el pedido debe quedar pendiente
```
-----------------------------------------------------------------------------
## HU6 - Actualizar estado del repartidor

**Como** sistema de asignación \
**Quiero** actualizar el estado del repartidor según los eventos del pedido \
**Para** garantizar que solo repartidores disponibles sea asignados

### Criterios de aceptación

```gherkin
Feature: Cambio de estados del repartidor

Scenario: Repartidor asignado cambia estado En_ENTREGA
Given un repartidor tiene estado ACTIVO
When se le asigna un pedido por el sistema 
Then el estado del repartidor debe cambiar a EN_ENTREGA

Scenario: Repartidor vuelve a ACTIVO al completar entrega
Given el repartidor tiene estado EN_ENTREGA
When marca el pedido como entregado
Then el estado del repartidor debe cambiar a ACTIVO

Scenario: Repartidor vuelve a ACTIVO al completar entrega
Given el repartidor tiene estado EN_ENTREGA
When marca el pedido como entregado
Then el estado del repartidor debe cambiar a ACTIVO

Scenario: Repartidor vuelve a ACTIVO al ser cancelado el pedido
Given el repartidor tiene estado EN_ENTREGA
When el usuario consumidor cancela el pedido
Then el estado del repartidor debe cambiar a ACTIVO

```
-----------------------------------------------------------------------------
# FASE 3 - Flujo de pedidos (Core del negocio)

-----------------------------------------------------------------------------
## HU7 - Generar pedido

**Como** usuario consumidor \
**Quiero** seleccionar restaurante, comida y generar un pedido \
**Para** solicitar la entrega a mi ubicación

### Criterios de aceptación

```gherkin
Feature: Generación de pedido

Scenario: Crear pedido correctamente
Given que el usuario ha  seleccionado un restaurante
And ha seleccionado productos del menú  
When confirma el pedido
Then el sistema debe registrar la orden correctamente
```
-----------------------------------------------------------------------------
## HU8 - Cancelar pedido

**Como** usuario consumidor \
**Quiero** cancelar un pedido \
**Para** evitar que sea procesado

### Criterios de aceptación

```gherkin
Feature: Cancelación de pedido

Scenario: Cancelar pedido antes de asignación
Given que el usuario tiene un pedido activo
When el usuario cancela el pedido
Then el sistema debe marcar el pedido como cancelado
```
-----------------------------------------------------------------------------
# FASE 4 - Interfaz de usuario consumidor

-----------------------------------------------------------------------------
## HU9 - Visualizar restaurantes en el mapa

**Como** usurio consumidor \
**Quiero** visualizar los restaurantes disponibles en un mapa \
**Para** poder seleccionar uno y realizar un pedido

### Criterios de aceptación

```gherkin
Feature: Visualización de restaurantes

Scenario: Mostrar restaurantes en el mapa
Given que existen restaurantes con coordenadas predefinidas
When el usuario accede al mapa
Then el sistema debe mostrar los restaurantes en sus respectivas ubicaciones
```
-----------------------------------------------------------------------------
## HU10 - Seleccionar restaurantes

**Como** usuario consumidor \
**Quiero** seleccionar un restaurante en el mapa \
**Para** poder ver su menú y realizar un pedido

### Criterios de aceptación

```gherkin
Feature: Selección de restaurante

Scenario: Seleccionar restaurante desde la UI 
Given que el usuario visualiza la lista de los restaurantes
When el usuario selecciona un restaurante
Then el sistema debe mostrar la información del restaurante seleccionado
```
-----------------------------------------------------------------------------
## HU11 - Mostrar tiempo estimado de entrega

**Como** usuario consumidor \
**Quiero** ver el tiempo estimado de entrega \
**Para** saber cuándo llegará mi pedido

### Criterios de aceptación

```gherkin
Feature: Tiempo Estimado

Scenario: Mostrar tiempo estimado 
Given un pedido asignado
When el sistema calcula el tiempo
Then debe mostrar un tiempo estimado al usuario
```
-----------------------------------------------------------------------------
# FASE 5 - Interfaz del repartidor

-----------------------------------------------------------------------------
## HU12 - Visualizar pedido asignado

**Como** repartidor \
**Quiero** ver el pedido asignado \
**Para** poder realizar la entrega

### Criterios de aceptación

```gherkin
Feature: Vista repartidor

Scenario: Mostrar pedido asignado
Given un repartidor con pedido asignado
When accede a su interfaz
Then debe visualizar los detalles del pedido
```
-----------------------------------------------------------------------------
## HU13 - Marcar pedido como entregado

**Como** repartidor \
**Quiero** marcar el pedido como entregado \
**Para** finalizar la entrega

### Criterios de aceptación

```gherkin
Feature: Finalización de entrega

Scenario: Marcar pedido como entregado
Given un pedido en curso
When el repartidor presiona "Entregado"
Then el pedido debe cambiar a estado completado
```