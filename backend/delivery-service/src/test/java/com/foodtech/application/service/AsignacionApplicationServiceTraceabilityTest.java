package com.foodtech.application.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.domain.port.input.RepartidorUseCase;
import com.foodtech.domain.port.output.RepartidorRepository;
import com.foodtech.domain.service.AsignacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignacionApplicationServiceTraceabilityTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @Mock
    private AsignacionService asignacionService;

    @Mock
    private RepartidorUseCase repartidorUseCase;

    @InjectMocks
    private AsignacionApplicationService service;

    // HU5 - Asignar pedido automaticamente
    @Test
    @DisplayName("TC-013 - Asignacion exitosa selecciona mejor candidato y cambia estado")
    void shouldAssignBestCandidate_TC013() {
        // Arrange
        Coordenada restaurante = new Coordenada(5, 5);
        Repartidor candidato = createRepartidor(1L, "Rider-1", EstadoRepartidor.ACTIVO, TipoVehiculo.MOTO, 6, 5);
        Repartidor asignado = createRepartidor(1L, "Rider-1", EstadoRepartidor.EN_ENTREGA, TipoVehiculo.MOTO, 6, 5);

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(List.of(candidato));
        when(asignacionService.priorizarRepartidores(anyList(), eq(restaurante), eq(Clima.SOLEADO)))
                .thenReturn(List.of(candidato));
        when(repartidorUseCase.cambiarEstado(1L, EstadoRepartidor.EN_ENTREGA)).thenReturn(asignado);

        // Act
        Repartidor result = service.asignarRepartidor(restaurante, Clima.SOLEADO);

        // Assert
        assertEquals(EstadoRepartidor.EN_ENTREGA, result.getEstado());
        assertEquals(1L, result.getId());
    }

    // HU5 - Asignar pedido automaticamente
    @Test
    @DisplayName("TC-014 - Sin candidatos validos retorna null y no cambia estados")
    void shouldReturnNullWhenNoCandidates_TC014() {
        // Arrange
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Collections.emptyList());

        // Act
        Repartidor result = service.asignarRepartidor(new Coordenada(1, 1), Clima.LLUVIA_FUERTE);

        // Assert
        assertNull(result);
        verify(asignacionService, never()).priorizarRepartidores(anyList(), any(), any());
        verify(repartidorUseCase, never()).cambiarEstado(any(), any());
    }

    private Repartidor createRepartidor(Long id,
                                        String nombre,
                                        EstadoRepartidor estado,
                                        TipoVehiculo vehiculo,
                                        int x,
                                        int y) {
        return Repartidor.builder()
                .id(id)
                .nombre(nombre)
                .estado(estado)
                .vehiculo(vehiculo)
                .ubicacion(new Coordenada(x, y))
                .build();
    }
}
