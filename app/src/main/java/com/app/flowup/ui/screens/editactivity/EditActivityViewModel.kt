package com.app.flowup.ui.screens.editactivity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.flowup.data.local.ActivityEntity
import com.app.flowup.data.repository.ActivityRepository
import com.app.flowup.notifications.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel para la pantalla de editar actividades.
// Carga la actividad existente y permite actualizarla.

@HiltViewModel
class EditActivityViewModel @Inject constructor(
    private val repository: ActivityRepository,
    private val notificationManager: NotificationManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val activityId: Long = savedStateHandle.get<Long>("activityId") ?: 0L

    private val _uiState = MutableStateFlow(EditActivityUiState())
    val uiState: StateFlow<EditActivityUiState> = _uiState.asStateFlow()

    init {
        loadActivity()
    }

    // Carga la actividad desde la base de datos.

    private fun loadActivity() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val activity = repository.getActivityById(activityId)

                if (activity != null) {
                    _uiState.update {
                        it.copy(
                            title = activity.title,
                            description = activity.description,
                            dueDate = activity.dueDate,
                            category = activity.category,
                            priority = activity.priority,
                            reminderDaysBefore = activity.reminderDaysBefore,
                            isLoading = false,
                            loadError = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = "No se encontró la actividad"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadError = "Error al cargar: ${e.message}"
                    )
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onDueDateChange(dueDate: Long) {
        _uiState.update { it.copy(dueDate = dueDate) }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onPriorityChange(priority: String) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onReminderDaysChange(days: Int?) {
        _uiState.update { it.copy(reminderDaysBefore = days) }
    }

    // Valida y actualiza la actividad en la base de datos.

    fun updateActivity() {
        val state = _uiState.value

        // Validación
        when {
            state.title.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "El título es obligatorio") }
                return
            }
            state.dueDate == null -> {
                _uiState.update { it.copy(errorMessage = "La fecha de vencimiento es obligatoria") }
                return
            }
        }

        // Actualizar actividad
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val updatedActivity = ActivityEntity(
                    id = activityId,
                    title = state.title.trim(),
                    description = state.description.trim(),
                    dueDate = state.dueDate,
                    category = state.category,
                    priority = state.priority,
                    reminderDaysBefore = state.reminderDaysBefore,
                    isCompleted = false // Mantener como pendiente
                )

                repository.updateActivity(updatedActivity)

                // Cancelar notificación anterior y programar nueva si corresponde
                notificationManager.cancelReminder(activityId)
                if (state.reminderDaysBefore != null && state.reminderDaysBefore > 0) {
                    notificationManager.scheduleReminder(updatedActivity, state.reminderDaysBefore)
                }

                _uiState.update { it.copy(isSaved = true, isSaving = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al actualizar: ${e.message}",
                        isSaving = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

