# FoodTech Frontend - Angular 19

Aplicación Angular 19 standalone para visualizar restaurantes y repartidores en un mapa.

## Estructura del proyecto

```
foodtech-app/
├── src/
│   ├── app/
│   │   ├── models/          # Interfaces TypeScript
│   │   │   ├── producto-menu.model.ts
│   │   │   ├── restaurante.model.ts
│   │   │   ├── deliver.model.ts
│   │   │   └── index.ts
│   │   ├── services/        # Servicios HTTP
│   │   │   ├── restaurante.service.ts
│   │   │   ├── deliver.service.ts
│   │   │   └── index.ts
│   │   └── components/      # Componentes (próximos pasos)
│   └── environments/        # Configuración de URLs
│       ├── environment.ts
│       └── environment.prod.ts
```

## Instalación y ejecución

```bash
cd /home/omar/proyectos/semana7/FoodTech_MVP/frontend/foodtech-app

# Instalar dependencias (ya ejecutado)
npm install

# Ejecutar en desarrollo
npm start
# o
ng serve

# La aplicación estará disponible en http://localhost:4200
```

## Estado del desarrollo

### ✅ Paso 3.1 - COMPLETADO
- Environments configurados (URLs de microservicios)
- Modelos/Interfaces creadas (Restaurante, ProductoMenu, Deliver)
- Servicios HTTP creados (RestauranteService, DeliverService)
- HttpClient configurado en app.config.ts

### ✅ Paso 3.2 - COMPLETADO
- MapaComponent creado con Canvas HTML5 (800x800px)
- Renderizado de restaurantes (círculos rojos) y repartidores (triángulos de colores)
- Sistema de coordenadas 0-100 → 0-800 pixels
- Interactividad: click en restaurantes, hover con tooltip
- Signals para estado reactivo
- Grid de fondo para referencia visual

### ⏳ Próximos pasos
- **Paso 3.3:** MenuModalComponent (mostrar menú del restaurante)
- **Paso 3.4:** Integración en AppComponent

## Conexión con Backend

La aplicación consume 2 microservicios:

| Servicio | URL | Endpoints |
|----------|-----|-----------|
| order-service | http://localhost:8081 | GET /restaurants, GET /restaurants/{id} |
| delivery-service | http://localhost:8080 | GET /delivers, GET /delivers/{id} |

**Nota:** Asegúrate de que los contenedores Docker del backend estén corriendo antes de ejecutar el frontend.

## Testing de servicios

Para probar los servicios desde el navegador:
1. Ejecuta `ng serve`
2. Abre la consola del navegador
3. Los servicios están inyectados como `providedIn: 'root'` y listos para usar

## Características del MapaComponent

El componente de mapa visualiza en un canvas de 800x800 pixels:

**Restaurantes:**
- Círculos rojos (radio 15px)
- Nombre del restaurante debajo
- Clickeable para ver el menú

**Repartidores:**
- Triángulos con colores según estado:
  - 🟢 Verde: ACTIVO
  - 🟡 Amarillo: EN_ENTREGA
  - ⚪ Gris: INACTIVO
- Letra del vehículo dentro: B (Bicicleta), M (Moto), A (Auto)

**Interacción:**
- Grid de fondo de 10x10 (coordenadas 0-100)
- Cursor pointer al pasar sobre restaurantes
- Tooltip al hacer hover
- Evento `restauranteSelected` al hacer click
