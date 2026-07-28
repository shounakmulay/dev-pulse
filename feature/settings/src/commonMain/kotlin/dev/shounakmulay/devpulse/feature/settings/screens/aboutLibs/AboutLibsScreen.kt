package dev.shounakmulay.devpulse.feature.settings.screens.aboutLibs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.resources.stringRes
import dev.shounakmulay.devpulse.core.ui.button.DPBackNavigationIconButton
import devpulse.core.resources.generated.resources.Res
import devpulse.core.resources.generated.resources.licenses
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutLibsScreen(navigator: Navigator) {
    val libraries by produceLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }
    Scaffold(
        topBar = {
            DPTopAppBar(
                title = stringResource(stringRes.licenses),
                navigationIcon = {
                    DPBackNavigationIconButton(onNavigateBack = navigator::navigateBack)
                }
            )
        }
    ) {
        LibrariesContainer(
            modifier = Modifier.padding(it),
            libraries = libraries
        )
    }
}