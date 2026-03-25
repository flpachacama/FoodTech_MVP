package com.foodtech.application.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.port.input.AsignacionUseCase;
import com.foodtech.domain.port.output.RepartidorRepository;
import com.foodtech.domain.service.AsignacionService;
import com.foodtech.domain.port.input.RepartidorUseCase;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AsignacionApplicationService implements AsignacionUseCase {

    private final RepartidorRepository repartidorRepository;
    private final AsignacionService asignacionService;
    private final RepartidorUseCase repartidorUseCase;

    public AsignacionApplicationService(RepartidorRepository repartidorRepository,
                                       AsignacionService asignacionService,
                                       RepartidorUseCase repartidorUseCase) {
        this.repartidorRepository = Objects.requireNonNull(repartidorRepository);
        this.asignacionService = Objects.requireNonNull(asignacionService);
        this.repartidorUseCase = Objects.requireNonNull(repartidorUseCase);
    }

    @Override
    public List<Repartidor> obtenerRepartidoresPriorizados(Coordenada restauranteUbicacion, Clima clima) {
        List<Repartidor> activos = repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO);
        
        if (activos == null || activos.isEmpty()) {
            return Collections.emptyList();
        }
        return asignacionService.priorizarRepartidores(activos, restauranteUbicacion, clima);
    }

    public Repartidor asignarRepartidor(Coordenada restauranteUbicacion, Clima clima) {
        List<Repartidor> activos = repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO);
        if (activos == null || activos.isEmpty()) {
            return null;
        }

        List<Repartidor> priorizados = asignacionService.priorizarRepartidores(activos, restauranteUbicacion, clima);
        if (priorizados == null || priorizados.isEmpty()) {
            return null;
        }

        Repartidor candidato = priorizados.get(0);
        return repartidorUseCase.cambiarEstado(candidato.getId(), EstadoRepartidor.EN_ENTREGA);
    }
}
