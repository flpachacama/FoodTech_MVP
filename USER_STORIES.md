# FASE 1 - Bases del proyecto (Modelo y reglas base)

-----------------------------------------------------------------------------
## HU1 - Gestionar estado de repartidores

**Como** sistema \
**Quiero** manejar estados de repartidores (ACTIVO, INACTIVO, EN_ENTREGA) \
**Para** saber cuáles están disponibles para asignación

### Criterios de aceptación

```gherkin
Feature: Estado de repartidores

Scenario: Repartidor activo disponible
Given un repartidor en estado ACTIVO
When el sistema evaluá disponibilidad
Then el repartidor debe ser considerado para asignación

Scenario: Repartidor en entrega no disponible
Given un repartidor en estado EN_ENTREGA
When el sistema evaluá disponibilidad
Then el repartidor no debe ser considerado
```
-----------------------------------------------------------------------------
## HU2 - Filtrar repartidores por cercanía 

**Como** sistema de asignación \
**Quiero** filtrar repartidores dentro de un radio determinado \
**Para** considerar solo candidatos cercanos 

### Criterios de aceptación 

```gherkin 
Feature: Filtrado por distancia

Scenario: Repartidores dentro del radio
Given repartidores con coordenadas
When el sistema calcula la distacia al restaurante
Then solo debe considerar los que están dentro del radio permitido
```
-----------------------------------------------------------------------------
## HU3 - Aplicar restricciones por clima

**Como** sistema de asignación \
**Quiero** aplicar reglas según el clima \
**Para** excluir transportes no aptos

### Criterios de aceptación

```gherkin 
Feature: Restricciones por clima

Scenario: Lluvia fuerte excluye bici y moto
Given clima Lluvia_Fuerte
And repartidores con transporte bicicleta y moto
When el sistema evalúa candidatos
Then estos repartidores no deben ser considerados

Scenario: Lluvia excluye bici
Given clima Lluvia
And repartidores con transporte bicicleta 
When el sistema evalúa candidatos
Then estos repartidores no deben ser considerados
```
-----------------------------------------------------------------------------
# FASE 2 - Lógica del algoritmo

-----------------------------------------------------------------------------
## HU4 - Calcular prioridad de repartidores

**Como** sistema de asignación \
**Quiero** priorizar repartidores por distancia y velocidad del transporte \
**Para** seleccionar el más eficiente

### Criterios de aceptación

```gherkin
Feature: Priorización de repartidores

Scenario: Selección por mejor combinación
Given múltiples repartidores candidatos
When el sistema evalúa distancia y tipo de transporte
Then debe asignar mayor prioridad el más eficiente
```
-----------------------------------------------------------------------------