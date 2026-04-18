package dev.shounakmulay.core.designsystem.components.devtools

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MotionBoard(modifier: Modifier = Modifier) {
    var toggled by remember { mutableStateOf(false) }
    val motion = MaterialTheme.motionScheme

    val slowSize: Dp by animateDpAsState(
        targetValue = if (toggled) 120.dp else 48.dp,
        animationSpec = motion.slowSpatialSpec(),
    )
    val defaultSize: Dp by animateDpAsState(
        targetValue = if (toggled) 120.dp else 48.dp,
        animationSpec = motion.defaultSpatialSpec(),
    )
    val fastSize: Dp by animateDpAsState(
        targetValue = if (toggled) 120.dp else 48.dp,
        animationSpec = motion.fastSpatialSpec(),
    )

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text("Spatial Motion Specs", style = MaterialTheme.typography.titleMedium)
        Text(
            "Tap toggle to animate all boxes between 48 dp and 120 dp.",
            style = MaterialTheme.typography.bodySmall,
        )

        Button(onClick = { toggled = !toggled }, modifier = Modifier.fillMaxWidth()) {
            Text(if (toggled) "Reset" else "Toggle")
        }

        Spacer(Modifier.height(8.dp))

        MotionRow(label = "slowSpatialSpec", size = slowSize)
        MotionRow(label = "defaultSpatialSpec", size = defaultSize)
        MotionRow(label = "fastSpatialSpec", size = fastSize)
    }
}

@Composable
private fun MotionRow(label: String, size: Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.tertiary),
        )
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}
