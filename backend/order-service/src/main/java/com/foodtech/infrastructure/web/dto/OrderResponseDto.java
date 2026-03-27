package com.foodtech.order.infrastructure.web.dto;

import com.foodtech.order.domain.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;
    private Long restauranteId;
    private Long repartidorId;
    private List<ProductoPedidoDto> productos;
    private Long clienteId;
    private String clienteNombre;
    private Integer clienteCoordenadasX;
    private Integer clienteCoordenadasY;
    private String clienteTelefono;
    private Integer tiempoEstimado;
    private EstadoPedido estado;
}
