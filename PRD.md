# FoodTech - MVP

-----------------------------------------------------------------------------------------------
## Visión

Foodtech es una plataforma web en la cual los usuarios pueden hacer pedidos de comida a domicilio, con variedad de restaurentes previamente registrados, igualmente repartidores que se movilizan en 3 modalidades, bicicleta, moto y carro, para entregar los pedidos a los clientes.

Nuestro objetivo es ser la plataforma con tiempos de entrega y asignación de pedidos más eficientes.

-----------------------------------------------------------------------------------------------

## Objetivos
1. Asignar automaticamente al repartirdor más cercano al restaurante
2. Considerar la distancia, el vehiculo y el clima al momento de asignar.
3. El usuario tendrá un interfaz para elegir el resturante, hacer su pedido, y el repartidor tendrá un interfaz para ver el pedido que tiene y para darlo como entregado cuando así sea.
-----------------------------------------------------------------------------------------------

## Alcance

-----------------------------------------------------------------------------------------------
### In

1. Repartidores y restaurantes con coordenadas simuladas (matriz X,Y) Harcodear restaurantes y repartidores.
2. Clima como parámetro del sistema Enum (Soleado, Lluvia_suave, Lluvia_Fuerte).
3. Reglas de restricción por clima (ej. lluvia_fuerte → Moto y bici queda excluida).
4. Algoritmo de selección: filtra por radio + clima, luego prioriza por distancia y velocidad estimada del vehículo.
5. Estado repartidor ACTIVO/INACTIVO/EN_ENTREGA.
6. Cambio de estado del repartidor al asignar.
7. Validación en todo el mapa del repartidor más cercano.
8. El usuario consumidor podrá seleccionar restaurante, seleccionar comida, generar esa orden, cancelar la orden.
9. Manejo de tiempos(estimación por distancia y vehiculo), para mostrar a los clientes.
10. Interfaz para repartidor. Solo vista de lo que fue asignado y botón de entregado.
11. El usuario puede agregar varias comidas que ofrezca el restaurante.
12. Interfaz gráfica del mapa con interacción básica para seleccionar restaurante y hacer pedido.

### Out 
1. LOGIN/REGISTER
2. GPS y API clima
3. El repartidor pueda rechazar el pedido
4. Multiples pedidos para un repartidor
5. Seguimiento en tiempo real
6. Notificaciones push
7. Estimación dinámica del tráfico
8. Historial de entregas
9. Recomendaciones u ofertas.
10. Interfaz para agregar restaurantes y repartidores.
11. Interfaz para el restaurante. 
-----------------------------------------------------------------------------------------------
## Riesgos de Negocio

-----------------------------------------------------------------------------------------------

### Falta de repartidores disponibles
* Si hay pocos repartidores en una zona determinada, los pedidos podrían experimentar retrasos.

### Alta demanda de pedidos
* Durante horas pico puede haber más pedidos que repartidores disponibles.

### Baja satisfacción del cliente
* Si el algoritmo no optimiza correctamente las asignaciones, los tiempos de entrega pueden aumentar.

-----------------------------------------------------------------------------------------------
## Riesgos Técnicos

-----------------------------------------------------------------------------------------------

### Error en el cálculo de distancias
* Si el sistema calcula mal las distancias entre repartidor y restaurante, podría asignar pedidos incorrectamente.

### Error en la consideración del clima
* Si la data sobre el clima no se lee correctamente o se define mal, el sistema podría tomar decisiones incorrectas.

### Escalabilidad del algoritmo
* Si el número de pedidos y repartidores crece demasiado, el algoritmo podría tardar más tiempo en procesar las asignaciones.
