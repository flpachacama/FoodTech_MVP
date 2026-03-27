-- =============================================================
-- Datos iniciales para la tabla repartidores
-- Idempotente: no inserta si ya existen (basado en nombre único)
-- =============================================================

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (1, 'Carlos Mendoza', 'ACTIVO', 'MOTO', 25, 40)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (2, 'Ana Rodríguez', 'ACTIVO', 'BICICLETA', 60, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (3, 'Luis Fernández', 'EN_ENTREGA', 'AUTO', 80, 75)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (4, 'María González', 'EN_ENTREGA', 'MOTO', 10, 90)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (5, 'Pedro Sánchez', 'INACTIVO', 'AUTO', 45, 55)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (6, 'Pedro Marquez', 'ACTIVO', 'BICICLETA', 95, 15)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (7, 'Laura Jimenez', 'ACTIVO', 'MOTO', 0, 100)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (8, 'Carla Vargas', 'ACTIVO', 'BICICLETA', 75, 25)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (9, 'Omar Ortiz', 'ACTIVO', 'AUTO', 63, 22)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (10, 'Javier Angarita', 'ACTIVO', 'MOTO', 31, 34)
ON CONFLICT (id) DO NOTHING;