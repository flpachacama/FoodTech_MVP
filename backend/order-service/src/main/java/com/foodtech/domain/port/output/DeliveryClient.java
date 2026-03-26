package com.foodtech.order.domain.port.output;

/**
 * Puerto de salida (output port) para la comunicación con el delivery-service.
 * La implementación REST del adaptador se crea en el Paso 3.
 */
public interface DeliveryClient {

    /**
     * Solicita al delivery-service la asignación de un repartidor para el pedido.
     *
     * @param pedidoId            ID del pedido persistido
     * @param restauranteId       ID del restaurante origen
     * @param clienteCoordenadasX coordenada X del cliente destino
     * @param clienteCoordenadasY coordenada Y del cliente destino
     * @return respuesta de asignación con estado y tiempo estimado
     */
    DeliveryAssignmentResponse assignOrder(
            Long pedidoId,
            Long restauranteId,
            Integer clienteCoordenadasX,
            Integer clienteCoordenadasY
    );

    /**
     * Respuesta del delivery-service tras intentar asignar un repartidor.
     *
     * @param estado          "ASIGNADO" | "PENDIENTE"
     * @param tiempoEstimado  minutos estimados de entrega (null si no fue asignado)
     */
    record DeliveryAssignmentResponse(String estado, Integer tiempoEstimado) {}
}
