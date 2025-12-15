package com.app.flowup.di

import android.content.Context
import androidx.room.Room
import com.app.flowup.data.local.ActivityDao
import com.app.flowup.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer dependencias relacionadas con la base de datos.
 *
 * @Module: Marca esta clase como un módulo de Hilt
 * @InstallIn(SingletonComponent::class): Indica que estas dependencias vivirán
 * durante todo el ciclo de vida de la aplicación (singleton)
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provee una instancia única de la base de datos.
     *
     * @Provides: Indica que este método provee una dependencia
     * @Singleton: Asegura que solo exista una instancia en toda la app
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "flowup_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provee el DAO de actividades.
     * Hilt automáticamente inyectará la database cuando se necesite este DAO.
     */
    @Provides
    @Singleton
    fun provideActivityDao(database: AppDatabase): ActivityDao {
        return database.activityDao()
    }
}

