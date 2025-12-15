package com.app.flowup.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.flowup.data.local.ActivityEntity
import com.app.flowup.data.preferences.PreferencesRepository
import com.app.flowup.data.repository.ActivityRepository
import com.app.flowup.notifications.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla principal (Home).
 * Gestiona el estado de la UI y la lógica de negocio para las actividades.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ActivityRepository,
    private val preferencesRepository: PreferencesRepository,
    private val notificationManager: NotificationManager
) : ViewModel() {

    // Estado privado mutable (solo el ViewModel puede modificarlo)
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    // Estado público de solo lectura (la UI lo observa)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadActivities()
    }

    /**
     * Carga las actividades desde el Repository.
     * Observa el Flow de Room y las preferencias para actualizaciones automáticas.
     * Filtra las completadas según la preferencia del usuario.
     */
    private fun loadActivities() {
        viewModelScope.launch {
            combine(
                repository.getPendingActivities(),
                preferencesRepository.showCompleted
            ) { activities, showCompleted ->
                Pair(activities, showCompleted)
            }
                .catch { exception ->
                    // Captura errores de Room y los convierte en estado Error
                    _uiState.value = HomeUiState.Error(
                        exception.message ?: "Error desconocido al cargar actividades"
                    )
                }
                .collect { (activities, showCompleted) ->
                    val pendingActivities = activities.filter { !it.isCompleted }
                    val completedActivities = if (showCompleted) {
                        activities.filter { it.isCompleted }
                    } else {
                        emptyList() // No mostrar completadas si está desactivado
                    }

                    // Actualiza el estado según si hay actividades o no
                    _uiState.value = if (pendingActivities.isEmpty() && completedActivities.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(
                            pendingActivities = pendingActivities,
                            completedActivities = completedActivities
                        )
                    }
                }
        }
    }

    /**
     * Marca una actividad como completada o no completada.
     * Cancela la notificación si se completa.
     * @param activity La actividad a actualizar
     */
    fun toggleActivityCompletion(activity: ActivityEntity) {
        viewModelScope.launch {
            val updatedActivity = activity.copy(isCompleted = !activity.isCompleted)
            repository.updateActivity(updatedActivity)

            // Cancelar notificación si se marcó como completada
            if (updatedActivity.isCompleted) {
                notificationManager.cancelReminder(activity.id)
            }
        }
    }

    /**
     * Elimina una actividad de la base de datos.
     * Cancela su notificación programada.
     * @param activity La actividad a eliminar
     */
    fun deleteActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
            notificationManager.cancelReminder(activity.id)
        }
    }

    // Recarga manualmente las actividades (útil para pull-to-refresh).

    fun refreshActivities() {
        _uiState.value = HomeUiState.Loading
        loadActivities()
    }
}

