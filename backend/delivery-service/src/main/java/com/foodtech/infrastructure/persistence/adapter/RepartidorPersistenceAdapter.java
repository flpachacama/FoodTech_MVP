package com.foodtech.infrastructure.persistence.adapter;

import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.port.output.RepartidorRepository;
import com.foodtech.infrastructure.persistence.entity.RepartidorEntity;
import com.foodtech.infrastructure.persistence.repository.JpaRepartidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia que implementa el puerto de salida RepartidorRepository.
 * Convierte entre el modelo de dominio (Repartidor) y la entidad JPA (RepartidorEntity).
 */
@Component
@RequiredArgsConstructor
public class RepartidorPersistenceAdapter implements RepartidorRepository {

    private final JpaRepartidorRepository jpaRepartidorRepository;

    @Override
    public Optional<Repartidor> findById(Long id) {
        return jpaRepartidorRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Repartidor> findByEstado(EstadoRepartidor estado) {
        return jpaRepartidorRepository.findByEstado(estado)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Repartidor save(Repartidor repartidor) {
        RepartidorEntity entity = toEntity(repartidor);
        RepartidorEntity savedEntity = jpaRepartidorRepository.save(entity);
        return toDomain(savedEntity);
    }

    /**
     * Convierte de entidad JPA a modelo de dominio.
     */
    private Repartidor toDomain(RepartidorEntity entity) {
        return Repartidor.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .estado(entity.getEstado())
                .vehiculo(entity.getVehiculo())
                .ubicacion(new Coordenada(entity.getX(), entity.getY()))
                .build();
    }

    /**
     * Convierte de modelo de dominio a entidad JPA.
     */
    private RepartidorEntity toEntity(Repartidor repartidor) {
        return RepartidorEntity.builder()
                .id(repartidor.getId())
                .nombre(repartidor.getNombre())
                .estado(repartidor.getEstado())
                .vehiculo(repartidor.getVehiculo())
                .x(repartidor.getUbicacion().x())
                .y(repartidor.getUbicacion().y())
                .build();
    }
}
