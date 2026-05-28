## Documentación

### Explicación de la arquitectura elegida y herramienta de persistencia
Utilicé un estilo llamado Clean Architecture junto con MVVM. Separé el código en 3 partes para que no se mezcle y sea más fácil de mantener:
* **Pantallas (Presentation):** Es lo que el usuario ve y toca (botones, listas). Usé Jetpack Compose para construirlas.
* **Datos (Data):** De dónde saco la información, ya sea de internet (API) o de la memoria del teléfono.
* **Reglas de negocio (Domain):** El "cerebro" de la app, que decide si un producto se puede comprar o calcula el total del carrito, sin importarle de dónde vengan los datos ni cómo se ven en pantalla.

**Justificación de la herramienta seleccionada (Room):**
Elegí usar Room (SQLite) que es una base de datos local recomendada por Google. Lo elegí porque:
1. Es muy popular, fácil de usar en Android y oficial.
2. Me avisa si escribo mal una consulta de base de datos antes de siquiera correr la app.
3. Me permite guardar los productos y el carrito directamente en el teléfono para que funcione sin internet.

**Estrategia adoptada para mitigar la pérdida de datos:**
Hice que el carrito guarde cada cambio (como agregar un producto o cambiar la cantidad) directamente en la base de datos del teléfono (Room). Así, si la app se cierra de golpe o te quedas sin batería, cuando vuelvas a abrir la app verás tu carrito tal como lo dejaste, porque está guardado de forma segura en el almacenamiento del dispositivo.

### Instrucciones detalladas para ejecutar la app y correr la suite de pruebas
**¿Qué necesitas?**
* Tener instalado **Android Studio**.
* Conexión a internet para descargar las librerías la primera vez y obtener el catálogo inicial.

**Pasos para correr la app:**
1. Descarga o clona este proyecto y ábrelo con Android Studio.
2. Deja que Android Studio sincronice todo
3. Conecta tu teléfono Android (que tenga versión 8.0 o mayor) o abre un emulador.
4. Dale clic al botón verde de "Play" ▶️ arriba (o presiona Shift + F10 en Windows / Control + R en Mac).

**Cómo correr la suite de pruebas:**
Abre la terminal dentro de Android Studio y escribe:
```bash
./gradlew test
```

### Supuestos técnicos o limitaciones asumidas
* **Límites del diseño:** La galería de imágenes muestra las fotos pero sin función de zoom. Además, solo muestro unas pocas reseñas en el detalle del producto para no saturar la pantalla.
* **Impuestos:** Asumí que los impuestos se cobran al final de la compra, aplicándose después de los descuentos.

---

## Preguntas de Profundidad Técnica

### 1. Arquitectura y Resiliencia
**Explique cómo la inversión de dependencia y el uso de interfaces en la capa de Domain facilitarían cambiar por completo el motor de persistencia local (ej. migrar de una base de datos relacional en SQLite con Room a una solución NoSQL orientada a objetos como ObjectBox) sin alterar una sola línea de código de la interfaz de usuario en Compose.**

Al utilizar Clean Architecture, apliqué el Principio de Inversión de Dependencias (SOLID). Esto significa que mi capa de Presentación (las pantallas hechas en Compose) no conoce los detalles de la base de datos; solo se comunica con la capa Domain a través de Interfaces (contratos). 
Si yo quisiera cambiar de Room a ObjectBox, simplemente crearía una nueva conexión en la capa de Datos que implemente esa misma interfaz. Como la interfaz (el contrato) no cambia, la vista en Compose seguiría llamando a los mismos métodos, logrando el cambio de base de datos sin tener que modificar absolutamente nada en la interfaz de usuario.

### 2. Estrategias Offline
**Al implementar una arquitectura Offline-First apoyada en SQLite o ObjectBox, ¿cómo gestionaría la sincronización de datos de la API para evitar conflictos de concurrencia o sobrescritura de datos locales si el usuario añade elementos al carrito mientras no tiene conexión a internet?**

Para evitar conflictos de concurrencia, apliqué una estrategia donde la base de datos local (Room) es la única fuente de la verdad. Manejo la lista de productos y el carrito del usuario en tablas separadas, por lo que una actualización de la API no sobrescribe las acciones del carrito.
Si no tienes internet, las acciones del carrito se guardan localmente con un timestamp (fecha exacta del cambio). Al recuperar la conexión, utilizaría una herramienta como WorkManager para sincronizar estos datos en segundo plano hacia la API. Para resolver posibles conflictos, aplicaría una política de Last-Write-Wins (LWW) basada en ese timestamp, asegurando que la modificación más reciente siempre prevalezca.

### 3. Seguridad y Profiling
**¿Qué estrategias de cifrado implementaría si los datos almacenados en SQLite/ObjectBox contuvieran información sensible del negocio, y qué herramientas de Android Studio Profiler utilizaría para inspeccionar en tiempo real las consultas SQL y el rendimiento de la base de datos local?**

* **Seguridad:** Para proteger información sensible, implementaría la librería SQLCipher. Esta herramienta cifra la base de datos por completo de forma transparente usando AES-256. La clave de cifrado generada se guardaría de forma segura en el Android Keystore, protegiendo los datos incluso si el dispositivo llega a ser manipulado externamente o *rooteado*.
* **Profiling:** Para monitorear el rendimiento y la velocidad, utilizo la herramienta App Inspection / Database Inspector de Android Studio. Esta herramienta me permite ver y modificar las tablas de Room en tiempo real mientras uso la app. Además, uso el Database Profiler (Trace SQL) para medir en milisegundos cuánto tarda cada consulta, lo que me ayuda a detectar cuellos de botella que puedan hacer que la aplicación se trabe.
