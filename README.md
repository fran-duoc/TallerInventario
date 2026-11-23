# Sistema de Gestión de Inventario & Repuestos - Taller Mecánico Automotriz (Puente Alto)

**Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105)  
**Institución:** Duoc UC  
**Evaluación:** Evaluación Parcial 4 (Entrega Final - Full Stack & Móvil)  
**Estudiante:** Francisca Velásquez (fra.velasquezp@duocuc.cl)  
**GitHub:** [https://github.com/fran-duoc/TallerInventario](https://github.com/fran-duoc/TallerInventario)  
**Rama de Entrega:** `evaluacion-parcial-4`

---

## 📋 1. Gestión Ágil del Proyecto (Tablero Trello)

El ciclo de desarrollo, la priorización de historias de usuario y el seguimiento continuo de tareas se administraron en el tablero real de Trello:

* 🔗 **Enlace Oficial del Tablero Trello:**  
  [https://trello.com/b/folcXL7H/tallerinventario-evaluaci%C3%B3n-parcial-4](https://trello.com/b/folcXL7H/tallerinventario-evaluaci%C3%B3n-parcial-4)

### Estructura de Listas de Flujo de Trabajo:
1. **Por hacer:** Backlog inicial y requerimientos desglosados.
2. **En desarrollo:** Tareas activas en implementación.
3. **En revisión:** Validaciones de arquitectura, DTOs y contratos HTTP.
4. **Pruebas:** Pruebas unitarias de backend, Android y análisis de cobertura JaCoCo.
5. **Finalizado:** Todas las entregables cumplidos al 100%.

---

## 📌 2. Descripción del Proyecto y Contexto de Negocio

La solución integral resuelve la gestión técnica, operativa y comercial de repuestos, lubricantes y fluidos para el **Taller Mecánico Automotriz de Puente Alto**. Permite la sincronización bidireccional entre la aplicación móvil Android y un backend empresarial Spring Boot, además de interoperar con la base de datos vehicular del gobierno de EE.UU. (NHTSA) para compatibilidad técnica de repuestos y verificación de VINs.

---

## ☕ 3. Backend REST con Spring Boot

Ubicación del código fuente: `./backend/`

### Tecnologías & Arquitectura Backend:
* **Java 21 / 25** con **Spring Boot 3.4.0**.
* **Spring Data JPA & Hibernate** para mapeo relacional.
* **Base de datos H2** persistente en archivo (`./backend/data/tallerdb.mv.db`) y consola interactiva web en `/h2-console`.
* **Jakarta Bean Validation** (`@Valid`, `@NotBlank`, `@Min`, `@DecimalMin`, etc.).
* **Arquitectura limpia por capas:** Controller, Service, Repository, Entity, DTO y Global Exception Handler con respuestas RFC 7807 (`ProblemDetail`).
* **Configuración CORS:** Habilitado para todos los orígenes (`*`) y emuladores Android (`10.0.2.2`).

### Endpoints de la API REST (`/api/productos`):

| Método | Endpoint | Descripción | Respuesta Exitosa |
|---|---|---|---|
| `GET` | `/api/productos` | Lista todo el inventario (admite filtro opcional `?categoria=...`) | `200 OK` (JSON List) |
| `GET` | `/api/productos/{codigo}` | Obtiene el detalle de un repuesto por su código | `200 OK` (JSON Object) |
| `POST` | `/api/productos` | Registra un nuevo repuesto con validaciones | `201 Created` |
| `PUT` | `/api/productos/{codigo}` | Actualiza completamente un repuesto existente | `200 OK` |
| `DELETE` | `/api/productos/{codigo}` | Elimina físicamente un repuesto del inventario | `204 No Content` |
| `GET` | `/api/productos/buscar?query={q}` | Búsqueda por coincidencia en código, nombre, marca o categoría | `200 OK` (JSON List) |
| `GET` | `/api/productos/bajo-stock` | Filtra repuestos críticos cuya cantidad $\le$ stock mínimo | `200 OK` (JSON List) |

### Instrucciones para Ejecutar el Backend:
```powershell
cd backend
.\gradlew.bat bootRun
```
* Servidor activo en: `http://localhost:8080/api/productos`
* Consola H2 Database: `http://localhost:8080/h2-console`
  * JDBC URL: `jdbc:h2:file:./data/tallerdb`
  * Usuario: `sa` | Contraseña: *(vacía)*

---

## 🌐 4. Integración de API Externa Automotriz (NHTSA API)

Se integró la API pública y gratuita de la **National Highway Traffic Safety Administration (NHTSA)** del Departamento de Transporte de los Estados Unidos:

* **Endpoint Base:** `https://vpic.nhtsa.dot.gov/api/`
* **Casos de Uso en el Taller:**
  1. **Decodificación de VIN (17 caracteres):** Obtiene especificaciones técnicas completas del vehículo ingresado al taller (fabricante, modelo, año, tipo de motor, cilindrada, potencia en HP, tipo de combustible y tracción). Permite verificar la compatibilidad de los repuestos con el vehículo exacto del cliente.
  2. **Búsqueda de Modelos por Marca:** Consulta los modelos vigentes asociados a cualquier marca automotriz para catálogo rápido de repuestos.
* **Interfaz de Usuario:** Nueva pantalla dedicada `VinConsultaScreen` accesible directamente desde la barra superior de la app, con integración directa al escáner de cámara nativo (`CameraX` + `ML Kit`) para escanear el VIN en formato código de barras.

---

## 📱 5. Aplicación Android (Jetpack Compose + MVVM)

* **Offline-First:** Persistencia primaria reactiva en SQLite mediante **Room Database**.
* **Sincronización Bidireccional:** Mediante **Retrofit 2.11.0** y **OkHttp Logging Interceptor**. Cuando hay conectividad, las operaciones CRUD se replican automáticamente en el backend Spring Boot (`http://10.0.2.2:8080`).
* **Botón de Sincronización Manual:** En el TopAppBar para descargar y actualizar el catálogo local con el del servidor remoto en cualquier momento.
* **Escaneo Nativo:** Cámara con CameraX y Google ML Kit para escaneo rápido de códigos QR y códigos de barra de repuestos y códigos VIN.
* **Notificaciones del Sistema:** Alerta nativa en barra de estado (`NotificationManager`) ante niveles críticos de stock.

---

## 🧪 6. Pruebas Unitarias y Cobertura de Código

Se implementaron suites exhaustivas de pruebas unitarias tanto en Backend como en Android con **JUnit 5**, **Mockito**, **MockK** y **Kotlinx Coroutines Test**:

### Backend Spring Boot (39 pruebas unitarias ejecutadas):
* `ProductoServiceTest`: Lógica de negocio, excepciones `ResourceNotFoundException` y regla de precios.
* `ProductoControllerTest`: MockMvc de todos los endpoints REST y códigos HTTP (200, 201, 204, 400, 404).
* `ProductoRepositoryTest`: Consultas personalizadas JPA y queries derivadas.
* `GlobalExceptionHandlerTest`: Mapeo de errores y validaciones.
* `DataInitializerTest`: Precarga de catálogo inicial.
* `BackendApplicationTest`: Contexto Spring Boot y arranque de servidor.
* **Cobertura Backend:** **95.9% de instrucciones / 98.17% de líneas** (Supera ampliamente el 80% exigido).

### Android (9 pruebas unitarias ejecutadas):
* `ProductoRepositoryTest`: Pruebas de orquestación de Room, sincronización Retrofit y llamadas a NHTSA.
* `ProductoViewModelTest`: Pruebas reactivas sobre `StateFlow`, validación de estados UI, decodificación VIN y sincronización con backend.
* **Ejecutar Pruebas Android:**
  ```powershell
  .\gradlew.bat testDebugUnitTest
  .\gradlew.bat jacocoTestReport
  ```

---

## 📦 7. Generación del APK Firmado (Release)

La aplicación cuenta con configuración de firmado de producción automatizada mediante Gradle y `local.properties`:

* **Ubicación del APK Firmado:**
  `app/build/outputs/apk/release/app-release.apk`
* **Tamaño del APK:** ~36.2 MB
* **Keystore:** `release.jks` (Algoritmo RSA 2048 bits, validez 10.000 días).
* **Comando de Generación:**
  ```powershell
  .\gradlew.bat assembleRelease
  ```


---

## 🖥️ 8. Guía de Ejecución y Defensa (Android Studio + Emulador)

Para la evaluación y defensa del proyecto en equipos con recursos limitados, se debe seguir exactamente este orden:

### Paso 1: Iniciar el Backend Spring Boot
Abra una terminal en la raíz del proyecto y ejecute:
```powershell
cd backend
.\gradlew.bat bootRun
```
* **Verificación:** Ingrese a `http://localhost:8080/api/productos` en su navegador o a la consola H2 en `http://localhost:8080/h2-console`.
* **Persistencia:** Los datos se guardan de forma permanente en el archivo `./backend/data/tallerdb.mv.db`.

### Paso 2: Ejecutar la App en Android Studio
1. Abra **Android Studio** y cargue la carpeta del proyecto `TallerInventario`.
2. Espere que finalice la sincronización de Gradle.
3. Inicie **un solo emulador Android** (Virtual Device / AVD).
4. La aplicación se comunicará de manera predeterminada con el backend mediante la IP reservada del emulador:
   ```
   http://10.0.2.2:8080/
   ```
5. Presione **Run 'app'** (`Shift + F10`) en Android Studio.

### Paso 3: Pruebas Funcionales en el Emulador
* **Sincronización:** En la barra superior de la app, pulse el botón de sincronización (ícono de flechas circulares) para sincronizar el catálogo de Room con Spring Boot.
* **CRUD Completo:** Registre, edite o elimine repuestos desde el formulario; los cambios se guardan localmente en Room y se replican en la base de datos H2 del backend.
* **API NHTSA:** Pulse el ícono de automóvil en el encabezado para acceder a `VinConsultaScreen`, ingrese un VIN de 17 caracteres (ej: `1HGBH41JXMN109186`) y pulse *Decodificar*, o consulte modelos por marca automotriz.
* **Modo Offline:** Si el backend se detiene, la aplicación continúa funcionando fluidamente con persistencia local en Room sin caídas (offline-first).
