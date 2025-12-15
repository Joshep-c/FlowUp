package com.app.flowup.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ActivityEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Método abstracto para obtener el DAO
    abstract fun activityDao(): ActivityDao

    companion object {
        // Singleton: Solo una instancia en toda la app
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flowup_database"
                )
                    .fallbackToDestructiveMigration() // En desarrollo: borra y recrea si cambia la estructura
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}