package com.example.workouttrackerapp.feature_workout.presentation.settings

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ModalSetting
import com.example.workouttrackerapp.feature_workout.presentation.settings.components.SettingRadioDialog
import com.example.workouttrackerapp.feature_workout.presentation.settings.components.SettingsRadioModalItem
import com.example.workouttrackerapp.feature_workout.presentation.settings.components.SettingsSwitchItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState = viewModel.settingsUiState.value
    val primaryColor = MaterialTheme.colorScheme.primary

    SideEffect {
        viewModel.onEvent(SettingsEvent.UpdateDynamicPrimaryColor(primaryColor.toArgb()))
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back button")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(dimensionResource(R.dimen.padding_extra_small))
                .verticalScroll(rememberScrollState()),
        ) {
            SettingsSwitchItem(
                text = "Dynamic Colour",
                description = "Switch between default colors, or dynamic colors taken from your device's theme",
                switchState = settingsUiState.dynamicColorPreference,
                onSwitchChange = {
                    viewModel.onEvent(SettingsEvent.ToggleDynamicColorPreference)
                    Log.d("SettingsScreen", primaryColor.toArgb().toString())
                }
            )
            SettingsRadioModalItem(
                text = "Weight Unit",
                description = "Switch between kg or lb",
                currentSelection = settingsUiState.weightUnitPreference,
                toggleModal = {
                    viewModel.onEvent(SettingsEvent.ToggleOptionsModal(ModalSetting.WEIGHT_UNIT_PREF))
                }
            )
            SettingsRadioModalItem(
                text = "Start Of Week",
                description = "Select what day will be the start of your week",
                currentSelection = settingsUiState.weekStartDayPref,
                toggleModal = {
                    viewModel.onEvent(SettingsEvent.ToggleOptionsModal(ModalSetting.WEEK_START_DAY))
                }
            )
        }
    }

    if (settingsUiState.isModalShowing && settingsUiState.settingsModalState.modalSetting != null) {
        SettingRadioDialog(
            onDismissRequest = {
                viewModel.onEvent(SettingsEvent.ToggleOptionsModal(null))
            },
            title = settingsUiState.settingsModalState.title,
            optionsList = settingsUiState.settingsModalState.options,
            onOptionPress = {
                viewModel.onEvent(SettingsEvent.ModalOptionPressed(it))
            },
            selectedOption = settingsUiState.settingsModalState.selectedOption,
        )
    }
}