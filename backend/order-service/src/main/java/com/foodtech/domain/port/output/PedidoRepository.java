package com.foodtech.order.domain.port.output;

import com.foodtech.order.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository {

    Pedido save(Pedido pedido);

    Optional<Pedido> findById(Long id);

    List<Pedido> findAll();

}
