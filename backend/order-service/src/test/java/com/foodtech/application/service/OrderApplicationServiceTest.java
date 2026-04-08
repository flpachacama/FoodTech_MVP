package com.foodtech.application.service;

import com.foodtech.domain.exception.PedidoCancelException;
import com.foodtech.domain.exception.PedidoDeliverException;
import com.foodtech.domain.exception.PedidoNotFoundException;
import com.foodtech.domain.model.EstadoPedido;
import com.foodtech.domain.model.Pedido;
import com.foodtech.domain.port.output.DeliveryClient;
import com.foodtech.domain.port.output.DeliveryClient.DeliveryAssignmentResponse;
import com.foodtech.domain.port.output.PedidoRepository;
import com.foodtech.domain.service.TiempoDeliveryCalculator;
import com.foodtech.infrastructure.persistence.RestauranteJpaRepository;
import com.foodtech.infrastructure.web.dto.CancelOrderResponseDto;
import com.foodtech.infrastructure.web.dto.DeliverOrderResponseDto;
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
import java.util.Optional;

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

    @Mock
    private TiempoDeliveryCalculator tiempoCalculator;

    @InjectMocks
    private OrderApplicationService service;

    private OrderRequestDto requestBase;

    @BeforeEach
    void setUp() {
        lenient().when(restauranteJpaRepository.existsById(anyLong())).thenReturn(true);
        lenient().when(tiempoCalculator.calcularMinutos(any(), any(), any(), any())).thenReturn(5);
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

    // ── Caso 7: repartidor con pedido activo ──────────────────────────────────
    @Test
    void getOrderByRepartidorId_cuandoPedidoActivo_retornaDto() {
        Pedido pedidoActivo = Pedido.builder()
                .id(55L).restauranteId(10L).repartidorId(1L)
                .clienteNombre("Juan").clienteCoordenadasX(-74.06).clienteCoordenadasY(4.64)
                .estado(EstadoPedido.ASIGNADO).tiempoEstimado(25).productos(List.of()).build();

        when(pedidoRepository.findPedidoActivoByRepartidorId(1L)).thenReturn(Optional.of(pedidoActivo));

        OrderResponseDto response = service.getOrderByRepartidorId(1L);

        assertThat(response.getId()).isEqualTo(55L);
        assertThat(response.getRepartidorId()).isEqualTo(1L);
        assertThat(response.getClienteNombre()).isEqualTo("Juan");
        assertThat(response.getTiempoEstimado()).isEqualTo(25);
        assertThat(response.getEstado()).isEqualTo(EstadoPedido.ASIGNADO);
    }

    // ── Caso 8: repartidor sin pedido activo ─────────────────────────────────
    @Test
    void getOrderByRepartidorId_cuandoSinPedidoActivo_lanzaNotFoundException() {
        when(pedidoRepository.findPedidoActivoByRepartidorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOrderByRepartidorId(99L))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── Caso 9: cancelOrder — pedido ASIGNADO con repartidor → cancela y libera ──
    @Test
    void cancelOrder_conPedidoAsignadoYRepartidor_cancelaYLiberaRepartidor() {
        Pedido pedidoAsignado = Pedido.builder()
                .id(60L).restauranteId(10L).repartidorId(3L)
                .clienteNombre("Luis").clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.ASIGNADO).productos(List.of()).build();

        when(pedidoRepository.findById(60L)).thenReturn(Optional.of(pedidoAsignado));
        when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CancelOrderResponseDto response = service.cancelOrder(60L);

        assertThat(response.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
        assertThat(response.getMensaje()).contains("cancelado");
        verify(deliveryClient).releaseRepartidor(3L, "CANCELADO");
        verify(pedidoRepository).save(any());
    }

    // ── Caso 10: cancelOrder — pedido PENDIENTE sin repartidor → cancela sin delivery ──
    @Test
    void cancelOrder_conPedidoPendienteSinRepartidor_cancelaSinLlamarDelivery() {
        Pedido pedidoPendiente = Pedido.builder()
                .id(61L).restauranteId(10L).repartidorId(null)
                .clienteNombre("María").clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.PENDIENTE).productos(List.of()).build();

        when(pedidoRepository.findById(61L)).thenReturn(Optional.of(pedidoPendiente));
        when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CancelOrderResponseDto response = service.cancelOrder(61L);

        assertThat(response.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
        verifyNoInteractions(deliveryClient);
    }

    // ── Caso 11: cancelOrder — pedido no encontrado → PedidoNotFoundException ──
    @Test
    void cancelOrder_cuandoPedidoNoEncontrado_lanzaNotFoundException() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelOrder(999L))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── Caso 12: cancelOrder — estado ENTREGADO → PedidoCancelException ────────
    @Test
    void cancelOrder_cuandoPedidoEntregado_lanzaCancelException() {
        Pedido pedidoEntregado = Pedido.builder()
                .id(62L).restauranteId(10L).repartidorId(null)
                .clienteNombre("Carlos").clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.ENTREGADO).productos(List.of()).build();

        when(pedidoRepository.findById(62L)).thenReturn(Optional.of(pedidoEntregado));

        assertThatThrownBy(() -> service.cancelOrder(62L))
                .isInstanceOf(PedidoCancelException.class)
                .hasMessageContaining("entregado");
    }

    // ── Caso 13: cancelOrder — estado CANCELADO → PedidoCancelException ────────
    @Test
    void cancelOrder_cuandoPedidoCancelado_lanzaCancelException() {
        Pedido pedidoCancelado = Pedido.builder()
                .id(63L).restauranteId(10L).repartidorId(null)
                .clienteNombre("Elena").clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.CANCELADO).productos(List.of()).build();

        when(pedidoRepository.findById(63L)).thenReturn(Optional.of(pedidoCancelado));

        assertThatThrownBy(() -> service.cancelOrder(63L))
                .isInstanceOf(PedidoCancelException.class)
                .hasMessageContaining("cancelado");
    }

    // ── Caso 14: deliverOrder — pedido ASIGNADO → ENTREGADO y libera repartidor ─
    @Test
    void deliverOrder_conPedidoAsignado_marcaEntregadoYLiberaRepartidor() {
        Pedido pedidoAsignado = Pedido.builder()
                .id(70L).restauranteId(10L).repartidorId(5L)
                .clienteNombre("Rosa").clienteCoordenadasX(3.0).clienteCoordenadasY(4.0)
                .estado(EstadoPedido.ASIGNADO).tiempoEstimado(30).productos(List.of()).build();

        when(pedidoRepository.findById(70L)).thenReturn(Optional.of(pedidoAsignado));
        when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DeliverOrderResponseDto response = service.deliverOrder(70L);

        assertThat(response.getEstado()).isEqualTo(EstadoPedido.ENTREGADO);
        assertThat(response.getMensaje()).contains("entregado");
        verify(deliveryClient).releaseRepartidor(5L, "ENTREGADO");
        verify(pedidoRepository).save(any());
    }

    // ── Caso 15: deliverOrder — estado PENDIENTE → PedidoDeliverException ──────
    @Test
    void deliverOrder_cuandoPedidoPendiente_lanzaDeliverException() {
        Pedido pedidoPendiente = Pedido.builder()
                .id(71L).restauranteId(10L).repartidorId(null)
                .clienteNombre("Tomás").clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.PENDIENTE).productos(List.of()).build();

        when(pedidoRepository.findById(71L)).thenReturn(Optional.of(pedidoPendiente));

        assertThatThrownBy(() -> service.deliverOrder(71L))
                .isInstanceOf(PedidoDeliverException.class)
                .hasMessageContaining("repartidor asignado");
    }

    // ── Caso 16: deliverOrder — estado ya ENTREGADO → PedidoDeliverException ───
    @Test
    void deliverOrder_cuandoPedidoYaEntregado_lanzaDeliverException() {
        Pedido pedidoEntregado = Pedido.builder()
                .id(72L).restauranteId(10L).repartidorId(6L)
                .clienteNombre("Sara").clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.ENTREGADO).productos(List.of()).build();

        when(pedidoRepository.findById(72L)).thenReturn(Optional.of(pedidoEntregado));

        assertThatThrownBy(() -> service.deliverOrder(72L))
                .isInstanceOf(PedidoDeliverException.class)
                .hasMessageContaining("ya ha sido entregado");
    }

    // ── Caso 17: deliverOrder — estado CANCELADO → PedidoDeliverException ──────
    @Test
    void deliverOrder_cuandoPedidoCancelado_lanzaDeliverException() {
        Pedido pedidoCancelado = Pedido.builder()
                .id(73L).restauranteId(10L).repartidorId(null)
                .clienteNombre("Pablo").clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.CANCELADO).productos(List.of()).build();

        when(pedidoRepository.findById(73L)).thenReturn(Optional.of(pedidoCancelado));

        assertThatThrownBy(() -> service.deliverOrder(73L))
                .isInstanceOf(PedidoDeliverException.class)
                .hasMessageContaining("cancelado");
    }

    // ── Caso 18: deliverOrder — pedido no encontrado → PedidoNotFoundException ─
    @Test
    void deliverOrder_cuandoPedidoNoEncontrado_lanzaNotFoundException() {
        when(pedidoRepository.findById(998L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deliverOrder(998L))
                .isInstanceOf(PedidoNotFoundException.class)
                .hasMessageContaining("998");
    }

    // ── Caso 19: createOrder — clienteNombre blank → IllegalArgumentException ──
    @Test
    void createOrder_cuandoClienteNombreBlank_lanzaIllegalArgument() {
        OrderRequestDto requestNombreBlank = OrderRequestDto.builder()
                .restauranteId(10L)
                .clienteNombre("   ")
                .clienteTelefono("600000001")
                .clienteCoordenadasX(1.0)
                .clienteCoordenadasY(2.0)
                .productos(List.of(ProductoPedidoDto.builder()
                        .id(1L).nombre("Burger").precio(BigDecimal.ONE).build()))
                .build();

        assertThatThrownBy(() -> service.createOrder(requestNombreBlank))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre del cliente");

        verifyNoInteractions(pedidoRepository, deliveryClient);
    }

    // ── Caso 20: createOrder — clienteTelefono null → IllegalArgumentException ─
    @Test
    void createOrder_cuandoTelefonoNull_lanzaIllegalArgument() {
        OrderRequestDto requestSinTelefono = OrderRequestDto.builder()
                .restauranteId(10L)
                .clienteNombre("Ana García")
                .clienteTelefono(null)
                .clienteCoordenadasX(1.0)
                .clienteCoordenadasY(2.0)
                .productos(List.of(ProductoPedidoDto.builder()
                        .id(1L).nombre("Burger").precio(BigDecimal.ONE).build()))
                .build();

        assertThatThrownBy(() -> service.createOrder(requestSinTelefono))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("teléfono");

        verifyNoInteractions(pedidoRepository, deliveryClient);
    }

    // ── Caso 21: createOrder — coordenadasX null → IllegalArgumentException ────
    @Test
    void createOrder_cuandoCoordenadasNull_lanzaIllegalArgument() {
        OrderRequestDto requestSinCoordenadas = OrderRequestDto.builder()
                .restauranteId(10L)
                .clienteNombre("Ana García")
                .clienteTelefono("600000001")
                .clienteCoordenadasX(null)
                .clienteCoordenadasY(2.0)
                .productos(List.of(ProductoPedidoDto.builder()
                        .id(1L).nombre("Burger").precio(BigDecimal.ONE).build()))
                .build();

        assertThatThrownBy(() -> service.createOrder(requestSinCoordenadas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("coordenadas");

        verifyNoInteractions(pedidoRepository, deliveryClient);
    }

    // ── Caso 22: createOrder — tiempoEstimado null en delivery → usa cero ──────
    @Test
    void createOrder_cuandoTiempoEstimadoNullEnDelivery_usaCero() {
        Pedido pedidoGuardado = Pedido.builder()
                .id(80L).restauranteId(10L).clienteNombre("Ana García")
                .clienteCoordenadasX(1.0).clienteCoordenadasY(2.0)
                .estado(EstadoPedido.PENDIENTE).productos(List.of()).build();

        when(pedidoRepository.save(any())).thenReturn(pedidoGuardado);
        when(deliveryClient.assign(any()))
                .thenReturn(new DeliveryAssignmentResponse(80L, "ASIGNADO", 2L, "Jorge", null));

        OrderResponseDto response = service.createOrder(requestBase);

        // tiempoEstimado null del delivery → contribución es 0; tiempoRestauranteCliente = 5 (mock)
        assertThat(response.getTiempoEstimado()).isEqualTo(5);
    }
}
