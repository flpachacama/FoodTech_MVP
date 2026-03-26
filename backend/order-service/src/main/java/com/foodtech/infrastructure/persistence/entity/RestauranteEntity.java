package com.foodtech.order.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurantes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "coordenada_x")
    private Integer coordenadaX;

    @Column(name = "coordenada_y")
    private Integer coordenadaY;

    /**
     * Menú del restaurante serializado como JSON.
     * Contiene la lista de productos disponibles.
     */
    @Column(name = "menu", columnDefinition = "TEXT")
    private String menu;
}
