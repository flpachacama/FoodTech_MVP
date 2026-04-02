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

    @Test
    void distancia_shouldReturnZero_whenSameCoordinates() {
        Coordenada a = new Coordenada(10, 20);
        assertEquals(0.0, a.distanciaA(a), 1e-6);
    }

    @Test
    void distancia_shouldReturnFive_for3_4_5Triangle() {
        Coordenada a = new Coordenada(0, 0);
        Coordenada b = new Coordenada(3, 4);
        assertEquals(5.0, a.distanciaA(b), 1e-6);
    }

    @Test
    void distancia_shouldBeSymmetric_betweenPoints() {
        Coordenada a = new Coordenada(10, 10);
        Coordenada b = new Coordenada(13, 14);
        double d1 = a.distanciaA(b);
        double d2 = b.distanciaA(a);
        assertEquals(d1, d2, 1e-9);
        assertEquals(5.0, d1, 1e-6);
    }

    @Test
    void distancia_shouldReturnSqrt2_forDiagonalUnit() {
        Coordenada a = new Coordenada(0, 0);
        Coordenada b = new Coordenada(1, 1);
        assertEquals(Math.sqrt(2), a.distanciaA(b), 1e-6);
    }
}
