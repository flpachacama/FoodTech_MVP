package com.foodtech.domain.model;

/**
 * Posición en la cuadrícula simulada (matriz X, Y).
 * Validaciones: x,y deben estar en el rango [0,100].
 */
public record Coordenada(int x, int y) {
	public Coordenada {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("Coordenada no puede ser negativa");
		}
		if (x > 100 || y > 100) {
			throw new IllegalArgumentException("Coordenada fuera de rango (0-100)");
		}
	}

	public double distanciaA(Coordenada otra) {
		if (otra == null) {
			throw new IllegalArgumentException("Coordenada destino no puede ser null");
		}
		return Math.sqrt(Math.pow(this.x - otra.x(), 2) + Math.pow(this.y - otra.y(), 2));
	}
}
