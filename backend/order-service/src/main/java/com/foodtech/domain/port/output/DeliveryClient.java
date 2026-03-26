package com.foodtech.order.domain.port.output;

/**
 * Puerto de salida (output port) para la comunicación con el delivery-service.
 * Contrato real: POST /delivery
 */
public interface DeliveryClient {

    DeliveryAssignmentResponse assign(DeliveryAssignmentRequest request);

    /**
     * Petición al delivery-service.
     * clima se envía hardcodeado como "SOLEADO" por ahora.
     */
    record DeliveryAssignmentRequest(
            Long pedidoId,
            Integer restauranteX,
            Integer restauranteY,
            String clima
    ) {}

    /**
     * Respuesta del delivery-service.
     * estado: "ASIGNADO" | "PENDIENTE"
     */
    record DeliveryAssignmentResponse(
            Long pedidoId,
            String estado,
            Long repartidorId,
            String nombreRepartidor
    ) {}
}
