## HU1 - Gestionar estado de repartidores

* Diseñar casos de prueba por cada estado
* Validar que EN_ENTREGA e INACTIVO no sean seleccionados
* Probar cambios de estado en distintos escenarios
* Validar integridad de estados en flujo completo
------------------------------------------------------------
## HU2 - Filtrar repartidores por cercanía

* Validar cálculo correcto de distancias
* Probar ordenamiento correcto
* Validar comportamiento sin repartidores activos
* Probar casos límite
------------------------------------------------------------
## HU3 - Aplicar restricciones por clima

* Probar cada tipo de clima
* Validar exclusión de bici y moto en lluvia fuerte
* Validar combinaciones clima + transporte
* Verificar comportamiento sin candidatos
------------------------------------------------------------
## HU4 - Calcular prioridad de repartidores 

* Validar cálculo correcto del tiempo estimado
* Probar diferentes combinaciones de transporte
* Validar comportamiento en empates
* Verificar consistencia de resultados
------------------------------------------------------------
## HU5 - Asignar pedido automaticamente

* Validar asignación correcta
* Probar escenarios sin repartidores
* Validar que siempre se elige el mejor candidato
* Verificar consistencia del flujo
------------------------------------------------------------
## HU6 - Actualizar estado del repartidor

* Probar transición ACTIVO -> EN_ENTREGA
* Probar transición EN_ENTREGA -> ACTIVO
* Validar cambios en cancelación
* Detectar estados inconsistentes
------------------------------------------------------------
## HU7 - Generar Pedido

* Probar agregar múltiples productos
* Validar eliminación de productos
* Verificar cálculo de total
* Validar restricción de carrito vacío
------------------------------------------------------------
## HU8 - Ingresar datos y confirmar pedido

* Validar campos obligatorios
* Probar confirmación exitosa
* Probar sin repartidores disponibles
* Validar visualización del tiempo estimado
------------------------------------------------------------
## HU9 - Cancelar pedido

* Validar cancelación antes de asignación
* Verificar cambio de estado
* Validar liberación del repartidor
* Probar multiples cancelaciones
------------------------------------------------------------