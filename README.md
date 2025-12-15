# FlowUp - Gestor de Actividades

Aplicación Android nativa desarrollada en Kotlin con Jetpack Compose para la gestión eficiente de actividades y tareas personales.

---

## 📱 Descripción

FlowUp es una aplicación de gestión de actividades que permite a los usuarios organizar, priorizar y realizar seguimiento de sus tareas diarias. Cuenta con un sistema completo de notificaciones, sincronización en segundo plano y una interfaz moderna implementada con Material Design 3.

---

## ✨ Funcionalidades Principales

### Gestión de Actividades
- **Crear actividades** con título, descripción, fecha de vencimiento, categoría y prioridad
- **Editar actividades** pendientes con actualización automática de notificaciones
- **Eliminar actividades** con cancelación de recordatorios programados
- **Marcar como completadas** con transición automática entre estados
- **Filtrado y ordenamiento** por fecha, prioridad o estado

### Sistema de Notificaciones
- **Recordatorios programados** con días de anticipación configurables
- **Notificaciones inmediatas** cuando el tiempo de recordatorio ya pasó
- **Cancelación automática** al completar o eliminar actividades
- **Reprogramación inteligente** al editar fechas o recordatorios
- **Botón de prueba** en configuración para verificar permisos

### Servicio en Primer Plano
- **Sincronización continua** en segundo plano
- **Notificación persistente** de baja prioridad
- **Control manual** desde la configuración (Iniciar/Detener)
- **Reinicio automático** si el sistema termina el servicio

### Preferencias y Configuración
- **Temas**: Claro, Oscuro, Sistema
- **Ordenamiento**: Por fecha (ascendente/descendente) o prioridad
- **Gestión de completadas**: Mostrar/ocultar, auto-eliminación después de 7 días
- **Notificaciones**: Habilitar/deshabilitar, configuración de horarios
- **Persistencia**: DataStore para guardar preferencias del usuario

### Visualización de Actividades Completadas
- **Pantalla dedicada** para actividades completadas
- **Restaurar a pendientes** tocando el checkbox
- **Eliminación permanente** de actividades completadas
- **Navegación** desde el menú principal

---

## 🏗️ Arquitectura

### Patrón Arquitectónico
- **MVVM (Model-View-ViewModel)** con separación clara de responsabilidades
- **Repository Pattern** para abstracción de fuentes de datos
- **Unidirectional Data Flow** con StateFlow

### Capas de la Aplicación

```
┌─────────────────────────────────────┐
│           UI Layer (Compose)         │
│  ┌─────────────────────────────┐   │
│  │ Screens (Composables)       │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │ ViewModels (State + Logic)  │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│         Domain Layer                 │
│  ┌─────────────────────────────┐   │
│  │ Repositories                │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│          Data Layer                  │
│  ┌───────────┐  ┌──────────────┐   │
│  │   Room    │  │  DataStore   │   │
│  │ Database  │  │ Preferences  │   │
│  └───────────┘  └──────────────┘   │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│      Background Services             │
│  ┌───────────┐  ┌──────────────┐   │
│  │WorkManager│  │  Foreground  │   │
│  │ (Workers) │  │   Service    │   │
│  └───────────┘  └──────────────┘   │
└─────────────────────────────────────┘
```

---

## 📂 Estructura del Proyecto

```
app/src/main/java/com/app/flowup/
│
├── data/                           # Capa de datos
│   ├── local/                      # Base de datos local
│   │   ├── AppDatabase.kt         # Configuración de Room
│   │   ├── ActivityEntity.kt      # Entidad de actividades
│   │   └── ActivityDao.kt         # Data Access Object
│   ├── repository/
│   │   └── ActivityRepository.kt  # Repositorio de actividades
│   └── preferences/
│       └── PreferencesRepository.kt # Gestión de preferencias
│
├── di/                            # Inyección de dependencias
│   └── DatabaseModule.kt          # Módulo Hilt para Room
│
├── notifications/                 # Sistema de notificaciones
│   └── NotificationManager.kt     # Gestor y Worker
│
├── service/                       # Servicios
│   └── SyncForegroundService.kt  # Servicio en primer plano
│
├── ui/                           # Interfaz de usuario
│   ├── components/               # Componentes reutilizables
│   │   └── ActivityCard.kt      # Tarjeta de actividad
│   ├── screens/                 # Pantallas
│   │   ├── home/                # Pantalla principal
│   │   │   ├── HomeScreen.kt
│   │   │   ├── HomeViewModel.kt
│   │   │   └── HomeUiState.kt
│   │   ├── addactivity/         # Agregar actividad
│   │   ├── editactivity/        # Editar actividad
│   │   ├── completed/           # Actividades completadas
│   │   └── settings/            # Configuración
│   └── theme/                   # Tema de la aplicación
│       ├── Theme.kt
│       ├── ThemeViewModel.kt
│       ├── Color.kt
│       └── Type.kt
│
├── navigation/                   # Navegación
│   ├── NavRoutes.kt             # Definición de rutas
│   └── FlowUpNavGraph.kt        # Grafo de navegación
│
├── MainActivity.kt              # Activity principal
└── FlowUpApplication.kt        # Clase Application
```

---

## 🛠️ Tecnologías y Librerías

### Framework y Lenguaje
- **Kotlin** 2.0.20
- **Jetpack Compose** (UI moderna y declarativa)
- **Material Design 3** (componentes y tema)

### Jetpack Components
- **Room** 2.6.1 - Persistencia de datos local
- **Navigation Compose** 2.8.5 - Navegación entre pantallas
- **DataStore** 1.1.1 - Almacenamiento de preferencias
- **WorkManager** 2.9.0 - Tareas en segundo plano
- **Lifecycle & ViewModel** - Gestión de ciclo de vida

### Inyección de Dependencias
- **Hilt** 2.52 - Inyección de dependencias con Dagger

### Procesamiento de Anotaciones
- **KSP** 2.0.20-1.0.25 - Kotlin Symbol Processing

### Build System
- **Gradle** 8.7.3 con Kotlin DSL
- **AGP** (Android Gradle Plugin) 8.7.3

---

## 🗄️ Base de Datos

### Entidad: ActivityEntity

```kotlin
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val dueDate: Long,
    val category: String,
    val priority: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderDaysBefore: Int? = null
)
```

### DAO Principal

- `getPendingActivities()` - Flow de actividades ordenadas por fecha
- `getCompletedActivities()` - Flow de actividades completadas
- `getActivityById(id)` - Obtener actividad específica
- `insertActivity(activity)` - Insertar nueva actividad
- `updateActivity(activity)` - Actualizar actividad existente
- `deleteActivity(activity)` - Eliminar actividad

---

## 📱 Pantallas de la Aplicación

### 1. Home (Pantalla Principal)
- Lista de actividades pendientes ordenadas
- Lista de actividades completadas (opcional)
- Checkbox para marcar como completada
- Botones de editar y eliminar
- FAB para agregar nueva actividad
- Menú con opciones de configuración

### 2. Agregar Actividad
- Formulario completo con validación
- Campos: Título, Descripción, Fecha, Categoría, Prioridad
- Selector de recordatorio (días antes)
- Guardado con programación automática de notificación

### 3. Editar Actividad
- Carga automática de datos existentes
- Actualización con reprogramación de notificaciones
- Validación de campos
- Cancelación de cambios

### 4. Actividades Completadas
- Vista dedicada para completadas
- Restaurar a pendientes
- Eliminación permanente
- Información de progreso

### 5. Configuración
- Selección de tema (Claro/Oscuro/Sistema)
- Ordenamiento de actividades
- Gestión de notificaciones
- Control de servicio en primer plano
- Auto-eliminación de completadas
- Botón de prueba de notificaciones
- Restablecer preferencias

---

## 🔔 Sistema de Notificaciones

### Arquitectura de Notificaciones

```
┌────────────────────────────────────┐
│     Usuario crea/edita actividad   │
└────────────────┬───────────────────┘
                 ↓
┌────────────────────────────────────┐
│      ViewModel guarda actividad    │
└────────────────┬───────────────────┘
                 ↓
┌────────────────────────────────────┐
│   NotificationManager.             │
│   scheduleReminder()               │
└────────────────┬───────────────────┘
                 ↓
        ┌────────┴────────┐
        │  Tiempo pasó?   │
        └────────┬────────┘
         ┌───────┴───────┐
         │ SÍ            │ NO
         ↓               ↓
  ┌──────────────┐  ┌──────────────┐
  │ Notificación │  │ WorkManager  │
  │  inmediata   │  │  programa    │
  └──────────────┘  └──────┬───────┘
                           ↓
                    ┌──────────────┐
                    │ReminderWorker│
                    │  se ejecuta  │
                    └──────┬───────┘
                           ↓
                    ┌──────────────┐
                    │ Notificación │
                    │  programada  │
                    └──────────────┘
```

### Características
- Canal de notificaciones `flowup_reminders`
- Prioridad por defecto (no invasiva)
- PendingIntent para abrir actividad al tocar
- Verificación de permisos Android 13+
- Notificaciones inmediatas si el recordatorio ya pasó
- WorkManager para programación robusta

---

## ⚙️ Servicio en Primer Plano

### Funcionalidad
- **Nombre**: SyncForegroundService
- **Tipo**: `dataSync` (sincronización de datos)
- **Intervalo**: Sincronización cada 60 segundos
- **Notificación**: Persistente de baja prioridad
- **Contador**: Muestra número de sincronizaciones realizadas

### Control
- Iniciar/Detener desde Configuración
- Notificación no descartable durante ejecución
- Reinicio automático con `START_STICKY`
- Cancelación apropiada de coroutines

---

## 🎨 Temas y Personalización

### Modos de Tema
- **Claro**: Paleta de colores claros
- **Oscuro**: Paleta de colores oscuros  
- **Sistema**: Sigue la configuración del dispositivo

### Persistencia
- Preferencia guardada en DataStore
- Aplicación inmediata al cambiar
- ThemeViewModel gestiona el estado global

---

## 🔧 Configuración del Proyecto

### Requisitos Previos
- Android Studio Hedgehog o superior
- JDK 11 o superior
- Android SDK API 24+ (mínimo)
- Android SDK API 35 (compilación)

### Instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd FlowUp
```

2. **Abrir en Android Studio**
```
File → Open → Seleccionar carpeta FlowUp
```

3. **Sincronizar Gradle**
```
File → Sync Project with Gradle Files
```

4. **Compilar y ejecutar**
```bash
# Terminal
./gradlew clean
./gradlew build
./gradlew installDebug

# O usar el botón Run en Android Studio
```

---

## 📋 Permisos

### Declarados en AndroidManifest.xml

```xml
<!-- Notificaciones (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Servicio en primer plano -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### Gestión de Permisos

- **POST_NOTIFICATIONS**: Solicitado al abrir la app (Android 13+)
- **FOREGROUND_SERVICE**: Se concede automáticamente

---

## 🚀 Compilación y Despliegue

### Versiones de Compilación

```kotlin
android {
    compileSdk = 35
    
    defaultConfig {
        applicationId = "com.app.flowup"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
```

### Tipos de Build

```bash
# Debug (desarrollo)
./gradlew assembleDebug

# Release (producción)
./gradlew assembleRelease

# Instalar en dispositivo
./gradlew installDebug
```

---

## 🧪 Pruebas

### Probar Notificaciones

1. **Método rápido**:
   - Ir a Configuración
   - Tocar "Probar Notificaciones"
   - Verificar que aparece la notificación

2. **Método con actividad**:
   - Crear actividad para mañana
   - Recordatorio: 1 día antes
   - Notificación aparece inmediatamente

### Probar Servicio en Primer Plano

1. Ir a Configuración
2. Sección "Servicio de Sincronización"
3. Tocar "Iniciar"
4. Verificar notificación persistente
5. Esperar 1 minuto → contador aumenta
6. Tocar "Detener" → notificación desaparece

---

## 🐛 Problemas Comunes y Soluciones

### 1. Notificaciones no aparecen

**Problema**: Las notificaciones no se muestran.

**Soluciones**:
- Verificar que se concedió el permiso POST_NOTIFICATIONS
- Ir a Configuración → Probar Notificaciones
- Verificar configuración del sistema: Apps → FlowUp → Notificaciones → Habilitadas

### 2. Error de compilación con Hilt

**Problema**: `[Hilt] Processing did not complete`

**Solución**:
```bash
./gradlew clean
# Eliminar carpeta build/
./gradlew build
```

### 3. Error KSP con Room

**Problema**: KSP no genera código de Room

**Solución**:
- Verificar que KSP está en `plugins` block
- Sincronizar Gradle
- Rebuild Project

### 4. Servicio en primer plano no inicia

**Problema**: Crash al iniciar servicio

**Verificar**:
- AndroidManifest tiene `<service>` declarado
- `foregroundServiceType="dataSync"` está presente
- Permiso FOREGROUND_SERVICE declarado

### 5. Tema no cambia

**Problema**: El tema no se aplica

**Solución**:
- Verificar que ThemeViewModel está en MainActivity
- DataStore debe estar inicializado
- Reiniciar la app si es necesario

---

## 📚 Dependencias Clave

### build.gradle.kts (Project)

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
```

### build.gradle.kts (App)

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    kapt(libs.androidx.hilt.work)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
}
```

---

## 🎯 Características Técnicas Implementadas

| Característica | Tecnología | Estado |
|----------------|------------|--------|
| **Persistencia de datos** | Room Database | ✅ Completo |
| **Preferencias de usuario** | DataStore | ✅ Completo |
| **Operaciones asíncronas** | Coroutines + Flow | ✅ Completo |
| **Inyección de dependencias** | Hilt (Dagger) | ✅ Completo |
| **Notificaciones** | NotificationManager + WorkManager | ✅ Completo |
| **Servicio en primer plano** | ForegroundService | ✅ Completo |
| **Navegación** | Navigation Compose | ✅ Completo |
| **UI moderna** | Jetpack Compose + Material 3 | ✅ Completo |
| **Temas dinámicos** | Material Theme + DataStore | ✅ Completo |

---

## 📖 Decisiones de Diseño

### 1. Arquitectura MVVM
- **Razón**: Separación clara entre UI y lógica de negocio
- **Beneficio**: Testeable, mantenible y escalable

### 2. Jetpack Compose
- **Razón**: UI declarativa moderna y reactiva
- **Beneficio**: Menos código boilerplate, desarrollo más rápido

### 3. Room + Flow
- **Razón**: Observabilidad automática de cambios en BD
- **Beneficio**: UI siempre sincronizada con datos

### 4. Hilt
- **Razón**: Inyección de dependencias estándar de Android
- **Beneficio**: Menos código manual, integración con Jetpack

### 5. WorkManager para notificaciones
- **Razón**: Robustez y garantía de ejecución
- **Beneficio**: Sobrevive a reinicios del dispositivo

### 6. DataStore
- **Razón**: Reemplazo moderno de SharedPreferences
- **Beneficio**: Type-safe, asíncrono, observables

---

## 🔄 Flujo de Datos

### Crear Actividad

```
Usuario → AddActivityScreen → AddActivityViewModel
         ↓
    repository.insertActivity(activity)
         ↓
    ActivityDao.insert()
         ↓
    Room Database
         ↓
    notificationManager.scheduleReminder()
         ↓
    WorkManager programa ReminderWorker
```

### Completar Actividad

```
Usuario toca checkbox → HomeScreen → HomeViewModel
                          ↓
                   toggleActivityCompletion()
                          ↓
            repository.updateActivity(completed = true)
                          ↓
                notificationManager.cancelReminder()
                          ↓
                   WorkManager cancela trabajo
```

---

## 📈 Mejoras Futuras Sugeridas

- [ ] Sincronización con backend (Firebase/API REST)
- [ ] Modo offline completo con cola de sincronización
- [ ] Widgets de Android para vista rápida
- [ ] Categorías personalizables
- [ ] Etiquetas y filtros avanzados
- [ ] Estadísticas y gráficos de productividad
- [ ] Exportar/Importar datos (JSON/CSV)
- [ ] Adjuntar archivos a actividades
- [ ] Recordatorios recurrentes
- [ ] Integración con calendario del sistema

---

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

## 👨‍💻 Desarrollo

**Última actualización**: Diciembre 2024  
**Versión**: 1.0  
**Estado**: Funcional y estable

---

## 📞 Contacto

Para preguntas, sugerencias o reportes de errores, por favor crear un issue en el repositorio del proyecto.

---

**FlowUp** - Organiza tu tiempo, maximiza tu productividad.

