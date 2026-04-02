-- =============================================================
-- Datos de ejemplo: tabla restaurantes
-- Microservicio: order-service
-- Formato menú: JSON array de productos {id, nombre, precio}
-- Idempotente: no inserta si ya existen (basado en id)
-- =============================================================

INSERT INTO restaurantes (id, nombre, coordenada_x, coordenada_y, menu) VALUES (
    1,
    'La Hamburguesería',
    10,
    20,
    '[{"id":1,"nombre":"Hamburguesa Clásica","precio":18000},{"id":2,"nombre":"Hamburguesa BBQ","precio":22000},{"id":3,"nombre":"Papas Fritas","precio":8000},{"id":4,"nombre":"Gaseosa","precio":5000}]'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO restaurantes (id, nombre, coordenada_x, coordenada_y, menu) VALUES (
    2,
    'Pizzería Napoli',
    50,
    40,
    '[{"id":1,"nombre":"Pizza Margherita","precio":25000},{"id":2,"nombre":"Pizza Pepperoni","precio":28000},{"id":3,"nombre":"Pizza Cuatro Quesos","precio":30000},{"id":4,"nombre":"Agua Mineral","precio":4000}]'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO restaurantes (id, nombre, coordenada_x, coordenada_y, menu) VALUES (
    3,
    'Sushi Kyoto',
    1,
    50,
    '[{"id":1,"nombre":"Roll Philadelphia","precio":32000},{"id":2,"nombre":"Roll Spicy Tuna","precio":35000},{"id":3,"nombre":"Edamame","precio":12000},{"id":4,"nombre":"Té Verde","precio":6000}]'
)
ON CONFLICT (id) DO NOTHING;
