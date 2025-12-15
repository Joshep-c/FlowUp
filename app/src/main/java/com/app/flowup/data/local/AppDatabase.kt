package com.app.flowup.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de datos principal de la aplicación usando Room.
 *
 * La instancia de esta clase será proporcionada por Hilt
 * a través del módulo DatabaseModule.
 */
@Database(
    entities = [ActivityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Método abstracto para obtener el DAO
    abstract fun activityDao(): ActivityDao
}