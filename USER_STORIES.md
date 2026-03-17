### FASE 1 - Bases del proyecto (Modelo y reglas base)

### HU1 - Gestionar estado de repartidores

**Como** sistema \
**Quiero** manejar estados de repartidores (ACTIVO, INACTIVO, EN_ENTREGA) \
**Para** saber cuáles están disponibles para asignación
-----------------------------------------------------------------------------
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