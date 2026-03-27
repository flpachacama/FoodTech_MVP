package com.foodtech.order.infrastructure.web.dto;

import com.foodtech.order.domain.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliverOrderResponseDto {

    private Long id;
    private EstadoPedido estado;
    private String mensaje;
}
