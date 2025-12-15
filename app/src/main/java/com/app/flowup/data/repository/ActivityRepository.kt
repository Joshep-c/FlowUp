package com.app.flowup.data.repository

import com.app.flowup.data.local.ActivityDao
import com.app.flowup.data.local.ActivityEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository que actúa como única fuente de verdad para los datos de actividades.
 * Abstrae la fuente de datos (Room) del resto de la aplicación.
 *
 * @Inject constructor: Permite que Hilt inyecte automáticamente el DAO
 * @Singleton: Asegura que solo exista una instancia del repository
 */
@Singleton
class ActivityRepository @Inject constructor(
    private val activityDao: ActivityDao
) {

    /**
     * Obtiene todas las actividades pendientes ordenadas por fecha de vencimiento.
     * Retorna un Flow que se actualiza automáticamente cuando hay cambios en la BD.
     */
    fun getPendingActivities(): Flow<List<ActivityEntity>> {
        return activityDao.getPendingActivities()
    }

    /**
     * Inserta una nueva actividad en la base de datos.
     */
    suspend fun insertActivity(activity: ActivityEntity) {
        activityDao.insertActivity(activity)
    }

    /**
     * Actualiza una actividad existente.
     */
    suspend fun updateActivity(activity: ActivityEntity) {
        activityDao.updateActivity(activity)
    }

    /**
     * Elimina una actividad de la base de datos.
     */
    suspend fun deleteActivity(activity: ActivityEntity) {
        activityDao.deleteActivity(activity)
    }

    /**
     * Obtiene una actividad específica por su ID.
     * Útil para cargar los datos al editar.
     */
    suspend fun getActivityById(id: Long): ActivityEntity? {
        return activityDao.getActivityById(id)
    }
}

