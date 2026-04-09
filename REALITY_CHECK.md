# REALITY CHECK - FoodTech MVP

## 1. ¿Que tareas subestimamos y por que?

En retrospectiva, las tareas mas subestimadas no fueron las de "crear endpoints", sino las de **integrar reglas de negocio entre servicios y UI sin romper consistencia de estados**.

### a) Complejidad real del algoritmo de asignacion
- Subestimamos que "elegir el mejor repartidor" implicaba combinar varias reglas al mismo tiempo: estado (`ACTIVO`), clima (`SOLEADO`, `LLUVIA_SUAVE`, `LLUVIA_FUERTE`), distancia y velocidad.
- El algoritmo en `AsignacionService` no solo ordena por cercania; prioriza por tiempo estimado (`distancia / velocidad`) y aplica filtro de clima antes de ordenar.
- Los casos de empate, candidatos invalidos (sin coordenadas) y clima nulo agregaron complejidad no prevista inicialmente.

### b) Integracion order-service + delivery-service
- Se subestimo el costo de sincronizar estados entre dos microservicios en operaciones sensibles: crear pedido, cancelar y marcar entregado.
- En `OrderApplicationService`, el flujo completo (`PENDIENTE -> ASIGNADO`, `ASIGNADO -> CANCELADO`, `ASIGNADO -> ENTREGADO`) depende de respuestas externas y manejo de fallos.
- Aprendizaje clave: la logica de negocio distribuida exige mas pruebas de integracion de las que estimamos al inicio.

### c) Modelo de coordenadas y tiempos
- En planning se hablo de matriz X,Y simple, pero en implementacion se trabajaron coordenadas geograficas (`Double`) y calculo aproximado de distancia.
- Eso afecto validaciones, conversion de tiempo estimado y pruebas de bordes (coordenadas invalidas, nulas o fuera de rango).
- Tambien subestimamos la explicacion de ETA total al usuario: tiempo repartidor->restaurante + tiempo restaurante->cliente.

### d) Frontend: menos "visual" de lo esperado
- La vista de mapa fue mas que dibujar puntos: hubo que resolver carga asincrona de restaurantes, seleccion, estados vacios y fallback de mapa embebido.
- La vista de repartidor tambien requirio mas manejo de estado de UI: cargando, sin pedido, pedido activo, accion de "entregado" y refresco local del estado.
- Se subestimo la coordinacion frontend-backend para mensajes claros cuando no hay repartidores disponibles.

### e) Entorno y estabilidad de desarrollo
- Docker, puertos, CORS, datos iniciales y dependencias entre servicios consumieron mas tiempo de estabilizacion que el estimado.
- El mayor costo oculto fue mantener entornos reproducibles para DEV y QA durante iteraciones rapidas.

---

## 2. ¿El MVP construido es realmente valioso para el negocio?

**Si, el MVP es valioso para negocio, pero con alcance controlado y sin venderlo como producto final.**

### Lo que si aporta valor real
- Resuelve el problema central definido en el PRD: asignar pedidos con criterio objetivo de eficiencia (tiempo estimado), no por asignacion manual.
- Implementa reglas operativas concretas de seguridad/viabilidad: con `LLUVIA_FUERTE` no se asigna bici ni moto.
- Entrega flujo usable extremo a extremo para el objetivo MVP: seleccionar restaurante, crear pedido, asignar repartidor, cancelar o entregar.
- Muestra ETA al usuario y control de estado del repartidor, lo que mejora transparencia operativa desde la primera version.

### Lo que limita su valor hoy
- Sin login/roles reales, no hay trazabilidad de usuario ni seguridad de operacion.
- Sin GPS real, trafico ni clima externo, la estimacion es util para simulacion pero no robusta para produccion.
- Sin multiples pedidos por repartidor y sin tracking en tiempo real, la operacion solo representa una version simplificada del negocio.
- La interfaz cumple para demo funcional, pero aun no para experiencia comercial completa.

### Conclusión de negocio
El sistema **no es solo una prueba tecnica**: ya valida la hipotesis principal de asignacion inteligente y permite demos operativas creibles con flujo completo.

Al mismo tiempo, **no esta listo para escalar como producto comercial** sin las capacidades fuera de alcance (autenticacion, optimizacion bajo alta concurrencia).

En terminos agiles: es un MVP **usable y con aprendizaje accionable**, no un "v1 listo para operar a gran escala".

---

## 3. ¿Como garantizo QA la calidad del MVP en poco tiempo?

QA trabajo con enfoque de riesgo, no de cobertura "por volumen". La prioridad fue proteger el core del negocio: asignacion, estados y flujo de pedido.

### Estrategia aplicada
- **BDD con Gherkin:** los criterios de `USER_STORIES.md` se usaron como contrato comun QA-DEV para reducir ambiguedad.
- **Priorizacion de casos criticos:** se ejecuto primero lo bloqueante (asignacion, clima, estados, cancelacion, entrega) antes de expandir casos secundarios.
- **Automatizacion por capas:**
  - Serenity + Cucumber para flujos de negocio,
  - Karate para contratos API entre servicios,
  - k6 para baseline de rendimiento y riesgos de escalabilidad.
- **Soporte con pruebas unitarias y trazabilidad tecnica:** los reportes de Maven en ambos servicios evidencian suites estables en dominio y aplicacion.

### Sinergia QA + DEV
- QA participo desde refinamiento, por eso varias reglas se validaron temprano: restricciones de clima, consistencia de estados y validaciones de coordenadas/datos obligatorios.
- DEV ajusto implementacion y manejo de errores segun feedback rapido de QA, evitando defectos acumulados al cierre.
- La trazabilidad HU -> caso de prueba -> resultado se mantuvo visible en `TEST_CASES.md`, con foco en HU1-HU10 para cerrar el alcance pactado.

### Como se manejo el tiempo limitado
- Se trabajo en micro-sprints con regresion parcial continua en lugar de una sola regresion grande al final.
- Se definio criterio de salida estricto para lo critico: sin defectos bloqueantes/altos abiertos en flujos core.
- Se acepto conscientemente deuda de cobertura en funcionalidades fuera de alcance (HU11-HU12 inicialmente fuera del plan de pruebas del MVP base) para proteger la fecha de entrega.

### Resultado practico de QA
Con el tiempo disponible, QA no busco "probar todo"; busco **probar lo que podia romper el valor de negocio**. Ese enfoque permitio entregar un MVP estable para demostracion, con riesgos conocidos y documentados para la siguiente iteracion.
