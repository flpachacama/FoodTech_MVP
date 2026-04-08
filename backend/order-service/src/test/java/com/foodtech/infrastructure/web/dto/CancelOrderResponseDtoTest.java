package com.foodtech.infrastructure.web.dto;

import com.foodtech.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancelOrderResponseDtoTest {

    @Test
    void builder_creaObjetoConCamposCorrectos() {
        CancelOrderResponseDto dto = CancelOrderResponseDto.builder()
                .id(1L)
                .estado(EstadoPedido.CANCELADO)
                .mensaje("Pedido cancelado")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(EstadoPedido.CANCELADO, dto.getEstado());
        assertEquals("Pedido cancelado", dto.getMensaje());
    }

    @Test
    void allArgsConstructor_inicializaTodosLosCampos() {
        CancelOrderResponseDto dto = new CancelOrderResponseDto(2L, EstadoPedido.CANCELADO, "Cancelado OK");

        assertEquals(2L, dto.getId());
        assertEquals(EstadoPedido.CANCELADO, dto.getEstado());
        assertEquals("Cancelado OK", dto.getMensaje());
    }

    @Test
    void noArgsConstructorYSetters_asignanValoresCorrectamente() {
        CancelOrderResponseDto dto = new CancelOrderResponseDto();
        dto.setId(3L);
        dto.setEstado(EstadoPedido.CANCELADO);
        dto.setMensaje("Cancelado por customer");

        assertEquals(3L, dto.getId());
        assertEquals(EstadoPedido.CANCELADO, dto.getEstado());
        assertEquals("Cancelado por customer", dto.getMensaje());
    }

    @Test
    void equals_dosObjetosIguales_retornaTrue() {
        CancelOrderResponseDto a = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");
        CancelOrderResponseDto b = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_objetosDiferentes_retornaFalse() {
        CancelOrderResponseDto a = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "msg1");
        CancelOrderResponseDto b = new CancelOrderResponseDto(2L, EstadoPedido.PENDIENTE, "msg2");

        assertNotEquals(a, b);
    }

    @Test
    void toString_contieneValoresClave() {
        CancelOrderResponseDto dto = new CancelOrderResponseDto(1L, EstadoPedido.CANCELADO, "test");

        String str = dto.toString();

        assertTrue(str.contains("1"));
        assertTrue(str.contains("CANCELADO"));
        assertTrue(str.contains("test"));
    }
}
