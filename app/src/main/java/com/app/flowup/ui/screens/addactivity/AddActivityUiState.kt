package com.app.flowup.ui.screens.addactivity

/**
 * Estado de la UI para la pantalla de agregar actividad.
 *
 * @property title Título de la actividad
 * @property description Descripción de la actividad
 * @property dueDate Fecha de vencimiento en milisegundos
 * @property category Categoría seleccionada
 * @property priority Prioridad seleccionada
 * @property reminderDaysBefore Días antes para recordatorio (null = sin recordatorio)
 * @property errorMessage Mensaje de error a mostrar
 * @property isSaving Indica si se está guardando
 * @property isSaved Indica si se guardó exitosamente
 */
data class AddActivityUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val category: String = "PERSONAL",
    val priority: String = "MEDIUM",
    val reminderDaysBefore: Int? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

