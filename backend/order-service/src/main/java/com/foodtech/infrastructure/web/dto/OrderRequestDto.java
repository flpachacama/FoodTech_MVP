package com.foodtech.order.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    private Long restauranteId;
    private List<ProductoPedidoDto> productos;
    private String clienteNombre;
    private Integer clienteCoordenadasX;
    private Integer clienteCoordenadasY;
    private String clienteTelefono;
}
