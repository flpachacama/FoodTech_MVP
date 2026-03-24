package com.foodtech.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordenadaTest {

    @Test
    void shouldCreateCoordenada_WhenValuesAreValid() {
        Coordenada a = new Coordenada(0, 0);
        assertEquals(0, a.x());
        assertEquals(0, a.y());

        Coordenada b = new Coordenada(100, 100);
        assertEquals(100, b.x());
        assertEquals(100, b.y());
    }

    @Test
    void shouldThrowException_WhenXIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Coordenada(-1, 10));
    }

    @Test
    void shouldThrowException_WhenYIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Coordenada(10, -1));
    }

    @Test
    void shouldThrowException_WhenValuesExceedLimit() {
        assertThrows(IllegalArgumentException.class, () -> new Coordenada(101, 50));
    }
}
