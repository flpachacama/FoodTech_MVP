package com.foodtech.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class RestauranteEntityTest {

    @Test
    void debeCrearYleerCampos() throws Exception {
        RestauranteEntity r = new RestauranteEntity(1L, "La Casa", 10.0, 20.0, "{\"menu\":[]}");

        Field nombre = RestauranteEntity.class.getDeclaredField("nombre");
        nombre.setAccessible(true);
        assertEquals("La Casa", nombre.get(r));

        Field menu = RestauranteEntity.class.getDeclaredField("menu");
        menu.setAccessible(true);
        assertEquals("{\"menu\":[]}", menu.get(r));
    }

    @Test
    void debePermitirCamposNulos() throws Exception {
        RestauranteEntity r = new RestauranteEntity();
        Field nombre = RestauranteEntity.class.getDeclaredField("nombre");
        nombre.setAccessible(true);
        assertNull(nombre.get(r));
    }

}
