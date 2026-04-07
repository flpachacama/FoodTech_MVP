package com.foodtech.application.service;

import com.foodtech.domain.model.EstadoPedido;
import com.foodtech.domain.model.Pedido;
import com.foodtech.domain.port.output.DeliveryClient;
import com.foodtech.domain.port.output.DeliveryClient.DeliveryAssignmentResponse;
import com.foodtech.domain.port.output.PedidoRepository;
import com.foodtech.infrastructure.persistence.RestauranteJpaRepository;
import com.foodtech.infrastructure.web.dto.OrderRequestDto;
import com.foodtech.infrastructure.web.dto.OrderResponseDto;
import com.foodtech.infrastructure.web.dto.ProductoPedidoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private RestauranteJpaRepository restauranteJpaRepository;

    @InjectMocks
    private OrderApplicationService service;

    private OrderRequestDto requestBase;

    @BeforeEach
    void setUp() {
        lenient().when(restauranteJpaRepository.existsById(anyLong())).thenReturn(true);
        requestBase = OrderRequestDto.builder()
                .restauranteId(10L)
                .restauranteX(5.0)
                .restauranteY(8.0)
                .clima("SOLEADO")
                .clienteNombre("Ana García")
                .clienteTelefono("600000001")
                .clienteCoordenadasX(1.0)
                .clienteCoordenadasY(2.0)
                .productos(List.of(ProductoPedidoDto.builder()
                        .id(1L).nombre("Hamburguesa").precio(BigDecimal.valueOf(8.50)).build()))
                .build();
    }

    // ── Caso 1: flujo completo, delivery responde ASIGNADO ────────────────────
    @Test
    void createOrder_whenDeliveryAsigna_returnsEstadoAsignado() {
        Pedido pedidoGuardado = Pedido.builder()
                .id(42L)
                .restauranteId(10L)
                .clienteNombre("Ana García")
                .clienteCoordenadasX(1.0)
                .clienteCoordenadasY(2.0)
                .estado(EstadoPedido.PENDIENTE)
                .productos(List.of())
                .build();

        when(pedidoRepository.save(any())).thenReturn(pedidoGuardado);
        when(deliveryClient.assign(any()))
                .thenReturn(new DeliveryAssignmentResponse(42L, "ASIGNADO", 7L, "Carlos", 15));

        OrderResponseDto response = service.createOrder(requestBase);

        assertThat(response.getEstado()).isEqualTo(EstadoPedido.ASIGNADO);
        assertThat(response.getId()).isEqualTo(42L);
        verify(pedidoRepository, times(2)).save(any()); // guardado + actualizado
    }

    // ── Caso 2: delivery responde PENDIENTE (sin repartidor disponible) ────────
    @Test
    void createOrder_whenDeliveryPendiente_returnsEstadoPendiente() {
        Pedido pedidoGuardado = Pedido.builder()
                .id(43L)
                .restauranteId(10L)
                .clienteNombre("Ana García")
                .clienteCoordenadasX(1.0)
                .clienteCoordenadasY(2.0)
                .estado(EstadoPedido.PENDIENTE)
                .productos(List.of())
                .build();

        when(pedidoRepository.save(any())).thenReturn(pedidoGuardado);
        when(deliveryClient.assign(any()))
                .thenReturn(new DeliveryAssignmentResponse(43L, "PENDIENTE", null, null, null));

        OrderResponseDto response = service.createOrder(requestBase);

        assertThat(response.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
    }

    // ── Caso 3: delivery-service lanza excepción → IllegalStateException ──────
    @Test
    void createOrder_whenDeliveryFails_throwsIllegalStateException() {
        Pedido pedidoGuardado = Pedido.builder()
                .id(44L)
                .restauranteId(10L)
                .clienteNombre("Ana García")
                .clienteCoordenadasX(1.0)
                .clienteCoordenadasY(2.0)
                .estado(EstadoPedido.PENDIENTE)
                .productos(List.of())
                .build();

        when(pedidoRepository.save(any())).thenReturn(pedidoGuardado);
        when(deliveryClient.assign(any()))
                .thenThrow(new RuntimeException("Connection refused"));

        assertThatThrownBy(() -> service.createOrder(requestBase))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Error al comunicarse con el servicio de delivery");
    }

    // ── Caso 4: restauranteId nulo → IllegalArgumentException ─────────────────
    @Test
    void createOrder_whenRestauranteIdNull_throwsIllegalArgumentException() {
        OrderRequestDto requestSinRestaurante = OrderRequestDto.builder()
                .restauranteId(null)
                .clienteNombre("Ana García")
                .clienteTelefono("600000001")
                .clienteCoordenadasX(1.0)
                .clienteCoordenadasY(2.0)
                .productos(List.of(ProductoPedidoDto.builder()
                        .id(1L).nombre("Burger").precio(BigDecimal.ONE).build()))
                .build();

        assertThatThrownBy(() -> service.createOrder(requestSinRestaurante))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("restauranteId");

        verifyNoInteractions(pedidoRepository, deliveryClient);
    }

    // ── Caso 5: lista de productos vacía → IllegalArgumentException ────────────
    @Test
    void createOrder_whenProductosEmpty_throwsIllegalArgumentException() {
        OrderRequestDto requestSinProductos = OrderRequestDto.builder()
                .restauranteId(10L)
                .clienteNombre("Ana García")
                .clienteTelefono("600000001")
                .clienteCoordenadasX(1.0)
                .clienteCoordenadasY(2.0)
                .productos(List.of())
                .build();

        assertThatThrownBy(() -> service.createOrder(requestSinProductos))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("producto");

        verifyNoInteractions(pedidoRepository, deliveryClient);
    }

    // ── Caso 6: clima null usa fallback "SOLEADO" ──────────────────────────────
    @Test
    void createOrder_whenClimaNull_usesFallbackSoleado() {
        requestBase.setClima(null);

        Pedido pedidoGuardado = Pedido.builder()
                .id(45L).restauranteId(10L).clienteNombre("Ana García")
                .clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.PENDIENTE).productos(List.of()).build();

        when(pedidoRepository.save(any())).thenReturn(pedidoGuardado);
        when(deliveryClient.assign(any()))
                .thenReturn(new DeliveryAssignmentResponse(45L, "ASIGNADO", 1L, "Pedro", 20));

        service.createOrder(requestBase);

        ArgumentCaptor<DeliveryClient.DeliveryAssignmentRequest> captor =
                ArgumentCaptor.forClass(DeliveryClient.DeliveryAssignmentRequest.class);
        verify(deliveryClient).assign(captor.capture());
        assertThat(captor.getValue().clima()).isEqualTo("SOLEADO");
    }
}
