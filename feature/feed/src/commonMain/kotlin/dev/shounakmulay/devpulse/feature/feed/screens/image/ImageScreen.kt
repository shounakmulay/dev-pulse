package dev.shounakmulay.devpulse.feature.feed.screens.image

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import dev.shounakmulay.devpulse.core.designsystem.theme.LocalDPDarkTheme
import dev.shounakmulay.devpulse.core.designsystem.theme.darkScheme
import dev.shounakmulay.devpulse.core.designsystem.theme.lightScheme
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.feature.feed.screens.image.components.ImageScreenTopAppBar
import dev.shounakmulay.devpulse.feature.feed.screens.image.components.ImageView
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import kotlin.math.absoluteValue

@Composable
fun ImageScreen(url: String, navigator: Navigator) {
    val zoomableState = rememberZoomState()
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }
    val darkTheme = LocalDPDarkTheme.current
    var isDarkTheme by rememberSaveable {
        mutableStateOf(darkTheme)
    }
    val backgroundColor by animateColorAsState(
        if (isDarkTheme) darkScheme.background else lightScheme.background
    )
    Scaffold(
        containerColor = backgroundColor,
        modifier = Modifier.fillMaxSize()
            .pointerInput(zoomableState.scale) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        isDragging = false
                        if (zoomableState.scale <= 1.0f && offsetY.value.absoluteValue > 200f) {
                            navigator.navigateBack()
                        } else {
                            scope.launch { offsetY.animateTo(0f) }
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        if (zoomableState.scale <= 1.0f) {
                            isDragging = true
                            change.consume()
                            scope.launch {
                                offsetY.snapTo(offsetY.value + dragAmount)
                            }
                        }
                    }
                )
            },
        topBar = {
            ImageScreenTopAppBar(
                isDarkTheme = isDarkTheme,
                onDarkThemeChanged = {
                    isDarkTheme = !isDarkTheme
                },
                onBack = {
                    navigator.navigateBack()
                }
            )
        }
    ) {
        ImageView(
            url = url,
            isDragging = isDragging,
            scaffoldPadding = it,
            zoomableState = zoomableState,
            offsetY = offsetY,
        )
    }
}



