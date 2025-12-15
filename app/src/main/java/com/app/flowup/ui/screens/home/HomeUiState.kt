package com.app.flowup.ui.screens.home

import com.app.flowup.data.local.ActivityEntity

// Representa todos los posibles estados de la pantalla Home.
// Sealed class garantiza que solo puede estar en uno de estos estados a la vez.

sealed class HomeUiState {

    // Estado inicial: La pantalla está cargando datos desde Room.

    data object Loading : HomeUiState()

    // Estado de éxito: Hay datos disponibles para mostrar.
    // @param pendingActivities Lista de actividades no completadas
    // @param completedActivities Lista de actividades completadas

    data class Success(
        val pendingActivities: List<ActivityEntity>,
        val completedActivities: List<ActivityEntity> = emptyList()
    ) : HomeUiState()

    // Estado de error: Ocurrió un problema al cargar los datos.
    // @param message Mensaje descriptivo del error

    data class Error(val message: String) : HomeUiState()

    // Estado vacío: No hay actividades creadas aún.
    // Útil para mostrar un mensaje de onboarding.

    data object Empty : HomeUiState()
}

