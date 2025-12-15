package com.app.flowup.ui.screens.addactivity

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

/**
 * ViewModel para la pantalla de agregar actividades.
 * Gestiona el estado del formulario y la lógica de guardado.
 */
@HiltViewModel
class AddActivityViewModel @Inject constructor(
    private val repository: ActivityRepository,
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddActivityUiState())
    val uiState: StateFlow<AddActivityUiState> = _uiState.asStateFlow()

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

    /**
     * Valida y guarda la actividad en la base de datos.
     */
    fun saveActivity() {
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

        // Guardar actividad
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val activity = ActivityEntity(
                    title = state.title.trim(),
                    description = state.description.trim(),
                    dueDate = state.dueDate,
                    category = state.category,
                    priority = state.priority,
                    reminderDaysBefore = state.reminderDaysBefore
                )
                repository.insertActivity(activity)

                // Programar notificación si se especificó recordatorio
                if (state.reminderDaysBefore != null && state.reminderDaysBefore > 0) {
                    notificationManager.scheduleReminder(activity, state.reminderDaysBefore)
                }

                _uiState.update { it.copy(isSaved = true, isSaving = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Error al guardar: ${e.message}",
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

