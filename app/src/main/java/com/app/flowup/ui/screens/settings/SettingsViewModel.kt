package com.app.flowup.ui.screens.settings

import android.content.Context
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.flowup.data.preferences.PreferencesRepository
import com.app.flowup.service.SyncForegroundService
import com.app.flowup.notifications.NotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import com.app.flowup.service.SyncForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
    @ApplicationContext private val context: Context,

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val notificationManager: NotificationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            preferencesRepository.allPreferences
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error: ${e.message}"
                        )
                    }
                }
                .collect { preferences ->
                    _uiState.update {
                        it.copy(
                            preferences = preferences,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.setThemeMode(mode)
                showSuccessMessage("Tema actualizado")
            } catch (e: Exception) {
                showErrorMessage("Error: ${e.message}")
            }
        }
    }

    fun setDefaultView(view: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.setDefaultView(view)
                showSuccessMessage("Vista actualizada")
            } catch (e: Exception) {
                showErrorMessage("Error: ${e.message}")
            }
        }
    }

    fun setSortOrder(order: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.setSortOrder(order)
                showSuccessMessage("Ordenamiento actualizado")
            } catch (e: Exception) {
                showErrorMessage("Error: ${e.message}")
            }
        }
    }

    fun setShowCompleted(show: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setShowCompleted(show)
            } catch (e: Exception) {
                showErrorMessage("Error: ${e.message}")
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setNotificationsEnabled(enabled)
                showSuccessMessage(if (enabled) "Notificaciones activadas" else "Notificaciones desactivadas")
            } catch (e: Exception) {
                showErrorMessage("Error: ${e.message}")
            }
        }
    }

    fun setNotificationTime(hours: Int, minutes: Int) {
        viewModelScope.launch {
            try {
                preferencesRepository.setNotificationTime(hours, minutes)
                showSuccessMessage("Hora actualizada")
            } catch (e: Exception) {
                showErrorMessage("Error: ${e.message}")
            }
        }
    }

    fun setAutoDeleteCompleted(autoDelete: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setAutoDeleteCompleted(autoDelete)
                showSuccessMessage(if (autoDelete) "Auto-eliminación activada" else "Auto-eliminación desactivada")
            } catch (e: Exception) {
                showErrorMessage("Error: ${e.message}")
            }
        }
    }

    fun clearAllPreferences() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }
                preferencesRepository.clearAllPreferences()
                _uiState.update { it.copy(isSaving = false) }
                showSuccessMessage("Preferencias restablecidas")
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                showErrorMessage("Error: ${e.message}")
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }

    /**
     * Muestra una notificación de prueba para verificar permisos.
     */
    fun testNotification() {
        if (notificationManager.hasPermission()) {
            notificationManager.showTestNotification()
            showSuccessMessage("Notificación enviada")
        } else {
    /**
     * Inicia el servicio en primer plano.
     */
    fun startForegroundService() {
        try {
            SyncForegroundService.start(context)
            showSuccessMessage("Servicio de sincronización iniciado")
        } catch (e: Exception) {
            showErrorMessage("Error al iniciar servicio: ${e.message}")
        }
    }

    /**
     * Detiene el servicio en primer plano.
     */
    fun stopForegroundService() {
        try {
            SyncForegroundService.stop(context)
            showSuccessMessage("Servicio de sincronización detenido")
        } catch (e: Exception) {
            showErrorMessage("Error al detener servicio: ${e.message}")
        }
    }

            showErrorMessage("Permiso de notificaciones no concedido")
        }
    }

    private fun showSuccessMessage(message: String) {
        _uiState.update { it.copy(successMessage = message) }
    }

        _uiState.update { it.copy(errorMessage = message) }
    }
}
