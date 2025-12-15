package com.app.flowup.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    // 1. Obtener actividades pendientes ordenadas por fecha (Requerimiento principal)
    // Usamos Flow para que la UI se actualice sola si hay cambios
    @Query("SELECT * FROM activities WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getPendingActivities(): Flow<List<ActivityEntity>>

    // 2. Obtener actividades completadas ordenadas por fecha
    @Query("SELECT * FROM activities WHERE isCompleted = 1 ORDER BY dueDate DESC")
    fun getCompletedActivities(): Flow<List<ActivityEntity>>

    // 3. Insertar nueva actividad
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    // 4. Modificar actividad
    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    // 5. Eliminar actividad
    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    // 6. Obtener una por ID (útil para cuando entres a la pantalla de "Editar")
    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getActivityById(id: Long): ActivityEntity?
}