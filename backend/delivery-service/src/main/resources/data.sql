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

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (11, 'Sofía Herrera', 'ACTIVO', 'MOTO', 18, 72)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (12, 'Diego Ramírez', 'ACTIVO', 'BICICLETA', 55, 88)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (13, 'Valentina Torres', 'ACTIVO', 'AUTO', 42, 11)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (14, 'Sebastián Castro', 'ACTIVO', 'MOTO', 67, 49)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (15, 'Camila Morales', 'ACTIVO', 'BICICLETA', 33, 61)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (16, 'Andrés Gutiérrez', 'ACTIVO', 'AUTO', 85, 37)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (17, 'Isabella Vargas', 'ACTIVO', 'MOTO', 9, 53)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (18, 'Mateo Pineda', 'EN_ENTREGA', 'AUTO', 74, 92)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (19, 'Lucía Navarro', 'ACTIVO', 'BICICLETA', 50, 5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO repartidores (id, nombre, estado, vehiculo, x, y) 
VALUES (20, 'Felipe Ríos', 'INACTIVO', 'MOTO', 22, 78)
ON CONFLICT (id) DO NOTHING;