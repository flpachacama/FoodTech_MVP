package com.foodtech.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordenadaTest {

    @Test
    void shouldCreateCoordenada_WhenValuesAreValid() {
        Coordenada a = new Coordenada(-12.5, 0.75);
        assertEquals(-12.5, a.x(), 1e-9);
        assertEquals(0.75, a.y(), 1e-9);

        Coordenada b = new Coordenada(100.25, -100.75);
        assertEquals(100.25, b.x(), 1e-9);
        assertEquals(-100.75, b.y(), 1e-9);
    }

    @Test
    void distancia_shouldReturnZero_whenSameCoordinates() {
        Coordenada a = new Coordenada(10.5, -20.25);
        assertEquals(0.0, a.distanciaA(a), 1e-6);
    }

    @Test
    void distancia_shouldReturnFive_for3_4_5Triangle() {
        Coordenada a = new Coordenada(-1.5, -2.5);
        Coordenada b = new Coordenada(1.5, 1.5);
        assertEquals(5.0, a.distanciaA(b), 1e-6);
    }

    @Test
    void distancia_shouldBeSymmetric_betweenPoints() {
        Coordenada a = new Coordenada(-10.5, 10.25);
        Coordenada b = new Coordenada(-7.5, 14.25);
        double d1 = a.distanciaA(b);
        double d2 = b.distanciaA(a);
        assertEquals(d1, d2, 1e-9);
        assertEquals(5.0, d1, 1e-6);
    }

    @Test
    void distancia_shouldReturnSqrt2_forDiagonalUnit() {
        Coordenada a = new Coordenada(0.0, 0.0);
        Coordenada b = new Coordenada(1.0, 1.0);
        assertEquals(Math.sqrt(2), a.distanciaA(b), 1e-6);
    }

    @Test
    void distancia_shouldThrowException_whenTargetIsNull() {
        Coordenada a = new Coordenada(0.0, 0.0);
        assertThrows(IllegalArgumentException.class, () -> a.distanciaA(null));
    }
}
