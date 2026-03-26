package com.foodtech.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Long id;
    private EstadoPedido estado;
    private Long restauranteId;
    private List<ProductoPedido> productos;
    private Long clienteId;
    private String clienteNombre;
    private Double clienteCoordenadasX;
    private Double clienteCoordenadasY;
    private Integer tiempoEstimado;
}
