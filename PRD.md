# FoodTech 
## Visión

Foodtech es una plataforma web en la cual los usuarios pueden hacer pedidos de comida a domicilio, con variedad de restaurentes previamente registrados, igualmente repartidores que se movilizan en 3 modalidades, bicicleta, moto y carro, para entregar los pedidos a los clientes.

Nuestro objetivo es ser la plataforma con tiempos de entrega y asignación de pedidos más eficientes. 

## Objetivos
1. Asignar automaticamente al repartirdor más cercano al restaurante
2. Considerar la distancia, el vehiculo y el clima al momento de asignar.
3. El usuario tendrá un interfaz para elegir el resturante, hacer su pedido, y el repartidor tendrá un interfaz para ver el pedido que tiene y para darlo como entregado cuando así sea.  

## Alcance

### In

1. Repartidores y restaurantes con coordenadas simuladas (matriz X,Y) Harcodear restaurantes y repartidores.
2. Clima como parámetro del sistema Enum (Soleado, Lluvia_suave, Lluvia_Fuerte).
3. Reglas de restricción por clima (ej. lluvia_fuerte → Moto y bici queda excluida).
4. Algoritmo de selección: filtra por radio + clima, luego prioriza por distancia y velocidad estimada del vehículo.
5. Estado repartidor ACTIVO/INACTIVO/EN_ENTREGA.
6. Cambio de estado del repartidor al asignar.
7. Validación en todo el mapa del repartidor más cercano.
8. El usuario consumidor podrá seleccionar restaurante, seleccionar comida, generar esa orden, cancelar la orden.
### Out 