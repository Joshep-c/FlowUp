package com.app.flowup.ui.screens.editactivity

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
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla para editar una actividad existente.
 * Similar a AddActivityScreen pero carga datos previos.
 *
 * @param activityId ID de la actividad a editar
 * @param onNavigateBack Callback para volver a la pantalla anterior
 * @param viewModel ViewModel inyectado por Hilt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    activityId: Long,
    onNavigateBack: () -> Unit,
    viewModel: EditActivityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Efectos de UI
    HandleScreenEffects(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onErrorShown = viewModel::clearError
    )

    // Estructura principal
    Scaffold(
        topBar = { ScreenTopBar(onNavigateBack = onNavigateBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingContent()
            uiState.loadError != null -> ErrorContent(
                error = uiState.loadError!!,
                onRetry = onNavigateBack
            )
            else -> ScreenContent(
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

// EFECTOS DE UI

@Composable
private fun HandleScreenEffects(
    uiState: EditActivityUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onErrorShown: () -> Unit
) {
    // Mostrar errores
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onErrorShown()
        }
    }

    // Navegar de vuelta cuando se guarda
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }
}

// ESTRUCTURA PRINCIPAL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("Editar Actividad") },
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

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text("Cargando actividad...")
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun ScreenContent(
    uiState: EditActivityUiState,
    viewModel: EditActivityViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TitleField(
            value = uiState.title,
            onValueChange = viewModel::onTitleChange
        )

        DescriptionField(
            value = uiState.description,
            onValueChange = viewModel::onDescriptionChange
        )

        DatePickerField(
            selectedDate = uiState.dueDate,
            onDateSelected = viewModel::onDueDateChange
        )

        CategorySelector(
            selectedCategory = uiState.category,
            onCategorySelected = viewModel::onCategoryChange
        )

        PrioritySelector(
            selectedPriority = uiState.priority,
            onPrioritySelected = viewModel::onPriorityChange
        )

        ReminderSelector(
            selectedDays = uiState.reminderDaysBefore,
            onDaysSelected = viewModel::onReminderDaysChange
        )

        UpdateButton(
            isSaving = uiState.isSaving,
            onClick = viewModel::updateActivity
        )
    }
}

// CAMPOS DE ENTRADA

@Composable
private fun TitleField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Título *") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun DescriptionField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Descripción") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 5
    )
}

@Composable
private fun DatePickerField(
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { formatDate(it) } ?: "",
        onValueChange = {},
        label = { Text("Fecha de vencimiento *") },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            TextButton(onClick = { showDialog = true }) {
                Text("Cambiar")
            }
        }
    )

    if (showDialog) {
        SimpleDatePickerDialog(
            onDateSelected = {
                onDateSelected(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

// SELECTORES

@Composable
private fun CategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    SelectorSection(
        title = "Categoría",
        options = listOf("WORK", "PERSONAL", "HEALTH", "STUDY", "OTHER"),
        selectedOption = selectedCategory,
        onOptionSelected = onCategorySelected
    )
}

@Composable
private fun PrioritySelector(
    selectedPriority: String,
    onPrioritySelected: (String) -> Unit
) {
    Column {
        Text(
            text = "Prioridad",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("ALTA", "MEDIA", "BAJA").forEach { priority ->
                FilterChip(
                    selected = priority == selectedPriority,
                    onClick = { onPrioritySelected(priority) },
                    label = { Text(priority) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (priority) {
                            "HIGH" -> MaterialTheme.colorScheme.errorContainer
                            "MEDIUM" -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun ReminderSelector(
    selectedDays: Int?,
    onDaysSelected: (Int?) -> Unit
) {
    Column {
        Text(
            text = "Recordatorio",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                null to "Sin recordatorio",
                1 to "1 día",
                3 to "3 días",
                7 to "7 días"
            ).forEach { (days, label) ->
                FilterChip(
                    selected = days == selectedDays,
                    onClick = { onDaysSelected(days) },
                    label = { Text(label) }
                )
            }
        }
    }
}

// COMPONENTES REUTILIZABLES

@Composable
private fun SelectorSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}

@Composable
private fun UpdateButton(
    isSaving: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isSaving,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Actualizar Actividad")
        }
    }
}

// DIÁLOGOS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let(onDateSelected)
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// UTILIDADES

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp)
}

