package com.foodtech.infrastructure.web.dto;

import com.foodtech.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderResponseDtoTest {

    private OrderResponseDto buildSample() {
        return OrderResponseDto.builder()
                .id(10L)
                .restauranteId(1L)
                .repartidorId(3L)
                .clienteId(5L)
                .clienteNombre("Ana García")
                .clienteCoordenadasX(3.0)
                .clienteCoordenadasY(4.0)
                .clienteTelefono("600000001")
                .tiempoEstimado(25)
                .estado(EstadoPedido.ASIGNADO)
                .productos(List.of())
                .build();
    }

    @Test
    void builder_conTodosLosCampos_gettersRetornanValoresCorrectos() {
        OrderResponseDto dto = buildSample();

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getRestauranteId()).isEqualTo(1L);
        assertThat(dto.getRepartidorId()).isEqualTo(3L);
        assertThat(dto.getClienteId()).isEqualTo(5L);
        assertThat(dto.getClienteNombre()).isEqualTo("Ana García");
        assertThat(dto.getClienteCoordenadasX()).isEqualTo(3.0);
        assertThat(dto.getClienteCoordenadasY()).isEqualTo(4.0);
        assertThat(dto.getClienteTelefono()).isEqualTo("600000001");
        assertThat(dto.getTiempoEstimado()).isEqualTo(25);
        assertThat(dto.getEstado()).isEqualTo(EstadoPedido.ASIGNADO);
        assertThat(dto.getProductos()).isEmpty();
    }

    @Test
    void allArgsConstructor_conTodosLosCampos_gettersRetornanValoresCorrectos() {
        List<ProductoPedidoDto> productos = List.of();

        OrderResponseDto dto = new OrderResponseDto(20L, 2L, 4L, productos,
                6L, "Luis", 1.0, 2.0, "611111111", 30, EstadoPedido.PENDIENTE);

        assertThat(dto.getId()).isEqualTo(20L);
        assertThat(dto.getEstado()).isEqualTo(EstadoPedido.PENDIENTE);
        assertThat(dto.getClienteNombre()).isEqualTo("Luis");
        assertThat(dto.getTiempoEstimado()).isEqualTo(30);
    }

    @Test
    void setters_modificanCamposCorrectamente() {
        OrderResponseDto dto = new OrderResponseDto();

        dto.setId(99L);
        dto.setRestauranteId(2L);
        dto.setRepartidorId(7L);
        dto.setClienteId(3L);
        dto.setClienteNombre("María");
        dto.setClienteCoordenadasX(5.0);
        dto.setClienteCoordenadasY(6.0);
        dto.setClienteTelefono("633333333");
        dto.setTiempoEstimado(15);
        dto.setEstado(EstadoPedido.CANCELADO);
        dto.setProductos(List.of());

        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
        assertThat(dto.getClienteNombre()).isEqualTo("María");
    }

    @Test
    void equals_dosObjetosConMismosCampos_retornaTrue() {
        OrderResponseDto a = buildSample();
        OrderResponseDto b = buildSample();

        assertThat(a).isEqualTo(b);
    }

    @Test
    void equals_objetosDistintos_retornaFalse() {
        OrderResponseDto a = buildSample();
        OrderResponseDto b = buildSample();
        b.setEstado(EstadoPedido.ENTREGADO);

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toString_noEsNuloYContieneId() {
        OrderResponseDto dto = buildSample();

        assertThat(dto.toString()).isNotNull().contains("10");
    }

    @Test
    void equals_conNull_retornaFalse() {
        assertThat(buildSample().equals(null)).isFalse();
    }

    @Test
    void equals_mismaReferencia_retornaTrue() {
        OrderResponseDto dto = buildSample();
        assertThat(dto.equals(dto)).isTrue();
    }

    @Test
    void equals_diferenteTipo_retornaFalse() {
        assertThat(buildSample().equals("distinto")).isFalse();
    }

    @Test
    void equals_objetoVacioContraObjetoPoblado_retornaFalse() {
        OrderResponseDto empty = new OrderResponseDto();
        OrderResponseDto full = buildSample();
        assertThat(empty.equals(full)).isFalse();
        assertThat(full.equals(empty)).isFalse();
    }

    @Test
    void equals_dosObjetosVacios_retornaTrue() {
        assertThat(new OrderResponseDto().equals(new OrderResponseDto())).isTrue();
    }

    @Test
    void hashCode_objetoVacio_noLanzaExcepcion() {
        assertThat(new OrderResponseDto().hashCode()).isEqualTo(new OrderResponseDto().hashCode());
    }

    @Test
    void builder_sinCampos_retornaObjetoNoNulo() {
        assertThat(OrderResponseDto.builder().build()).isNotNull();
    }
}
