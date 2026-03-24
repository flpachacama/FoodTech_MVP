# TEST PLAN - FoodTech MVP

## 1. Identificacion del Plan

- **Nombre del proyecto:** FoodTech MVP
- **Sistema bajo prueba (SUT):** Plataforma de delivery con algoritmo de asignacion automatica de repartidores
- **Version del plan:** 1.0
- **Fecha:** 24 de marzo de 2026
- **Equipo:**
  - QA Lead
  - QA Automation Engineer
  - QA Manual (apoyo exploratorio)
  - Backend Developer (Order/Delivery)
  - Frontend Developer
  - Product Owner
  - Scrum Master

## 2. Contexto

FoodTech MVP busca reducir tiempos de entrega y mejorar la asignacion de pedidos mediante reglas de negocio claras:

- Seleccionar repartidores por cercania al restaurante.
- Priorizar por tiempo estimado segun tipo de transporte.
- Aplicar restricciones por clima (por ejemplo, excluir bicicleta y moto en lluvia fuerte).
- Controlar estado operativo del repartidor (solo `ACTIVO` puede recibir pedidos).

El problema de negocio que resuelve es la asignacion ineficiente de pedidos en escenarios de alta variabilidad (clima, disponibilidad y demanda), disminuyendo retrasos, reasignaciones y cancelaciones.

## 3. Alcance de las Pruebas

### IN (dentro del alcance)

Se cubren historias de usuario funcionales del MVP:

- HU1 Gestion de estado de repartidores
- HU2 Filtrado por cercania
- HU3 Restricciones por clima
- HU4 Calculo de prioridad
- HU5 Asignacion automatica de pedidos
- HU6 Actualizacion de estado del repartidor
- HU7 Generacion de pedido
- HU8 Confirmacion de pedido y tiempo estimado
- HU9 Cancelacion de pedido
- HU10 Visualizacion y seleccion de restaurantes
- HU11 Visualizacion del pedido en interfaz repartidor
- HU12 Marcar pedido como entregado

### OUT (fuera del alcance)

- Login/registro y seguridad de autenticacion
- GPS real y geolocalizacion en tiempo real
- Integraciones con APIs externas
- Tracking en vivo del repartidor en mapa

## 4. Estrategia de Pruebas

Se aplicara una estrategia por capas para detectar defectos temprano y reducir retrabajo.

### 4.1 Pruebas funcionales E2E y de aceptacion - Serenity BDD + Cucumber

**Objetivo:** validar flujos de negocio completos desde perspectiva usuario/sistema usando criterios Gherkin de `USER_STORIES.md`.

**Aplicacion en el proyecto:**

- Definir features por HU prioritaria (HU1-HU12).
- Automatizar escenarios criticos:
  - Asignacion exitosa con repartidor valido
  - Sin repartidores disponibles
  - Restriccion por clima en `LLUVIA_FUERTE`
  - Cambio de estado `ACTIVO -> EN_ENTREGA -> ACTIVO`
  - Cancelacion y liberacion de repartidor
- Generar evidencia ejecutiva en reportes Serenity para demo de sprint y gate de release.

### 4.2 Pruebas de API - Karate

**Objetivo:** validar contratos, reglas de negocio y respuestas de microservicios `order` y `delivery`.

**Aplicacion en el proyecto:**

- Pruebas de endpoints principales:
  - `POST /orders`
  - `PUT /orders/{id}/cancel`
  - `PUT /orders/{id}/delivered`
  - `POST /delivery`
  - `PUT /delivery/{id}/state`
  - `GET /orders/{id}/active-order`
- Cobertura de casos positivos, negativos y bordes:
  - Campos obligatorios
  - Estado invalido
  - Sin candidatos
  - Reglas de clima
- Validar consistencia de estados entre servicios (order y delivery).

### 4.3 Pruebas de rendimiento baseline - k6

**Objetivo:** medir comportamiento bajo carga controlada para detectar cuellos de botella temprano.

**Aplicacion en el proyecto:**

- Escenarios iniciales:
  - Rampa de creacion de pedidos
  - Cancelaciones concurrentes
  - Picos de asignacion en clima adverso
- Metricas observadas:
  - `p95` de tiempo de respuesta por endpoint critico
  - tasa de error
  - throughput
- Resultado esperado del MVP: establecer linea base y riesgos de escalabilidad para siguientes iteraciones.

## 5. Criterios de Entrada y Salida

### Criterios de Entrada

Para iniciar pruebas de una HU/sprint se requiere:

- Historia refinada con criterios de aceptacion claros.
- Ambiente local estable y accesible para QA.
- Datos de prueba disponibles (repartidores, restaurantes, clima, pedidos).
- Build desplegable del sprint con cambios integrados.
- Casos de prueba revisados por QA Lead y PO (cuando aplique).

### Criterios de Salida

Para cerrar pruebas de una HU/sprint se requiere:

- 100% de escenarios criticos ejecutados.
- 100% de pruebas bloqueantes en estado aprobado.
- Sin defectos criticos ni altos abiertos (o con plan de mitigacion aprobado por PO/DEV).
- Evidencia publicada (reportes Serenity, resultados Karate, reporte k6 baseline).
- Trazabilidad HU -> caso de prueba -> resultado.

## 6. Entorno de Pruebas

### Configuracion del entorno

- Ejecucion local en entorno de desarrollo (MVP).
- Dos microservicios activos:
  - `order`
  - `delivery`
- Frontend web para flujo consumidor y repartidor.

### Datos simulados

- Repartidores con estados: `ACTIVO`, `INACTIVO`, `EN_ENTREGA`.
- Vehiculos: `BICICLETA`, `MOTO`, `AUTO`.
- Clima: `SOLEADO`, `LLUVIA_SUAVE`, `LLUVIA_FUERTE`.
- Coordenadas `X,Y` simuladas para restaurantes, clientes y repartidores.
- Pedidos en estados: `PENDIENTE`, `ASIGNADO`, `ENTREGADO`, `CANCELADO`.

### Ejecucion local

- Ejecucion por suite funcional (Serenity+Cucumber), API (Karate) y rendimiento (k6).
- Corridas minimas por HU en pipeline local de QA antes de cierre de sprint.

## 7. Herramientas

- **Serenity BDD:** Orquesta pruebas de aceptacion y genera reportes ejecutivos con evidencia clara para negocio y equipo tecnico.
- **Cucumber:** Define escenarios en lenguaje natural (Gherkin), facilitando alineacion entre PO, QA y DEV.
- **Karate:** Automatiza pruebas API y validacion de contratos sin alta complejidad de codigo.
- **k6:** Ejecuta pruebas de carga y rendimiento para identificar latencia, errores bajo concurrencia y limites del MVP.

## 8. Roles y Responsabilidades

### QA

- Diseñar estrategia de pruebas por sprint y por HU.
- Definir y mantener casos de prueba manuales/automatizados.
- Automatizar suites con Serenity+Cucumber y Karate.
- Ejecutar pruebas funcionales, API y baseline de rendimiento.
- Reportar defectos con evidencia, severidad y criterio de reproduccion.
- Gestionar metricas de calidad y recomendar decision de salida (Go/No-Go).

### DEV

- Implementar funcionalidades y criterios de aceptacion acordados.
- Corregir defectos priorizados dentro del sprint.
- Mantener estabilidad tecnica del entorno para pruebas.
- Soportar analisis de causa raiz en defectos criticos.
- Participar en definicion de datos de prueba y observabilidad basica.

## 9. Cronograma y Estimacion

La planificacion QA se alinea a Story Points del backlog y se ejecuta de forma incremental por fases.

| Fase | HU incluidas | SP QA estimados |
|---|---|---:|
| Fase 1 - Modelo base | HU1, HU2, HU3 | 11 |
| Fase 2 - Algoritmo | HU4, HU5, HU6 | 13 |
| Fase 3 - Flujo de pedidos | HU7, HU8, HU9 | 16 |
| Fase 4 - UX consumidor | HU10 | 3 |
| Fase 5 - UX repartidor | HU11, HU12 | 6 |
| **Total QA** | **HU1-HU12** | **49** |

### Distribucion recomendada por sprint (referencial)

- **Sprint 1:** HU1-HU3 + automatizacion base (11 SP QA)
- **Sprint 2:** HU4-HU6 + regresion parcial (13 SP QA)
- **Sprint 3:** HU7-HU9 + pruebas de integracion de flujo completo (16 SP QA)
- **Sprint 4:** HU10-HU12 + regresion final + baseline k6 (9 SP QA)

## 10. Entregables de Prueba

- Matriz de cobertura HU vs casos de prueba.
- Casos de prueba funcionales (Gherkin) versionados.
- Scripts automatizados:
  - Serenity BDD + Cucumber
  - Karate
  - k6
- Reportes de ejecucion por sprint y reporte final de regresion.
- Registro de defectos con severidad/prioridad y estado.
- Metricas de calidad:
  - porcentaje de escenarios aprobados
  - defectos por severidad
  - tendencia de retrabajo
  - estabilidad de endpoints criticos

## 11. Riesgos y Contingencias

| Riesgo | Impacto | Mitigacion preventiva | Contingencia |
|---|---|---|---|
| Error en calculo de distancia | Asignaciones incorrectas y mayor tiempo de entrega | Pruebas de formula con casos controlados, limites y regresion automatizada HU2/HU4 | Hotfix priorizado + recalculo controlado + monitoreo reforzado |
| Escalabilidad insuficiente | Lentitud o errores en picos de pedidos | Baseline temprano con k6 en endpoints criticos y ajustes iterativos | Limitar concurrencia temporal, priorizar colas y optimizar consultas |
| Falta de repartidores disponibles | Pedidos en estado pendiente y mala experiencia usuario | Validar mensajes claros y flujo PENDIENTE en HU5/HU8; preparar reglas de fallback | Notificar disponibilidad limitada y habilitar reintento manual del usuario |
| Alta demanda en horarios pico | Aumento de latencia y cancelaciones | Pruebas de carga por ventanas pico, pruebas de cancelacion concurrente | Plan operativo de contingencia y priorizacion de pedidos por SLA |
| Error en captura/aplicacion de clima | Exclusiones incorrectas de vehiculos y riesgo operativo | Casos cruzados clima-transporte (HU3) y validaciones de consistencia en API | Desactivar regla defectuosa por feature flag temporal y aplicar correccion urgente |

---

## Enfoque de mejora continua

Este plan se revisa al cierre de cada sprint en retrospectiva tecnica QA-DEV para ajustar cobertura, priorizacion de riesgo y deuda de automatizacion. El objetivo es prevenir defectos desde refinamiento, no solo detectarlos en validacion final.
