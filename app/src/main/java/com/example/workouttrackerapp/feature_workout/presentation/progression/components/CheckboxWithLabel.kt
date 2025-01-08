package com.example.workouttrackerapp.feature_workout.presentation.progression.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun CheckboxWithLabel(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}