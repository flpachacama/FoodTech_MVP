package com.foodtech.infrastructure.web.controller;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.port.input.AsignacionUseCase;
import com.foodtech.infrastructure.web.dto.AsignacionRequestDTO;
import com.foodtech.infrastructure.web.dto.AsignacionResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionUseCase asignacionUseCase;

    @PostMapping
    public AsignacionResponseDTO asignarRepartidor(@Valid @RequestBody AsignacionRequestDTO request) {
        Coordenada coordenada = new Coordenada(request.getRestauranteX(), request.getRestauranteY());
        Clima clima = request.getClima() != null ? Clima.valueOf(request.getClima()) : null;
        List<Repartidor> candidatos = asignacionUseCase.obtenerRepartidoresPriorizados(coordenada, clima);

        if (candidatos == null || candidatos.isEmpty()) {
            return AsignacionResponseDTO.builder()
                    .pedidoId(request.getPedidoId())
                    .estado("PENDIENTE")
                    .repartidorId(null)
                    .nombreRepartidor(null)
                    .build();
        }

        Repartidor primerCandidato = candidatos.get(0);
        return AsignacionResponseDTO.builder()
                .pedidoId(request.getPedidoId())
                .estado("ASIGNADO")
                .repartidorId(primerCandidato.getId())
                .nombreRepartidor(primerCandidato.getNombre())
                .build();
    }
}
