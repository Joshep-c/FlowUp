package com.app.flowup.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.flowup.data.local.ActivityEntity
import com.app.flowup.ui.components.ActivityCard

// 1. COMPOSABLE PÚBLICO (Entry Point)

/**
 * Pantalla principal que muestra la lista de actividades.
 * Entry point de la pantalla, gestiona navegación y ViewModel.
 *
 * @param onNavigateToAddActivity Callback para navegar a la pantalla de agregar
 * @param onNavigateToEditActivity Callback para navegar a la pantalla de editar
 * @param onNavigateToSettings Callback para navegar a configuración
 * @param viewModel ViewModel inyectado por Hilt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddActivity: () -> Unit,
    onNavigateToEditActivity: (Long) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estructura principal
    Scaffold(
        topBar = { ScreenTopBar(onNavigateToSettings = onNavigateToSettings) },
        floatingActionButton = {
            AddActivityFab(onClick = onNavigateToAddActivity)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        ScreenContent(
            uiState = uiState,
            onToggleCompletion = viewModel::toggleActivityCompletion,
            onDeleteActivity = viewModel::deleteActivity,
            onEditActivity = onNavigateToEditActivity,
            onRetry = viewModel::refreshActivities,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// 2. ESTRUCTURA PRINCIPAL (Layout)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenTopBar(onNavigateToSettings: () -> Unit) {
    TopAppBar(
        title = { Text("FlowUp") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración"
                )
            }
        }
    )
}

@Composable
private fun AddActivityFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar actividad"
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: HomeUiState,
    onToggleCompletion: (ActivityEntity) -> Unit,
    onDeleteActivity: (ActivityEntity) -> Unit,
    onEditActivity: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is HomeUiState.Loading -> LoadingState()
            is HomeUiState.Empty -> EmptyState()
            is HomeUiState.Success -> ActivitiesContent(
                pendingActivities = uiState.pendingActivities,
                completedActivities = uiState.completedActivities,
                onToggleCompletion = onToggleCompletion,
                onDeleteActivity = onDeleteActivity,
                onEditActivity = onEditActivity
            )
            is HomeUiState.Error -> ErrorState(
                message = uiState.message,
                onRetry = onRetry
            )
        }
    }
}

// 3. LISTA DE ACTIVIDADES

@Composable
private fun ActivitiesContent(
    pendingActivities: List<ActivityEntity>,
    completedActivities: List<ActivityEntity>,
    onToggleCompletion: (ActivityEntity) -> Unit,
    onDeleteActivity: (ActivityEntity) -> Unit,
    onEditActivity: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sección: Actividades Pendientes
        if (pendingActivities.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Pendientes (${pendingActivities.size})",
                    emoji = "📋"
                )
            }

            items(
                items = pendingActivities,
                key = { it.id }
            ) { activity ->
                ActivityCard(
                    activity = activity,
                    onToggleCompletion = { onToggleCompletion(activity) },
                    onDelete = { onDeleteActivity(activity) },
                    onEdit = { onEditActivity(activity.id) }
                )
            }
        }

        // Sección: Actividades Completadas
        if (completedActivities.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(
                    title = "Completadas (${completedActivities.size})",
                    emoji = "✅"
                )
            }

            items(
                items = completedActivities,
                key = { it.id }
            ) { activity ->
                ActivityCard(
                    activity = activity,
                    onToggleCompletion = { onToggleCompletion(activity) },
                    onDelete = { onDeleteActivity(activity) },
                    onEdit = { onEditActivity(activity.id) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// 4. ESTADOS (Loading/Empty/Error)

@Composable
private fun LoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Cargando actividades...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "📋",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No hay actividades",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Comienza a organizar tu tiempo\nagregando tu primera actividad",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error al cargar",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Reintentar")
        }
    }
}

