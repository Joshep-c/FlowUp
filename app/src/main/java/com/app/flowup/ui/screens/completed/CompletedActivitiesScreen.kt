package com.app.flowup.ui.screens.completed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.flowup.data.local.ActivityEntity
import com.app.flowup.ui.components.ActivityCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedActivitiesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CompletedActivitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actividades Completadas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        ScreenContent(
            uiState = uiState,
            onToggleCompletion = viewModel::toggleActivityCompletion,
            onDeleteActivity = viewModel::deleteActivity,
            onRetry = viewModel::refreshActivities,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: CompletedActivitiesUiState,
    onToggleCompletion: (ActivityEntity) -> Unit,
    onDeleteActivity: (ActivityEntity) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is CompletedActivitiesUiState.Loading -> LoadingState()
            is CompletedActivitiesUiState.Empty -> EmptyState()
            is CompletedActivitiesUiState.Success -> ActivitiesContent(
                completedActivities = uiState.completedActivities,
                onToggleCompletion = onToggleCompletion,
                onDeleteActivity = onDeleteActivity
            )
            is CompletedActivitiesUiState.Error -> ErrorState(
                message = uiState.message,
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text("Cargando actividades completadas...")
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "No hay actividades completadas",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Las actividades que completes aparecerán aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Error", style = MaterialTheme.typography.titleLarge)
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

@Composable
private fun ActivitiesContent(
    completedActivities: List<ActivityEntity>,
    onToggleCompletion: (ActivityEntity) -> Unit,
    onDeleteActivity: (ActivityEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Completadas (${completedActivities.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Marca el checkbox para restaurar a pendientes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        items(items = completedActivities, key = { it.id }) { activity ->
            ActivityCard(
                activity = activity,
                onToggleCompletion = { onToggleCompletion(activity) },
                onDelete = { onDeleteActivity(activity) },
                onEdit = {}
            )
        }
    }
}

