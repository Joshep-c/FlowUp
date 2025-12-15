package com.app.flowup.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Extension para crear el DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "flowup_preferences")

/**
 * Repositorio para manejar las preferencias de la aplicación usando DataStore.
 *
 * Almacena configuraciones del usuario como:
 * - Tema (claro/oscuro)
 * - Vista predeterminada (todas/pendientes/completadas)
 * - Notificaciones habilitadas
 * - Ordenamiento preferido
 * - Última sincronización
 */
@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // ==========================================
    // KEYS (Claves de preferencias)
    // ==========================================

    companion object {
        // UI Preferences
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_VIEW = stringPreferencesKey("default_view")
        val SORT_ORDER = stringPreferencesKey("sort_order")

        // Notification Preferences
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_TIME_HOURS = intPreferencesKey("notification_time_hours")
        val NOTIFICATION_TIME_MINUTES = intPreferencesKey("notification_time_minutes")

        // App Preferences
        val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
        val AUTO_DELETE_COMPLETED = booleanPreferencesKey("auto_delete_completed")
        val LAST_SYNC_TIMESTAMP = stringPreferencesKey("last_sync_timestamp")

        // Default values
        const val DEFAULT_THEME = "SYSTEM" // LIGHT, DARK, SYSTEM
        const val DEFAULT_VIEW_FILTER = "PENDING" // ALL, PENDING, COMPLETED
        const val DEFAULT_SORT_ORDER = "DATE_ASC" // DATE_ASC, DATE_DESC, PRIORITY_HIGH_FIRST
        const val DEFAULT_NOTIFICATION_HOUR = 9
        const val DEFAULT_NOTIFICATION_MINUTE = 0
    }

    // ==========================================
    // THEME PREFERENCES
    // ==========================================

    /**
     * Obtiene el modo de tema preferido
     */
    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: DEFAULT_THEME
    }

    /**
     * Guarda el modo de tema
     */
    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    // ==========================================
    // VIEW PREFERENCES
    // ==========================================

    /**
     * Obtiene el filtro de vista predeterminado
     */
    val defaultView: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_VIEW] ?: DEFAULT_VIEW_FILTER
    }

    /**
     * Guarda el filtro de vista predeterminado
     */
    suspend fun setDefaultView(view: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_VIEW] = view
        }
    }

    /**
     * Obtiene el orden de clasificación
     */
    val sortOrder: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SORT_ORDER] ?: DEFAULT_SORT_ORDER
    }

    /**
     * Guarda el orden de clasificación
     */
    suspend fun setSortOrder(order: String) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER] = order
        }
    }

    /**
     * Obtiene si mostrar actividades completadas
     */
    val showCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SHOW_COMPLETED] ?: true
    }

    /**
     * Guarda si mostrar actividades completadas
     */
    suspend fun setShowCompleted(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_COMPLETED] = show
        }
    }

    // ==========================================
    // NOTIFICATION PREFERENCES
    // ==========================================

    /**
     * Obtiene si las notificaciones están habilitadas
     */
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    /**
     * Guarda si las notificaciones están habilitadas
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    /**
     * Obtiene la hora de notificación (hora del día)
     */
    val notificationTimeHours: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATION_TIME_HOURS] ?: DEFAULT_NOTIFICATION_HOUR
    }

    /**
     * Obtiene los minutos de la hora de notificación
     */
    val notificationTimeMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATION_TIME_MINUTES] ?: DEFAULT_NOTIFICATION_MINUTE
    }

    /**
     * Guarda la hora de notificación
     */
    suspend fun setNotificationTime(hours: Int, minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_TIME_HOURS] = hours
            preferences[NOTIFICATION_TIME_MINUTES] = minutes
        }
    }

    // ==========================================
    // APP PREFERENCES
    // ==========================================

    /**
     * Obtiene si auto-eliminar actividades completadas
     */
    val autoDeleteCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_DELETE_COMPLETED] ?: false
    }

    /**
     * Guarda si auto-eliminar actividades completadas
     */
    suspend fun setAutoDeleteCompleted(autoDelete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_DELETE_COMPLETED] = autoDelete
        }
    }

    /**
     * Obtiene el timestamp de la última sincronización
     */
    val lastSyncTimestamp: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_SYNC_TIMESTAMP]
    }

    /**
     * Guarda el timestamp de la última sincronización
     */
    suspend fun setLastSyncTimestamp(timestamp: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIMESTAMP] = timestamp
        }
    }

    // ==========================================
    // UTILITY FUNCTIONS
    // ==========================================

    /**
     * Limpia todas las preferencias (útil para testing o reset)
     */
    suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Obtiene todas las preferencias como un objeto combinado
     */
    val allPreferences: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            themeMode = preferences[THEME_MODE] ?: DEFAULT_THEME,
            defaultView = preferences[DEFAULT_VIEW] ?: DEFAULT_VIEW_FILTER,
            sortOrder = preferences[SORT_ORDER] ?: DEFAULT_SORT_ORDER,
            notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
            notificationHours = preferences[NOTIFICATION_TIME_HOURS] ?: DEFAULT_NOTIFICATION_HOUR,
            notificationMinutes = preferences[NOTIFICATION_TIME_MINUTES] ?: DEFAULT_NOTIFICATION_MINUTE,
            showCompleted = preferences[SHOW_COMPLETED] ?: true,
            autoDeleteCompleted = preferences[AUTO_DELETE_COMPLETED] ?: false,
            lastSyncTimestamp = preferences[LAST_SYNC_TIMESTAMP]
        )
    }
}

/**
 * Data class que representa todas las preferencias del usuario
 */
data class UserPreferences(
    val themeMode: String,
    val defaultView: String,
    val sortOrder: String,
    val notificationsEnabled: Boolean,
    val notificationHours: Int,
    val notificationMinutes: Int,
    val showCompleted: Boolean,
    val autoDeleteCompleted: Boolean,
    val lastSyncTimestamp: String?
)

