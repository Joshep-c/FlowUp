package com.app.flowup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.app.flowup.navigation.FlowUpNavGraph
import com.app.flowup.ui.theme.FlowUpTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal de la aplicación.
 *
 * @AndroidEntryPoint: Permite que Hilt inyecte dependencias en esta Activity
 *
 * Para:
 * - El tema de la aplicación (FlowUpTheme)
 * - El sistema de navegación (NavController + NavGraph)
 * - La configuración Edge-to-Edge
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FlowUpTheme {
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