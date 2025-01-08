package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun RecordsHeading(
    modifier: Modifier = Modifier,
    weightLabel: String,
    isBold: Boolean = true
) {
    val fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
    val fontStyle =
        if (!isBold) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isBold) {
            Icon(
                Icons.Default.Numbers,
                "Sets",
                modifier = Modifier.weight(1f),
            )
        } else {
            Text(
                "#",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = fontWeight,
                style = fontStyle
            )
        }
        Text(
            weightLabel,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = fontWeight,
            style = fontStyle
        )
        Text(
            "REPS",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = fontWeight,
            style = fontStyle
        )
    }
}