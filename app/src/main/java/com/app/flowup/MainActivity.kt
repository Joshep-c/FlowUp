package com.app.flowup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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
 * - Solicitud de permisos de notificaciones (Android 13+)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    // Launcher para solicitar permiso de notificaciones
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // El permiso se concedió o denegó
        // No necesitamos hacer nada especial aquí para MVP
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicitar permiso de notificaciones si es necesario
        requestNotificationPermission()

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

    /**
     * Solicita permiso de notificaciones para Android 13+.
     * Solo solicita si aún no se ha concedido.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}