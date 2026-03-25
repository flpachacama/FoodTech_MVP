package com.foodtech.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsignacionResponseDTOTest {

    @Test
    void debeConstruirYLeerCampos_cuandoSeUsaBuilder() {
        AsignacionResponseDTO dto = AsignacionResponseDTO.builder()
                .pedidoId(1L)
                .estado("ASIGNADO")
                .repartidorId(2L)
                .nombreRepartidor("Carlos")
                .build();

        assertThat(dto).isNotNull();
        assertThat(dto.getPedidoId()).isEqualTo(1L);
        assertThat(dto.getEstado()).isEqualTo("ASIGNADO");
        assertThat(dto.getRepartidorId()).isEqualTo(2L);
        assertThat(dto.getNombreRepartidor()).isEqualTo("Carlos");
    }
}
