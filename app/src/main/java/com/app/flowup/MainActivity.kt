package com.app.flowup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.app.flowup.navigation.FlowUpNavGraph
import com.app.flowup.ui.theme.FlowUpTheme
import com.app.flowup.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal de la aplicación.
 *
 * @AndroidEntryPoint: Permite que Hilt inyecte dependencias en esta Activity
 *
 * Gestiona:
 * - El tema de la aplicación (FlowUpTheme) con preferencias dinámicas
 * - El sistema de navegación (NavController + NavGraph)
 * - La configuración Edge-to-Edge
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Observar el modo de tema desde las preferencias
            val themeMode by themeViewModel.themeMode.collectAsState()
            val systemInDarkTheme = isSystemInDarkTheme()

            // Determinar si usar tema oscuro basado en las preferencias
            val darkTheme = when (themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                "SYSTEM" -> systemInDarkTheme
                else -> systemInDarkTheme
            }

            FlowUpTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Controlador de navegación
                    val navController = rememberNavController()

                    // Grafo de navegación con todas las pantallas
                    FlowUpNavGraph(navController = navController)
                }
            }
        }
    }
}