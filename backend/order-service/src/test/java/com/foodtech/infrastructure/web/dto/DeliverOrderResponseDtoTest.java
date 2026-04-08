package com.foodtech.infrastructure.web.dto;

import com.foodtech.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliverOrderResponseDtoTest {

    @Test
    void builder_creaObjetoConCamposCorrectos() {
        DeliverOrderResponseDto dto = DeliverOrderResponseDto.builder()
                .id(1L)
                .estado(EstadoPedido.ENTREGADO)
                .mensaje("Pedido entregado")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(EstadoPedido.ENTREGADO, dto.getEstado());
        assertEquals("Pedido entregado", dto.getMensaje());
    }

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        DeliverOrderResponseDto dto = new DeliverOrderResponseDto(2L, EstadoPedido.ENTREGADO, "Entregado OK");

        assertEquals(2L, dto.getId());
        assertEquals(EstadoPedido.ENTREGADO, dto.getEstado());
        assertEquals("Entregado OK", dto.getMensaje());
    }

    @Test
    void noArgsConstructorYSetters_asignanValoresCorrectamente() {
        DeliverOrderResponseDto dto = new DeliverOrderResponseDto();
        dto.setId(3L);
        dto.setEstado(EstadoPedido.ENTREGADO);
        dto.setMensaje("Entregado al cliente");

        assertEquals(3L, dto.getId());
        assertEquals(EstadoPedido.ENTREGADO, dto.getEstado());
        assertEquals("Entregado al cliente", dto.getMensaje());
    }

    @Test
    void equals_dosObjetosIguales_retornaTrue() {
        DeliverOrderResponseDto a = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg");
        DeliverOrderResponseDto b = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_objetosDiferentes_retornaFalse() {
        DeliverOrderResponseDto a = new DeliverOrderResponseDto(1L, EstadoPedido.ENTREGADO, "msg1");
        DeliverOrderResponseDto b = new DeliverOrderResponseDto(2L, EstadoPedido.PENDIENTE, "msg2");

        assertNotEquals(a, b);
    }

    @Test
    void toString_contieneValoresClave() {
        DeliverOrderResponseDto dto = new DeliverOrderResponseDto(5L, EstadoPedido.ENTREGADO, "entregado");

        String str = dto.toString();

        assertTrue(str.contains("5"));
        assertTrue(str.contains("ENTREGADO"));
        assertTrue(str.contains("entregado"));
    }
}
