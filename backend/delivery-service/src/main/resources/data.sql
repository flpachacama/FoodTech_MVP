-- =============================================================
-- Datos iniciales para la tabla repartidores
-- Idempotente: no inserta si ya existen (basado en nombre único)
-- =============================================================

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (1, 'Carlos Mendoza', 'ACTIVO', 'MOTO', 25.5, 40.25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (2, 'Ana Rodríguez', 'ACTIVO', 'BICICLETA', -60.75, 15.5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (3, 'Luis Fernández', 'EN_ENTREGA', 'AUTO', 80.0, -75.25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (4, 'María González', 'EN_ENTREGA', 'MOTO', 10.5, 90.9)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (5, 'Pedro Sánchez', 'INACTIVO', 'AUTO', -45.2, 55.55)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (6, 'Pedro Marquez', 'ACTIVO', 'BICICLETA', 95.75, 15.25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (7, 'Laura Jimenez', 'ACTIVO', 'MOTO', 0.0, 100.5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (8, 'Carla Vargas', 'ACTIVO', 'BICICLETA', 75.33, 25.67)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (9, 'Omar Ortiz', 'ACTIVO', 'AUTO', -63.1, 22.2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (10, 'Javier Angarita', 'ACTIVO', 'MOTO', 31.9, 34.4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (11, 'Sofía Herrera', 'ACTIVO', 'MOTO', 18.25, -72.8)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (12, 'Diego Ramírez', 'ACTIVO', 'BICICLETA', 55.5, 88.88)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (13, 'Valentina Torres', 'ACTIVO', 'AUTO', -42.42, 11.11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (14, 'Sebastián Castro', 'ACTIVO', 'MOTO', 67.01, 49.99)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (15, 'Camila Morales', 'ACTIVO', 'BICICLETA', 33.3, -61.6)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (16, 'Andrés Gutiérrez', 'ACTIVO', 'AUTO', 85.85, 37.37)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (17, 'Isabella Vargas', 'ACTIVO', 'MOTO', -9.9, 53.53)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (18, 'Mateo Pineda', 'EN_ENTREGA', 'AUTO', 74.74, 92.92)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (19, 'Lucía Navarro', 'ACTIVO', 'BICICLETA', 50.05, -5.5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (20, 'Felipe Ríos', 'INACTIVO', 'MOTO', 22.22, 78.78)
ON CONFLICT (id) DO NOTHING;