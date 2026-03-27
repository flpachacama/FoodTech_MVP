package com.foodtech.order.application.service;

import com.foodtech.order.domain.exception.RestauranteNotFoundException;
import com.foodtech.order.domain.model.EstadoPedido;
import com.foodtech.order.domain.model.Pedido;
import com.foodtech.order.domain.model.ProductoPedido;
import com.foodtech.order.domain.port.input.OrderUseCase;
import com.foodtech.order.domain.port.output.DeliveryClient;
import com.foodtech.order.domain.port.output.DeliveryClient.DeliveryAssignmentRequest;
import com.foodtech.order.domain.port.output.DeliveryClient.DeliveryAssignmentResponse;
import com.foodtech.order.domain.port.output.PedidoRepository;
import com.foodtech.order.infrastructure.persistence.RestauranteJpaRepository;
import com.foodtech.order.infrastructure.web.dto.OrderRequestDto;
import com.foodtech.order.infrastructure.web.dto.OrderResponseDto;
import com.foodtech.order.infrastructure.web.dto.ProductoPedidoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderApplicationService implements OrderUseCase {

    private final PedidoRepository pedidoRepository;
    private final DeliveryClient deliveryClient;
    private final RestauranteJpaRepository restauranteRepository;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto request) {

        // ── 1. Validar campos obligatorios ───────────────────────────────────
        validateRequest(request);
        log.info("[createOrder] Creando pedido para cliente='{}' restauranteId={}",
                request.getClienteNombre(), request.getRestauranteId());

        // ── 2. Mapear DTO → dominio con estado inicial PENDIENTE ─────────────
        Pedido pedido = toDomain(request);

        // ── 3. Persistir pedido ──────────────────────────────────────────────
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.info("[createOrder] Pedido persistido con id={}", pedidoGuardado.getId());

        // ── 4. Solicitar asignación al delivery-service ──────────────────────
        DeliveryAssignmentResponse deliveryResponse;
        try {
            DeliveryAssignmentRequest deliveryRequest = new DeliveryAssignmentRequest(
                    pedidoGuardado.getId(),
                    request.getRestauranteX(),
                    request.getRestauranteY(),
                    request.getClima() != null ? request.getClima() : "SOLEADO"
            );
            deliveryResponse = deliveryClient.assign(deliveryRequest);
            log.info("[createOrder] Respuesta delivery: estado={} repartidorId={}",
                    deliveryResponse.estado(), deliveryResponse.repartidorId());
        } catch (Exception ex) {
            log.error("[createOrder] Fallo al llamar delivery-service: {}", ex.getMessage());
            throw new IllegalStateException(
                    "Error al comunicarse con el servicio de delivery: " + ex.getMessage(), ex
            );
        }

        // ── 5. Actualizar estado según respuesta de delivery ─────────────────
        EstadoPedido estadoFinal = resolveEstado(deliveryResponse.estado());

        Pedido pedidoActualizado = Pedido.builder()
                .id(pedidoGuardado.getId())
                .restauranteId(pedidoGuardado.getRestauranteId())
                .clienteId(pedidoGuardado.getClienteId())
                .clienteNombre(pedidoGuardado.getClienteNombre())
                .clienteCoordenadasX(pedidoGuardado.getClienteCoordenadasX())
                .clienteCoordenadasY(pedidoGuardado.getClienteCoordenadasY())
                .productos(pedidoGuardado.getProductos())
                .estado(estadoFinal)
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
        
        // Validar que el restaurante existe
        if (!restauranteRepository.existsById(request.getRestauranteId())) {
            throw new RestauranteNotFoundException(request.getRestauranteId());
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
                .clienteId(request.getClienteId())
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
                .clienteId(pedido.getClienteId())
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
