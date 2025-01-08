package com.example.workouttrackerapp.feature_workout.presentation.settings

import com.example.workouttrackerapp.feature_workout.domain.model.helper.ModalSetting

sealed class SettingsEvent {
    data object ToggleDynamicColorPreference : SettingsEvent()
    data class ToggleOptionsModal(val settingKey : ModalSetting?) : SettingsEvent()
    data class ModalOptionPressed(val selectedOption: String) : SettingsEvent()
    data class UpdateDynamicPrimaryColor(val primaryColor: Int) : SettingsEvent()
}