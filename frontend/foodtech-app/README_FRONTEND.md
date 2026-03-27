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

### ⏳ Próximos pasos
- **Paso 3.2:** MapaComponent con Canvas (visualizar restaurantes y repartidores)
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
