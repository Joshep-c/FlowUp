package com.app.flowup.ui.screens.settings

import com.app.flowup.data.preferences.UserPreferences

data class SettingsUiState(
    val preferences: UserPreferences? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

