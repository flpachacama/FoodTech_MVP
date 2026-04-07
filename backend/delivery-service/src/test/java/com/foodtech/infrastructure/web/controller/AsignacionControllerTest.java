package com.foodtech.infrastructure.web.controller;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.domain.service.AsignacionService;
import com.foodtech.domain.port.input.AsignacionUseCase;
import com.foodtech.infrastructure.web.dto.AsignacionRequestDTO;
import com.foodtech.infrastructure.web.dto.AsignacionResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignacionControllerTest {

        @Mock
        private AsignacionUseCase asignacionUseCase;

        @Mock
        private com.foodtech.application.service.AsignacionApplicationService asignacionApplicationService;

        @Mock
        private com.foodtech.domain.port.input.RepartidorUseCase repartidorUseCase;

        @Mock
        private AsignacionService asignacionService;

    @InjectMocks
    private AsignacionController controller;

    @Test
    void debeRetornarASIGNADO_cuandoHayCandidatos() {
        AsignacionRequestDTO request = AsignacionRequestDTO.builder()
                .pedidoId(1L)
                .restauranteX(25)
                .restauranteY(40)
                .clima(Clima.SOLEADO.name())
                .build();

        Repartidor candidato = Repartidor.builder()
                .id(1L)
                .nombre("Carlos Mendoza")
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.MOTO)
                .ubicacion(new Coordenada(25, 40))
                .build();

        when(asignacionApplicationService.asignarRepartidor(eq(new Coordenada(25, 40)), eq(Clima.SOLEADO)))
                .thenReturn(candidato);
        when(asignacionService.calcularTiempoEstimadoMinutos(candidato, new Coordenada(25, 40)))
                .thenReturn(12);

        AsignacionResponseDTO resp = controller.asignarRepartidor(request);

        assertThat(resp).isNotNull();
        assertThat(resp.getEstado()).isEqualTo("ASIGNADO");
        assertThat(resp.getRepartidorId()).isEqualTo(1L);
        assertThat(resp.getNombreRepartidor()).isEqualTo("Carlos Mendoza");
    }

    @Test
    void debeRetornarPENDIENTE_cuandoNoHayCandidatos() {
        AsignacionRequestDTO request = AsignacionRequestDTO.builder()
                .pedidoId(2L)
                .restauranteX(10)
                .restauranteY(10)
                .clima(Clima.LLUVIA_SUAVE.name())
                .build();

        when(asignacionApplicationService.asignarRepartidor(eq(new Coordenada(10, 10)), eq(Clima.LLUVIA_SUAVE)))
                .thenReturn(null);

        AsignacionResponseDTO resp = controller.asignarRepartidor(request);

        assertThat(resp).isNotNull();
        assertThat(resp.getEstado()).isEqualTo("PENDIENTE");
        assertThat(resp.getRepartidorId()).isNull();
        assertThat(resp.getNombreRepartidor()).isNull();
    }

    @Test
    void debeLlamarUseCaseConClimaNull_cuandoRequestNoTieneClima() {
        AsignacionRequestDTO request = AsignacionRequestDTO.builder()
                .pedidoId(3L)
                .restauranteX(5)
                .restauranteY(5)
                .clima(null)
                .build();

        when(asignacionApplicationService.asignarRepartidor(eq(new Coordenada(5, 5)), isNull()))
                .thenReturn(null);

        AsignacionResponseDTO resp = controller.asignarRepartidor(request);

        assertThat(resp.getEstado()).isEqualTo("PENDIENTE");
        verify(asignacionApplicationService).asignarRepartidor(eq(new Coordenada(5, 5)), isNull());
    }

    // HU3 - Aplicar restricciones por clima
    @Test
    void debeLanzarIllegalArgumentException_cuandoElClimaNoEsValido_TC009() {
        AsignacionRequestDTO request = AsignacionRequestDTO.builder()
                .pedidoId(4L)
                .restauranteX(5)
                .restauranteY(5)
                .clima("TORMENTA_EXTREMA")
                .build();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                controller.asignarRepartidor(request)
        );
    }
}
