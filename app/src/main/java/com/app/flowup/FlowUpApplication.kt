package com.app.flowup

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Application personalizada para inicializar Hilt.
// @HiltAndroidApp genera el contenedor de dependencias a nivel de aplicación.
// Todas las inyecciones de dependencias comienzan aquí.

@HiltAndroidApp
class FlowUpApplication : Application()