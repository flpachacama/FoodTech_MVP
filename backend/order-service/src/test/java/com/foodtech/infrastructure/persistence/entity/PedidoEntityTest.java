package com.foodtech.infrastructure.persistence.entity;

import com.foodtech.domain.model.EstadoPedido;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoEntityTest {

    @Test
    void debeConstruirPedidoEntity_conConstructorYtenerCampos() {
        PedidoEntity e = new PedidoEntity(1L, EstadoPedido.PENDIENTE, 2L, null, 3L, "Juan", 1, 2, 30, "[]");

        assertEquals(1L, e.getId());
        assertEquals(EstadoPedido.PENDIENTE, e.getEstado());
        assertEquals("Juan", e.getClienteNombre());
        assertEquals("[]", e.getProductos());
    }

    @Test
    void debePermitirValoresNulos_conConstructorPorDefecto() {
        PedidoEntity e = new PedidoEntity();
        assertNull(e.getId());
        assertNull(e.getEstado());
    }

}
