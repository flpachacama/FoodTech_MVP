package com.foodtech.infrastructure.persistence.adapter;

import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
import com.foodtech.infrastructure.persistence.entity.RepartidorEntity;
import com.foodtech.infrastructure.persistence.repository.JpaRepartidorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepartidorPersistenceAdapterTest {

    @Mock
    private JpaRepartidorRepository jpaRepartidorRepository;

    @InjectMocks
    private RepartidorPersistenceAdapter adapter;

    @Test
    void findById_shouldConvertEntityToDomain_withCoordenada() {
        Long id = 1L;
        RepartidorEntity entity = RepartidorEntity.builder()
                .id(id)
                .nombre("Juan Perez")
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.MOTO)
                .x(5)
                .y(7)
                .build();

        when(jpaRepartidorRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<Repartidor> result = adapter.findById(id);

        assertTrue(result.isPresent());
        Repartidor repartidor = result.get();
        assertEquals(id, repartidor.getId());
        assertEquals("Juan Perez", repartidor.getNombre());
        Coordenada ubicacion = repartidor.getUbicacion();
        assertNotNull(ubicacion);
        assertEquals(5, ubicacion.x());
        assertEquals(7, ubicacion.y());
    }

    @Test
    void save_shouldCallJpaSave_withEntityHavingSeparatedXY() {
        Repartidor domain = Repartidor.builder()
                .id(null)
                .nombre("Ana Lopez")
                .estado(EstadoRepartidor.EN_ENTREGA)
                .vehiculo(TipoVehiculo.BICICLETA)
                .ubicacion(new Coordenada(12, 34))
                .build();

        when(jpaRepartidorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Repartidor saved = adapter.save(domain);

        ArgumentCaptor<RepartidorEntity> captor = ArgumentCaptor.forClass(RepartidorEntity.class);
        verify(jpaRepartidorRepository).save(captor.capture());

        RepartidorEntity persisted = captor.getValue();
        assertEquals("Ana Lopez", persisted.getNombre());
        assertEquals(12, persisted.getX());
        assertEquals(34, persisted.getY());

        assertNotNull(saved);
        assertEquals("Ana Lopez", saved.getNombre());
        assertEquals(12, saved.getUbicacion().x());
        assertEquals(34, saved.getUbicacion().y());
    }

    @Test
    void findById_NotFound_shouldReturnEmptyOptional() {
        Long id = 99L;
        when(jpaRepartidorRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Repartidor> result = adapter.findById(id);

        assertFalse(result.isPresent());
    }

    @Test
    void save_BoundaryCoordinates_shouldPersistExtremes() {
        // lower boundary
        Repartidor low = Repartidor.builder()
                .id(null)
                .nombre("Low")
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.BICICLETA)
                .ubicacion(new Coordenada(0, 0))
                .build();

        // upper boundary (assumed map limit)
        Repartidor high = Repartidor.builder()
                .id(null)
                .nombre("High")
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.MOTO)
                .ubicacion(new Coordenada(100, 100))
                .build();

        when(jpaRepartidorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(low);
        adapter.save(high);

        ArgumentCaptor<RepartidorEntity> captor = ArgumentCaptor.forClass(RepartidorEntity.class);
        verify(jpaRepartidorRepository, times(2)).save(captor.capture());

        List<RepartidorEntity> entities = captor.getAllValues();
        assertEquals(0, entities.get(0).getX());
        assertEquals(100, entities.get(1).getX());
    }

    @Test
    void findByEstado_EmptyList_shouldReturnEmptyListNotNull() {
        when(jpaRepartidorRepository.findByEstado(EstadoRepartidor.INACTIVO)).thenReturn(Collections.emptyList());

        List<Repartidor> result = adapter.findByEstado(EstadoRepartidor.INACTIVO);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_NullValues_nameNullAndUbicacionNull_behaviour() {
        // name null -> adapter should still call save and propagate null name in entity
        Repartidor nameNull = Repartidor.builder()
                .id(null)
                .nombre(null)
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.AUTO)
                .ubicacion(new Coordenada(1, 1))
                .build();

        when(jpaRepartidorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Repartidor savedNameNull = adapter.save(nameNull);

        ArgumentCaptor<RepartidorEntity> captor = ArgumentCaptor.forClass(RepartidorEntity.class);
        verify(jpaRepartidorRepository).save(captor.capture());
        RepartidorEntity persisted = captor.getValue();
        assertNull(persisted.getNombre());

        // ubicacion null -> expect NullPointerException when adapter tries to access ubicacion.x()
        Repartidor ubicacionNull = Repartidor.builder()
                .id(null)
                .nombre("NoUbicacion")
                .estado(EstadoRepartidor.ACTIVO)
                .vehiculo(TipoVehiculo.AUTO)
                .ubicacion(null)
                .build();

        assertThrows(NullPointerException.class, () -> adapter.save(ubicacionNull));
    }

    @Test
    void toDomain_DataIntegrity_enumsAndFieldsAreMappedExactly() {
        RepartidorEntity entity = RepartidorEntity.builder()
                .id(42L)
                .nombre("Integrity")
                .estado(EstadoRepartidor.INACTIVO)
                .vehiculo(TipoVehiculo.AUTO)
                .x(9)
                .y(8)
                .build();

        when(jpaRepartidorRepository.findById(42L)).thenReturn(Optional.of(entity));

        Optional<Repartidor> result = adapter.findById(42L);
        assertTrue(result.isPresent());
        Repartidor r = result.get();
        assertEquals(EstadoRepartidor.INACTIVO, r.getEstado());
        assertEquals(TipoVehiculo.AUTO, r.getVehiculo());
        assertEquals(9, r.getUbicacion().x());
        assertEquals(8, r.getUbicacion().y());
    }
}
