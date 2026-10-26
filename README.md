# Sistema de Gestión de Inventario & Repuestos - Taller Mecánico Automotriz (Puente Alto)

**Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105)  
**Institución:** Duoc UC  
**Evaluación:** Evaluación Parcial 2 (Encargo - Experiencia de Aprendizaje 2)  
**Equipo de Desarrollo:**
* Estudiante 1: Francisca Velásquez 
* Estudiante 2: Guioinno Igor 

---

## 📌 1. Descripción del Proyecto y Contexto de Negocio

La aplicación móvil da solución integral a la problemática real de control de repuestos, lubricantes, fluidos y accesorios del **Taller Mecánico Automotriz de Puente Alto**.

El sistema permite al jefe de taller y a los mecánicos:
1. Mantener un catálogo categorizado de repuestos con control estricto de existencias y umbrales mínimos.
2. Calcular en tiempo real la **regla de reposición automática mínima sugerida**:
   $$\text{cantidadSugeridaReposicion} = \max(0, (\text{stockMinimo} \times 2) - \text{cantidad})$$
3. Prevenir quiebres de inventario mediante insignias visuales accesibles y alertas automáticas.
4. Conocer la ubicación física exacta de cada pieza en bodega (Zona/Sector, Estante y Repisa) y el proveedor asignado.

---

## 🚀 2. Funcionalidades Principales Implementadas

### A. Interfaz Visual & Usabilidad (Jetpack Compose + Material Design 3)
* **Pantalla de Catálogo Reactivo (`ListaProductosScreen`):**
  * Búsqueda en tiempo real por texto (código, nombre del repuesto, marca o categoría) con botón de limpieza rápida.
  * Filtros horizontales por categoría (`FilterChip`), incluyendo filtro directo por repuestos en estado crítico (**Bajo Stock / Crítico**).
  * Tarjetas de repuesto (`ProductCard`) con bordes de contraste, desglose de precios (Venta y Costo), ubicación física y proveedor.
  * Insignia de stock accesible (`StockBadge`) con color semántico, ícono y texto explícito (*Disponible*, *Bajo Stock*, *Agotado*).
  * Diálogo modal de confirmación antes de eliminar cualquier registro.

### B. Formularios Validados & Experiencia de Usuario (`ProductoFormScreen`)
* Formulario estructurado en 3 secciones funcionales con íconos temáticos en cada campo:
  1. *Identificación del Repuesto:* Código con botón de escáner directo, Nombre, Marca, Selector de Categoría y Selector de Unidad de Medida.
  2. *Inventario, Stock y Precios:* Cantidad actual, Stock Mínimo, cálculo dinámico de reposición sugerida, Precios de compra y venta.
  3. *Ubicación y Proveedor:* Sector de bodega, Estante, Repisa y Proveedor.
* Validaciones visuales completas por campo (`isError = true`, ícono de advertencia `trailingIcon` y mensajes de error descriptivos `supportingText`).

### C. Lógica Desacoplada y Arquitectura MVVM
* Patrón **Model-View-ViewModel (MVVM)** con separación estricta de responsabilidades:
  * **View (Compose):** Interfaz declarativa sin lógica de negocio acoplada.
  * **ViewModel (`ProductoViewModel`):** Gestión de estado reactivo mediante `StateFlow<ProductoUiState>` y validaciones desacopladas:
    * Código obligatorio y verificación de clave primaria única en base de datos.
    * Campos de texto obligatorios (nombre, marca, categoría).
    * Cantidad y stock mínimo enteros $\ge 0$.
    * Precios numéricos $\ge 0$ y regla financiera: $\text{precioVenta} \ge \text{precioCompra}$ (prohibida venta bajo costo).
    * Actualización dinámica del estado del inventario (*Disponible*, *Bajo Stock*, *Agotado*).
  * **Repository (`ProductoRepository`):** Abstracción de datos que asegura ejecución en hilos de fondo (`Dispatchers.IO`).

### D. Persistencia Local con SQLite y Room Database
* Base de datos local mediante **Room**:
  * Entidad `ProductoEntity` con clave primaria `codigo`.
  * DAO `ProductoDao` con consultas reactivas mediante Kotlin `Flow` (búsqueda dinámica, filtros y ordenamiento).
  * Precarga automática en segundo plano (`Dispatchers.IO`) con 5 productos representativos del taller de Puente Alto (Castrol EDGE 5W-30, Filtro Mann W712/22, Líquido de Frenos Bosch DOT4, Bujías Denso Iridium, Pastillas Brembo P060).
  * Respaldo y persistencia total de datos tras cerrar y reiniciar la aplicación.

### E. Integración de Recursos Nativos del Dispositivo (IE 2.4.1)
1. **Cámara con CameraX y Google ML Kit Barcode Scanning:**
   * Diálogo modal `QrScannerDialog` con vista previa de cámara y procesamiento continuo de frames en hilo dedicado.
   * Compatible con códigos QR y códigos de barra 1D estándar en repuestos (EAN-13, CODE-128, UPC-A, UPC-E).
   * Solicitud segura de permisos de cámara en runtime (`Manifest.permission.CAMERA`).
   * Botón de control de linterna (Flash/Torch) para lectura en bodegas con baja iluminación.
   * Feedback sensorial instantáneo: vibración háptica y pitido acústico (`ToneGenerator`) al detectar un código.
2. **Notificaciones del Sistema Operativo (`NotificationManager`):**
   * Canal de notificación nativo registrado en Android (`taller_alertas_stock`).
   * Emisión automática de alertas en la barra de estado del sistema cuando un repuesto entra en estado *Bajo Stock* o *Agotado*.
   * Verificación de permisos para Android 13+ (`Manifest.permission.POST_NOTIFICATIONS`).
   * Botón de prueba directa en el encabezado de la aplicación y en cada tarjeta de repuesto con stock comprometido.
3. **Marcador Telefónico Nativo (`Intent.ACTION_DIAL`):**
   * Botón de contacto directo con proveedores automotrices desde la ficha del repuesto y el formulario.

### F. Animaciones Visuales Funcionales (IE 2.2.2)
* **Transiciones de Navegación Fluidas:** Configuración en `NavHost` con deslizamientos horizontales y desvanecimientos (`slideInHorizontally` + `fadeIn`).
* **Expansión y Contracción Suave:** `Modifier.animateContentSize()` en tarjetas `ProductCard`.
* **Visibilidad Animada:** `AnimatedVisibility` para avisos de reposición mínima sugerida y contadores de stock crítico.
* **Transiciones de Color Suave:** `animateColorAsState` en insignias `StockBadge`.

---

## 🛠️ 3. Tecnologías y Librerías Utilizadas

* **Lenguaje:** Kotlin 2.2.10
* **SDK Android:** compileSdk 35 | minSdk 24 | targetSdk 35
* **UI Toolkit:** Jetpack Compose (BOM 2024.12.01) con Material Design 3
* **Navegación:** `androidx.navigation:navigation-compose:2.8.5`
* **Persistencia:** Room Database 2.7.0 (KSP + SQLite + Kotlin Coroutines & Flow)
* **Recursos Nativos:**
  * CameraX 1.4.1 (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`)
  * Google ML Kit Barcode Scanning 17.3.0
  * Android Notifications API (`NotificationManager`, `NotificationCompat`)
  * Android Intents (`Intent.ACTION_DIAL`)

---

## 📱 4. Pasos para Compilar y Ejecutar

### Requisitos Previos:
* Android Studio Ladybug o superior (o Jellyfish / Koala).
* JDK 17 o 21 configurado en Android Studio (`Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`).
* Dispositivo físico Android con depuración USB habilitada (o emulador con cámara y emulación de webcam configurada).

### Instrucciones de Ejecución:
1. Clonar el repositorio:
   ```bash
   git clone <URL_DEL_REPOSITORIO_GITHUB>
   ```
2. Abrir el proyecto en Android Studio (`Open...` y seleccionar la carpeta del proyecto).
3. Esperar la sincronización de Gradle (`Sync Project with Gradle Files`).
4. Seleccionar la variante de compilación `debug`.
5. Presionar el botón **Run 'app'** (Shift + F10) en Android Studio o compilar mediante terminal:
   ```bash
   ./gradlew assembleDebug
   ```
6. El APK generado se ubica en: `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📋 5. Planificación y Trabajo Colaborativo (Trello)

La distribución de tareas, asignación de responsabilidades y seguimiento de avances del equipo se gestionó en el siguiente tablero de Trello:
* **Enlace a Tablero Trello:** `https://trello.com/b/taller-mecanico-inventario-duoc` *(Reemplazar con el enlace real del equipo)*
* **Columnas de Seguimiento:** *Por Hacer*, *En Progreso*, *Revisión / Pruebas*, *Finalizado*.
