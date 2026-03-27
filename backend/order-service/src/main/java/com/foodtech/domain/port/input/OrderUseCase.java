package com.foodtech.order.domain.port.input;

import com.foodtech.order.infrastructure.web.dto.CancelOrderResponseDto;
import com.foodtech.order.infrastructure.web.dto.OrderRequestDto;
import com.foodtech.order.infrastructure.web.dto.OrderResponseDto;

/**
 * Puerto de entrada (input port) para el caso de uso de pedidos.
 * Define el contrato que el adaptador web (controller) debe invocar.
 */
public interface OrderUseCase {

    OrderResponseDto createOrder(OrderRequestDto request);

    CancelOrderResponseDto cancelOrder(Long pedidoId);
}
