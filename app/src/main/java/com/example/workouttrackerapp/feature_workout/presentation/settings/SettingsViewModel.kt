package com.example.workouttrackerapp.feature_workout.presentation.settings

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttrackerapp.di.AppModule
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ModalSetting
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeekStartDay
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeightUnit
import com.example.workouttrackerapp.feature_workout.presentation.widget.UpLiftWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: AppModule.DataStoreManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _settingsUiState = mutableStateOf(SettingsUiState())
    val settingsUiState: State<SettingsUiState> = _settingsUiState


    init {
        viewModelScope.launch {
            dataStoreManager.weightUnitPref.collect { pref ->
                val weightUnitPref = WeightUnit.fromValue(pref)?.displayName
                    ?: WeightUnit.KG.displayName

                if (_settingsUiState.value.settingsModalState.modalSetting == ModalSetting.WEIGHT_UNIT_PREF) {
                    _settingsUiState.value = _settingsUiState.value.copy(
                        settingsModalState = _settingsUiState.value.settingsModalState.copy(
                            selectedOption = weightUnitPref
                        )
                    )
                }

                _settingsUiState.value = _settingsUiState.value.copy(
                    weightUnitPreference = weightUnitPref
                )
            }
        }
        viewModelScope.launch {
            dataStoreManager.dynamicColors.collect { pref ->
                _settingsUiState.value = _settingsUiState.value.copy(
                    dynamicColorPreference = pref
                )
            }
        }
        viewModelScope.launch {
            dataStoreManager.weekStartDay.collect { pref ->
                val weekStartDayPref = WeekStartDay.fromValue(pref)?.displayName
                    ?: WeekStartDay.SUNDAY.displayName

                if (_settingsUiState.value.settingsModalState.modalSetting == ModalSetting.WEEK_START_DAY) {
                    _settingsUiState.value = _settingsUiState.value.copy(
                        settingsModalState = _settingsUiState.value.settingsModalState.copy(
                            selectedOption = weekStartDayPref
                        )
                    )
                }

                _settingsUiState.value = _settingsUiState.value.copy(
                    weekStartDayPref = weekStartDayPref
                )
            }
        }
    }

    fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                UpLiftWidgetReceiver::class.java
            )
        )

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        }

        context.sendBroadcast(intent)
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleDynamicColorPreference -> {
                viewModelScope.launch {
                    dataStoreManager.toggleDynamicColors(!_settingsUiState.value.dynamicColorPreference)
                }
            }

            is SettingsEvent.ToggleOptionsModal -> {
                when (event.settingKey) {
                    ModalSetting.WEEK_START_DAY -> {
                        _settingsUiState.value = _settingsUiState.value.copy(
                            isModalShowing = true,
                            settingsModalState = SettingModalState(
                                modalSetting = ModalSetting.WEEK_START_DAY,
                                title = "Start Of Week",
                                options = WeekStartDay.entries.map { it.displayName },
                                selectedOption = _settingsUiState.value.weekStartDayPref
                            )
                        )
                    }

                    ModalSetting.WEIGHT_UNIT_PREF -> {
                        _settingsUiState.value = _settingsUiState.value.copy(
                            isModalShowing = true,
                            settingsModalState = SettingModalState(
                                modalSetting = ModalSetting.WEIGHT_UNIT_PREF,
                                title = "Weight Unit",
                                options = WeightUnit.entries.map { it.displayName },
                                selectedOption = _settingsUiState.value.weightUnitPreference
                            )
                        )
                    }

                    null -> {
                        _settingsUiState.value = _settingsUiState.value.copy(
                            isModalShowing = false,
                            settingsModalState = SettingModalState()
                        )
                    }
                }
            }

            is SettingsEvent.ModalOptionPressed -> {
                when (_settingsUiState.value.settingsModalState.modalSetting) {
                    ModalSetting.WEEK_START_DAY -> {
                        viewModelScope.launch {
                            dataStoreManager.setWeekStartDay(WeekStartDay.fromDisplayName(event.selectedOption)!!.value)
                        }
                    }

                    ModalSetting.WEIGHT_UNIT_PREF -> {
                        viewModelScope.launch {
                            dataStoreManager.setWeightUnitPref(WeightUnit.fromDisplayName(event.selectedOption)!!.value)
                        }
                    }

                    null -> TODO()
                }
            }

            is SettingsEvent.UpdateDynamicPrimaryColor -> {
                viewModelScope.launch {
                    dataStoreManager.setPrimaryDynamicColor(event.primaryColor)
                    updateWidget()
                }

            }
        }
    }
}

data class SettingsUiState(
    val weightUnitPreference: String = WeightUnit.KG.displayName,
    val dynamicColorPreference: Boolean = false,
    val weekStartDayPref: String = WeekStartDay.SUNDAY.displayName,
    val isModalShowing: Boolean = false,
    val settingsModalState: SettingModalState = SettingModalState()
)

data class SettingModalState(
    val modalSetting: ModalSetting? = null,
    val title: String = "",
    val options: List<String> = emptyList(),
    val selectedOption: String = ""
)