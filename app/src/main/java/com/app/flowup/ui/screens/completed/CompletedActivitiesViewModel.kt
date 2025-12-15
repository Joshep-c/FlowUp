package com.app.flowup.ui.screens.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.flowup.data.local.ActivityEntity
import com.app.flowup.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de actividades completadas.
 */
@HiltViewModel
class CompletedActivitiesViewModel @Inject constructor(
    private val repository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CompletedActivitiesUiState>(CompletedActivitiesUiState.Loading)
    val uiState: StateFlow<CompletedActivitiesUiState> = _uiState.asStateFlow()

    init {
        loadCompletedActivities()
    }

    private fun loadCompletedActivities() {
        viewModelScope.launch {
            repository.getCompletedActivities()
                .catch { e ->
                    _uiState.value = CompletedActivitiesUiState.Error(
                        message = "Error al cargar actividades: ${e.message}"
                    )
                }
                .collect { activities ->
                    _uiState.value = if (activities.isEmpty()) {
                        CompletedActivitiesUiState.Empty
                    } else {
                        CompletedActivitiesUiState.Success(activities)
                    }
                }
        }
    }

    fun toggleActivityCompletion(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.updateActivity(
                activity.copy(isCompleted = !activity.isCompleted)
            )
        }
    }

    fun deleteActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
        }
    }

    fun refreshActivities() {
        loadCompletedActivities()
    }
}

