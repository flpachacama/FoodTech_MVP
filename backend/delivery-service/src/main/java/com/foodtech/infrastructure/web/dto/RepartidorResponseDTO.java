package com.foodtech.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartidorResponseDTO {

    private Long id;
    private String nombre;
    private String estado;
    private String vehiculo;
    private Integer x;
    private Integer y;
}
