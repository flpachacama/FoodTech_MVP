package com.foodtech.domain.model;

/**
 * Posición geográfica expresada en coordenadas reales.
 */
public record Coordenada(double x, double y) {

	public double distanciaA(Coordenada otra) {
		if (otra == null) {
			throw new IllegalArgumentException("Coordenada destino no puede ser null");
		}
		return Math.sqrt(Math.pow(this.x - otra.x(), 2) + Math.pow(this.y - otra.y(), 2));
	}
}
