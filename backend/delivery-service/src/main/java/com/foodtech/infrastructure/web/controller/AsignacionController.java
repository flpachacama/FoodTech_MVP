package com.foodtech.infrastructure.web.controller;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.port.input.RepartidorUseCase;
import com.foodtech.infrastructure.web.dto.AsignacionRequestDTO;
import com.foodtech.infrastructure.web.dto.AsignacionResponseDTO;
import com.foodtech.infrastructure.web.dto.RepartidorResponseDTO;
import com.foodtech.application.service.AsignacionApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionApplicationService asignacionApplicationService;
    private final RepartidorUseCase repartidorUseCase;

    @PostMapping
    public AsignacionResponseDTO asignarRepartidor(@Valid @RequestBody AsignacionRequestDTO request) {
        Coordenada coordenada = new Coordenada(request.getRestauranteX(), request.getRestauranteY());
        Clima clima = request.getClima() != null ? Clima.valueOf(request.getClima()) : null;

        Repartidor asignado = asignacionApplicationService.asignarRepartidor(coordenada, clima);

        if (asignado == null) {
            return AsignacionResponseDTO.builder()
                    .pedidoId(request.getPedidoId())
                    .estado("PENDIENTE")
                    .repartidorId(null)
                    .nombreRepartidor(null)
                    .build();
        }

        return AsignacionResponseDTO.builder()
                .pedidoId(request.getPedidoId())
                .estado("ASIGNADO")
                .repartidorId(asignado.getId())
                .nombreRepartidor(asignado.getNombre())
                .build();
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<?> updateEstado(@PathVariable("id") Long id, @RequestBody EstadoUpdateRequest request) {
        String evento = request == null ? null : request.evento();

        try {
            Repartidor actualizado = asignacionApplicationService.procesarEventoRepartidor(id, evento);

            if (actualizado == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Repartidor no encontrado"));
            }

            RepartidorResponseDTO dto = RepartidorResponseDTO.builder()
                    .id(actualizado.getId())
                    .nombre(actualizado.getNombre())
                    .estado(actualizado.getEstado() != null ? actualizado.getEstado().name() : null)
                    .vehiculo(actualizado.getVehiculo() != null ? actualizado.getVehiculo().name() : null)
                    .x(actualizado.getUbicacion() != null ? actualizado.getUbicacion().x() : null)
                    .y(actualizado.getUbicacion() != null ? actualizado.getUbicacion().y() : null)
                    .build();

            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
        }
    }
}
