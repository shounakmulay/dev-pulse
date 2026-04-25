package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

@Composable
fun TypographyBoard(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(8.dp)) {
        items(DPTextViewVariant.entries) { variant ->
            Column(
                Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DPTextView(text = "Dev Pulse", variant = variant)
                Spacer(Modifier.height(4.dp))
                DPTextView(text = variant.name, variant = DPTextViewVariant.BodyMedium)
                Spacer(Modifier.height(4.dp))
                HorizontalDivider()
            }
        }
    }
}
