package com.app.flowup.navigation

// Constantes de rutas de navegación de la aplicación.

object NavRoutes {

    // Ruta de la pantalla principal (lista de actividades)

    const val HOME = "home"

    // Ruta de la pantalla para agregar una nueva actividad

    const val ADD_ACTIVITY = "add_activity"

    // Ruta de la pantalla para editar una actividad existente
    // Incluye parámetro {activityId}

    const val EDIT_ACTIVITY = "edit_activity/{activityId}"

    // Ruta de la pantalla de configuración

    const val SETTINGS = "settings"

    // Ruta de la pantalla de actividades completadas

    const val COMPLETED_ACTIVITIES = "completed_activities"

    // Función helper para construir la ruta de edición con el ID

    fun editActivity(activityId: Long): String {
        return "edit_activity/$activityId"
    }
}

