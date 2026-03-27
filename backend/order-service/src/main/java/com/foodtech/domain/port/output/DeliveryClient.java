package com.foodtech.order.domain.port.output;

public interface DeliveryClient {

    DeliveryAssignmentResponse assign(DeliveryAssignmentRequest request);

    void releaseRepartidor(Long repartidorId, String evento);

    record DeliveryAssignmentRequest(
            Long pedidoId,
            Integer restauranteX,
            Integer restauranteY,
            String clima
    ) {}

    record DeliveryAssignmentResponse(
            Long pedidoId,
            String estado,
            Long repartidorId,
            String nombreRepartidor,
            Integer tiempoEstimado
    ) {}
}
