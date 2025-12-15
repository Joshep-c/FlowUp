package com.app.flowup.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.flowup.data.local.ActivityEntity
import com.app.flowup.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// ViewModel para la pantalla principal (Home).
// Gestiona el estado de la UI y la lógica de negocio para las actividades.

class HomeViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    // Estado privado mutable (solo el ViewModel puede modificarlo)
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    // Estado público de solo lectura (la UI lo observa)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Cargar actividades al crear el ViewModel
        loadActivities()
    }

    // Carga las actividades desde el Repository.
    // Observa el Flow de Room para actualizaciones automáticas.

    private fun loadActivities() {
        viewModelScope.launch {
            repository.getPendingActivities()
                .catch { exception ->
                    // Captura errores de Room y los convierte en estado Error
                    _uiState.value = HomeUiState.Error(
                        exception.message ?: "Error desconocido al cargar actividades"
                    )
                }
                .collect { activities ->
                    // Actualiza el estado según si hay actividades o no
                    _uiState.value = if (activities.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(
                            pendingActivities = activities.filter { !it.isCompleted },
                            completedActivities = activities.filter { it.isCompleted }
                        )
                    }
                }
        }
    }

    // Marca una actividad como completada o no completada.
    // @param activity La actividad a actualizar

    fun toggleActivityCompletion(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.updateActivity(
                activity.copy(isCompleted = !activity.isCompleted)
            )
            // No es necesario llamar loadActivities() porque el Flow de Room
            // emite automáticamente los cambios y collect{} los recibe
        }
    }

    // Elimina una actividad de la base de datos.
    // @param activity La actividad a eliminar

    fun deleteActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
            // El Flow actualiza automáticamente la UI
        }
    }

    // Recarga manualmente las actividades (útil para pull-to-refresh).

    fun refreshActivities() {
        _uiState.value = HomeUiState.Loading
        loadActivities()
    }
}

