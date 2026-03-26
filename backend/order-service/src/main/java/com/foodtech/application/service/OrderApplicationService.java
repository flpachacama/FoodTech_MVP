package com.foodtech.order.application.service;

import com.foodtech.order.domain.model.EstadoPedido;
import com.foodtech.order.domain.model.Pedido;
import com.foodtech.order.domain.model.ProductoPedido;
import com.foodtech.order.domain.port.input.OrderUseCase;
import com.foodtech.order.domain.port.output.DeliveryClient;
import com.foodtech.order.domain.port.output.DeliveryClient.DeliveryAssignmentResponse;
import com.foodtech.order.domain.port.output.PedidoRepository;
import com.foodtech.order.infrastructure.web.dto.OrderRequestDto;
import com.foodtech.order.infrastructure.web.dto.OrderResponseDto;
import com.foodtech.order.infrastructure.web.dto.ProductoPedidoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderApplicationService implements OrderUseCase {

    private final PedidoRepository pedidoRepository;
    private final DeliveryClient deliveryClient;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto request) {

        // ── 1. Validar campos obligatorios ───────────────────────────────────
        validateRequest(request);

        // ── 2. Mapear DTO → dominio con estado inicial PENDIENTE ─────────────
        Pedido pedido = toDomain(request);

        // ── 3. Persistir pedido ──────────────────────────────────────────────
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // ── 4. Solicitar asignación al delivery-service ──────────────────────
        DeliveryAssignmentResponse deliveryResponse;
        try {
            deliveryResponse = deliveryClient.assignOrder(
                    pedidoGuardado.getId(),
                    pedidoGuardado.getRestauranteId(),
                    pedidoGuardado.getClienteCoordenadasX(),
                    pedidoGuardado.getClienteCoordenadasY()
            );
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Error al comunicarse con el servicio de delivery: " + ex.getMessage(), ex
            );
        }

        // ── 5. Actualizar estado según respuesta de delivery ─────────────────
        EstadoPedido estadoFinal = resolveEstado(deliveryResponse.estado());
        Integer tiempoEstimado  = deliveryResponse.tiempoEstimado();

        Pedido pedidoActualizado = Pedido.builder()
                .id(pedidoGuardado.getId())
                .restauranteId(pedidoGuardado.getRestauranteId())
                .clienteId(pedidoGuardado.getClienteId())
                .clienteNombre(pedidoGuardado.getClienteNombre())
                .clienteCoordenadasX(pedidoGuardado.getClienteCoordenadasX())
                .clienteCoordenadasY(pedidoGuardado.getClienteCoordenadasY())
                .productos(pedidoGuardado.getProductos())
                .estado(estadoFinal)
                .tiempoEstimado(tiempoEstimado)
                .build();

        pedidoRepository.save(pedidoActualizado);

        // ── 6. Mapear dominio → DTO de respuesta ─────────────────────────────
        return toResponse(pedidoActualizado, request);
    }

    // ─── Helpers privados ────────────────────────────────────────────────────

    private void validateRequest(OrderRequestDto request) {
        if (request.getRestauranteId() == null) {
            throw new IllegalArgumentException("El restauranteId es obligatorio");
        }
        if (request.getClienteNombre() == null || request.getClienteNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (request.getClienteTelefono() == null || request.getClienteTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono del cliente es obligatorio");
        }
        if (request.getClienteCoordenadasX() == null || request.getClienteCoordenadasY() == null) {
            throw new IllegalArgumentException("Las coordenadas del cliente son obligatorias");
        }
        if (request.getProductos() == null || request.getProductos().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe incluir al menos un producto");
        }
    }

    private Pedido toDomain(OrderRequestDto request) {
        List<ProductoPedido> productos = request.getProductos().stream()
                .map(dto -> ProductoPedido.builder()
                        .id(dto.getId())
                        .nombre(dto.getNombre())
                        .precio(dto.getPrecio())
                        .build())
                .toList();

        return Pedido.builder()
                .restauranteId(request.getRestauranteId())
                .clienteNombre(request.getClienteNombre())
                .clienteCoordenadasX(request.getClienteCoordenadasX())
                .clienteCoordenadasY(request.getClienteCoordenadasY())
                .productos(productos)
                .estado(EstadoPedido.PENDIENTE)
                .build();
    }

    private EstadoPedido resolveEstado(String estadoDelivery) {
        if ("ASIGNADO".equalsIgnoreCase(estadoDelivery)) {
            return EstadoPedido.ASIGNADO;
        }
        return EstadoPedido.PENDIENTE;
    }

    private OrderResponseDto toResponse(Pedido pedido, OrderRequestDto request) {
        List<ProductoPedidoDto> productosDto = pedido.getProductos().stream()
                .map(p -> ProductoPedidoDto.builder()
                        .id(p.getId())
                        .nombre(p.getNombre())
                        .precio(p.getPrecio())
                        .build())
                .toList();

        return OrderResponseDto.builder()
                .id(pedido.getId())
                .restauranteId(pedido.getRestauranteId())
                .clienteNombre(pedido.getClienteNombre())
                .clienteCoordenadasX(request.getClienteCoordenadasX())
                .clienteCoordenadasY(request.getClienteCoordenadasY())
                .clienteTelefono(request.getClienteTelefono())
                .productos(productosDto)
                .estado(pedido.getEstado())
                .tiempoEstimado(pedido.getTiempoEstimado())
                .build();
    }
}
