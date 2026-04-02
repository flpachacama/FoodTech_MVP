# FoodTech Frontend — Angular 19

Aplicación Angular 19 que visualiza restaurantes y repartidores en un mapa interactivo, permite ver el menú de cada restaurante y realizar pedidos conectándose a los microservicios del backend.

---

## Requisitos

| Herramienta | Versión recomendada |
|-------------|---------------------|
| Node.js     | **≥ 18.19** (probado en v20 y v22) |
| npm         | ≥ 9 |
| Angular CLI | 19.x (`npm install -g @angular/cli`) |

> Angular 19 **no es compatible** con Node 16 ni anteriores.

---

## Instalación y ejecución

```bash
# Desde la raíz del repositorio
cd frontend/foodtech-app

# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm start
# o
ng serve
```

La aplicación estará disponible en **http://localhost:4200**

---

## Conexión con el backend

Antes de ejecutar el frontend, asegúrate de que los contenedores Docker del backend estén corriendo:

```bash
# Desde la raíz del repositorio
cd backend
docker compose up -d --build
```

| Microservicio     | URL base               | Endpoints usados                          |
|-------------------|------------------------|-------------------------------------------|
| order-service     | http://localhost:8081  | `GET /restaurants`, `GET /restaurants/{id}`, `POST /orders` |
| delivery-service  | http://localhost:8080  | `GET /delivers`, `GET /delivers/{id}`     |

Las URLs se configuran en `src/environments/environment.ts` (desarrollo) y `environment.prod.ts` (producción).

---

## Estructura del proyecto

```
foodtech-app/
├── src/
│   ├── app/
│   │   ├── models/              # Interfaces TypeScript
│   │   │   ├── restaurante.model.ts
│   │   │   ├── producto-menu.model.ts
│   │   │   ├── deliver.model.ts
│   │   │   └── index.ts
│   │   ├── services/            # Servicios HTTP
│   │   │   ├── restaurante.service.ts
│   │   │   ├── deliver.service.ts
│   │   │   └── index.ts
│   │   └── components/
│   │       ├── mapa/            # Canvas interactivo con restaurantes y repartidores
│   │       └── menu-modal/      # Modal para ver y agregar productos al pedido
│   └── environments/
│       ├── environment.ts       # URLs desarrollo (localhost)
│       └── environment.prod.ts  # URLs producción
```

---

## Componentes principales

### MapaComponent
Canvas HTML5 de 800×800 px con sistema de coordenadas 0–100:

- **Restaurantes** — Desplegable para ver restaurantes ver su menú
- Mapa en google maps para visualizar ubicación del restaurante

### MenuModalComponent
Modal que aparece al hacer click en un restaurante:

- Lista de productos con nombre y precio formateado (`$18.000 COP`)
- Botón "Agregar" por producto
- Cierre con botón X o click en el overlay
- Animaciones de entrada (fadeIn, slideIn)
- Responsive: 95% del ancho en móvil

---

## Comandos útiles

```bash
# Compilar para producción
ng build

# Ejecutar tests unitarios
ng test

# Generar un componente nuevo
ng generate component components/nombre-componente
```

---

## Estado del desarrollo

| Paso | Descripción | Estado |
|------|-------------|--------|
| 3.1  | Environments, modelos e interfaces, servicios HTTP, HttpClient | ✅ |
| 3.2  | MapaComponent con Canvas, repartidores y restaurantes | ✅ |
| 3.3  | MenuModalComponent con lista de productos | ✅ |
| 3.4  | Integración completa en AppComponent con signals | ✅ |
