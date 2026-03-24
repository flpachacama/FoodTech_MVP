package com.foodtech.domain.service;

import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.domain.port.output.RepartidorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    @Test
    void obtenerCercanos_NoActivos_returnsEmptyListSingleton() {
        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Collections.emptyList());

        List<Repartidor> result = asignacionService.obtenerRepartidoresCercanos(new Coordenada(0, 0));

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertSame(Collections.emptyList(), result);

        verify(repartidorRepository, times(1)).findByEstado(eq(EstadoRepartidor.ACTIVO));
    }

    @Test
    void obtenerCercanos_OrdenCorrecto_sortsByDistanceAsc() {
        Repartidor r1 = Repartidor.builder().id(1L).nombre("R1").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(10,10)).build();
        Repartidor r2 = Repartidor.builder().id(2L).nombre("R2").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(20,20)).build();
        Repartidor r3 = Repartidor.builder().id(3L).nombre("R3").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(5,5)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Arrays.asList(r1, r2, r3));

        List<Repartidor> result = asignacionService.obtenerRepartidoresCercanos(new Coordenada(0,0));

        assertEquals(3, result.size());
        assertEquals("R3", result.get(0).getNombre());
        assertEquals("R1", result.get(1).getNombre());
        assertEquals("R2", result.get(2).getNombre());

        verify(repartidorRepository, times(1)).findByEstado(eq(EstadoRepartidor.ACTIVO));
    }

    @Test
    void obtenerCercanos_EmpateDistancia_handlesTieGracefully() {
        Repartidor r1 = Repartidor.builder().id(1L).nombre("R1").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(1,0)).build();
        Repartidor r2 = Repartidor.builder().id(2L).nombre("R2").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(0,1)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Arrays.asList(r1, r2));

        List<Repartidor> result = asignacionService.obtenerRepartidoresCercanos(new Coordenada(0,0));

        assertEquals(2, result.size());
        // order is not important in a tie, but both must be present
        assertTrue(result.stream().anyMatch(r -> r.getNombre().equals("R1")));
        assertTrue(result.stream().anyMatch(r -> r.getNombre().equals("R2")));

        verify(repartidorRepository, times(1)).findByEstado(eq(EstadoRepartidor.ACTIVO));
    }

    @Test
    void obtenerCercanos_MismoPunto_zeroDistanceComesFirst() {
        Repartidor r = Repartidor.builder().id(1L).nombre("R").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(10,10)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Collections.singletonList(r));

        List<Repartidor> result = asignacionService.obtenerRepartidoresCercanos(new Coordenada(10,10));

        assertEquals(1, result.size());
        assertEquals("R", result.get(0).getNombre());

        verify(repartidorRepository, times(1)).findByEstado(eq(EstadoRepartidor.ACTIVO));
    }
}
