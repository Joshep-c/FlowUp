package com.app.flowup.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.flowup.data.local.ActivityEntity
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Componente reutilizable para mostrar una actividad individual.
 * Muestra toda la información de la actividad incluyendo categoría, prioridad y recordatorio.
 *
 * @param activity La actividad a mostrar
 * @param onToggleCompletion Callback cuando se marca/desmarca como completada
 * @param onEdit Callback cuando se edita la actividad
 * @param onDelete Callback cuando se elimina la actividad
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActivityCard(
    activity: ActivityEntity,
    onToggleCompletion: (ActivityEntity) -> Unit,
    onEdit: () -> Unit = {},
    onDelete: (ActivityEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox para marcar como completada
            Checkbox(
                checked = activity.isCompleted,
                onCheckedChange = { onToggleCompletion(activity) },
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Información de la actividad
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (activity.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Descripción
                if (activity.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fecha de vencimiento
                Text(
                    text = "${formatDate(activity.dueDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Chips de categoría, prioridad y recordatorio
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Chip de categoría
                    AssistChip(
                        onClick = { },
                        label = { Text(getCategoryLabel(activity.category)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = getCategoryColor(activity.category)
                        )
                    )

                    // Chip de prioridad
                    AssistChip(
                        onClick = { },
                        label = { Text(getPriorityLabel(activity.priority)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = getPriorityColor(activity.priority)
                        )
                    )

                    // Chip de recordatorio (si existe)
                    activity.reminderDaysBefore?.let { days ->
                        AssistChip(
                            onClick = { },
                            label = { Text("$days día${if (days > 1) "s" else ""} antes") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            // Botones de acción (Editar y Eliminar)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botón de editar (solo para actividades pendientes)
                if (!activity.isCompleted) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar actividad",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Botón de eliminar
                IconButton(
                    onClick = { onDelete(activity) },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar actividad",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

            }
        }
    }
}

// FUNCIONES HELPER

// Formatea la fecha para mostrar de forma legible.

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(timestamp)
}

// Obtiene la etiqueta visual de la categoría.

@Composable
private fun getCategoryLabel(category: String): String {
    return when (category.uppercase()) {
        "WORK" -> "Trabajo"
        "PERSONAL" -> "Personal"
        "HEALTH" -> "Salud"
        "STUDY" -> "Estudio"
        "OTHER" -> "Otro"
        else -> category
    }
}

// Obtiene el color de fondo para la categoría.

@Composable
private fun getCategoryColor(category: String) = when (category.uppercase()) {
    "WORK" -> MaterialTheme.colorScheme.primaryContainer
    "PERSONAL" -> MaterialTheme.colorScheme.secondaryContainer
    "HEALTH" -> MaterialTheme.colorScheme.tertiaryContainer
    "STUDY" -> MaterialTheme.colorScheme.surfaceVariant
    else -> MaterialTheme.colorScheme.surfaceVariant
}

// Obtiene la etiqueta visual de la prioridad.

@Composable
private fun getPriorityLabel(priority: String): String {
    return when (priority.uppercase()) {
        "HIGH" -> "Alta"
        "MEDIUM" -> "Media"
        "LOW" -> "Baja"
        else -> priority
    }
}

// Obtiene el color de fondo para la prioridad.

@Composable
private fun getPriorityColor(priority: String) = when (priority.uppercase()) {
    "HIGH" -> MaterialTheme.colorScheme.errorContainer
    "MEDIUM" -> MaterialTheme.colorScheme.tertiaryContainer
    "LOW" -> MaterialTheme.colorScheme.surfaceVariant
    else -> MaterialTheme.colorScheme.surfaceVariant
}
