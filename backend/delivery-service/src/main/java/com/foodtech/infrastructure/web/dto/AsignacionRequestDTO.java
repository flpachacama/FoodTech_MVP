package com.foodtech.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionRequestDTO {

    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;

    @NotNull(message = "La coordenada X es obligatoria")
    private Integer restauranteX;

    @NotNull(message = "La coordenada Y es obligatoria")
    private Integer restauranteY;

    private String clima;
}
