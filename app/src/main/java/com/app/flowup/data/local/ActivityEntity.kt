package com.app.flowup.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val description: String,

    // Requerimiento: Fecha de cumplimiento/entrega
    // Guardamos milisegundos para ordenar fácil
    val dueDate: Long,

    // Requerimiento: Opción de recordar días antes
    // null = sin recordatorio, 1 = 1 día antes, etc.
    val reminderDaysBefore: Int? = null,

    val category: String, // WORK, PERSONAL, etc.
    val priority: String, // HIGH, MEDIUM, LOW

    // Requerimiento: Modificar/Eliminar actividad "pendiente"
    // Usamos esto para filtrar cuáles mostrar
    val isCompleted: Boolean = false
)

enum class Category {
    WORK, PERSONAL, HEALTH, STUDY, OTHER
}

enum class Priority {
    HIGH, MEDIUM, LOW
}