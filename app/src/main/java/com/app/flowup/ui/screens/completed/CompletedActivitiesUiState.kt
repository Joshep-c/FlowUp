package com.app.flowup.ui.screens.completed

import com.app.flowup.data.local.ActivityEntity

/**
 * Estado de la UI para la pantalla de actividades completadas.
 */
sealed class CompletedActivitiesUiState {
    data object Loading : CompletedActivitiesUiState()
    data object Empty : CompletedActivitiesUiState()
    data class Success(
        val completedActivities: List<ActivityEntity>
    ) : CompletedActivitiesUiState()
    data class Error(val message: String) : CompletedActivitiesUiState()
}

