package com.foodtech.domain.service;

import com.foodtech.domain.model.Clima;
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
class FiltroClimaTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    @Test
    void filtroClima_LluviaFuerte_SoloAutos() {
        Repartidor rMoto = Repartidor.builder().id(1L).nombre("MOTO").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(0,0)).build();
        Repartidor rBici = Repartidor.builder().id(2L).nombre("BICI").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(1,1)).build();
        Repartidor rAuto = Repartidor.builder().id(3L).nombre("AUTO").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(2,2)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Arrays.asList(rMoto, rBici, rAuto));

        List<Repartidor> result = asignacionService.obtenerRepartidoresCercanos(new Coordenada(0,0), Clima.LLUVIA_FUERTE);

        assertEquals(1, result.size());
        assertEquals(TipoVehiculo.AUTO, result.get(0).getVehiculo());
        verify(repartidorRepository, times(1)).findByEstado(eq(EstadoRepartidor.ACTIVO));
    }

    @Test
    void filtroClima_LluviaSuave_ExcluyeBici() {
        Repartidor rMoto = Repartidor.builder().id(1L).nombre("MOTO").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(0,0)).build();
        Repartidor rBici = Repartidor.builder().id(2L).nombre("BICI").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(1,1)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Arrays.asList(rMoto, rBici));

        List<Repartidor> result = asignacionService.obtenerRepartidoresCercanos(new Coordenada(0,0), Clima.LLUVIA_SUAVE);

        assertEquals(1, result.size());
        assertEquals(TipoVehiculo.MOTO, result.get(0).getVehiculo());
        verify(repartidorRepository, times(1)).findByEstado(eq(EstadoRepartidor.ACTIVO));
    }

    @Test
    void filtroClima_Soleado_TodosAptos() {
        Repartidor r1 = Repartidor.builder().id(1L).nombre("R1").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(0,0)).build();
        Repartidor r2 = Repartidor.builder().id(2L).nombre("R2").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.MOTO).ubicacion(new Coordenada(1,1)).build();
        Repartidor r3 = Repartidor.builder().id(3L).nombre("R3").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.AUTO).ubicacion(new Coordenada(2,2)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Arrays.asList(r1, r2, r3));

        List<Repartidor> result = asignacionService.obtenerRepartidoresCercanos(new Coordenada(0,0), Clima.SOLEADO);

        assertEquals(3, result.size());
        verify(repartidorRepository, times(1)).findByEstado(eq(EstadoRepartidor.ACTIVO));
    }

    @Test
    void filtroClima_SinCandidatosAptos_returnsEmpty() {
        Repartidor bici1 = Repartidor.builder().id(1L).nombre("B1").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(5,5)).build();
        Repartidor bici2 = Repartidor.builder().id(2L).nombre("B2").estado(EstadoRepartidor.ACTIVO).vehiculo(TipoVehiculo.BICICLETA).ubicacion(new Coordenada(6,6)).build();

        when(repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO)).thenReturn(Arrays.asList(bici1, bici2));

        List<Repartidor> result = asignacionService.obtenerRepartidoresCercanos(new Coordenada(0,0), Clima.LLUVIA_FUERTE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repartidorRepository, times(1)).findByEstado(eq(EstadoRepartidor.ACTIVO));
    }
}
