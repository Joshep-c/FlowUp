package com.app.flowup

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.app.flowup.ui.screens.home.HomeScreen
import com.app.flowup.ui.theme.FlowUpTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal de la aplicación.
 *
 * @AndroidEntryPoint: Permite que Hilt inyecte dependencias en esta Activity
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
                    HomeScreen(
                        onNavigateToAddActivity = {
                            // TODO: Implementar navegación completa con Navigation Compose
                            Toast.makeText(
                                this,
                                "Navegación a AddActivity próximamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}