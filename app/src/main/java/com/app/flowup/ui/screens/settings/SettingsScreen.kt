package com.app.flowup.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            SettingsContent(
                preferences = uiState.preferences,
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun SettingsContent(
    preferences: com.app.flowup.data.preferences.UserPreferences?,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    if (preferences == null) return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsSection(title = "Apariencia") {
            ThemeSelector(
                currentTheme = preferences.themeMode,
                onThemeSelected = viewModel::setThemeMode
            )
        }

        SettingsSection(title = "Vista Predeterminada") {
            Spacer(modifier = Modifier.height(8.dp))
            SortOrderSelector(
                currentOrder = preferences.sortOrder,
                onOrderSelected = viewModel::setSortOrder
            )
        }

        SettingsSection(title = "Notificaciones") {
            SwitchPreference(
                title = "Habilitar notificaciones",
                subtitle = "Recibe recordatorios de tus actividades",
                checked = preferences.notificationsEnabled,
                onCheckedChange = viewModel::setNotificationsEnabled
            )
            if (preferences.notificationsEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hora: ${String.format("%02d:%02d", preferences.notificationHours, preferences.notificationMinutes)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.testNotification() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Probar Notificaciones")
                }
            }
        }

        SettingsSection(title = "Servicio de Sincronización") {
            Text(
                text = "Mantiene la app sincronizada en segundo plano",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.startForegroundService() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Iniciar")
                }
                Button(
                    onClick = { viewModel.stopForegroundService() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Detener")
                }
            }
        }

        SettingsSection(title = "Avanzado") {
            SwitchPreference(
                title = "Auto-eliminar completadas después de 7 días",
                subtitle = "Por defecto, las tareas completadas se eliminan automáticamente después de 7 días. Desactiva esta opción para mantenerlas indefinidamente.",
                checked = preferences.autoDeleteCompleted,
                onCheckedChange = viewModel::setAutoDeleteCompleted
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.clearAllPreferences() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restablecer Configuración")
            }
        }

        preferences.lastSyncTimestamp?.let { timestamp ->
            Text(
                text = "Última sincronización: $timestamp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeSelector(
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Tema",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("LIGHT" to "Claro", "DARK" to "Oscuro", "SYSTEM" to "Sistema").forEach { (value, label) ->
                FilterChip(
                    selected = currentTheme == value,
                    onClick = { onThemeSelected(value) },
                    label = { Text(label) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SortOrderSelector(
    currentOrder: String,
    onOrderSelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Ordenar por",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                "DATE_ASC" to "Fecha Más Antiguas",
                "DATE_DESC" to "Fecha Más Recientes",
                "PRIORITY_HIGH_FIRST" to "Prioridad"
            ).forEach { (value, label) ->
                FilterChip(
                    selected = currentOrder == value,
                    onClick = { onOrderSelected(value) },
                    label = { Text(label) }
                )
            }
        }
    }
}

@Composable
private fun SwitchPreference(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
