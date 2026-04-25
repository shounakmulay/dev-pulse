package dev.shounakmulay.devpulse.core.designsystem.components.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shounakmulay.devpulse.core.designsystem.components.DPLeadingIconTab
import dev.shounakmulay.devpulse.core.designsystem.components.DPNavigationBar
import dev.shounakmulay.devpulse.core.designsystem.components.DPNavigationBarItem
import dev.shounakmulay.devpulse.core.designsystem.components.DPTab
import dev.shounakmulay.devpulse.core.designsystem.components.DPTabRow
import dev.shounakmulay.devpulse.core.designsystem.components.DPTabRowVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBar
import dev.shounakmulay.devpulse.core.designsystem.components.DPTopAppBarVariant
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextView
import dev.shounakmulay.devpulse.core.designsystem.components.DPTextViewVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBoard(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            DPTextView(text = "Top App Bar – variants", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))
            DPTopAppBarVariant.entries.forEach { variant ->
                DPTopAppBar(
                    title = "Page title",
                    subtitle = "Subtitle",
                    variant = variant,
                    navigationIcon = Icons.Default.Home,
                    onNavigationClick = {},
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            DPTextView(text = "Navigation Bar", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))
            var selectedNav by remember { mutableIntStateOf(0) }
            DPNavigationBar(Modifier.fillMaxWidth()) {
                DPNavigationBarItem(
                    text = "Home",
                    icon = Icons.Default.Home,
                    selected = selectedNav == 0,
                    onClick = { selectedNav = 0 },
                )
                DPNavigationBarItem(
                    text = "Search",
                    icon = Icons.Default.Search,
                    selected = selectedNav == 1,
                    onClick = { selectedNav = 1 },
                )
                DPNavigationBarItem(
                    text = "Inbox",
                    icon = Icons.Default.Mail,
                    selected = selectedNav == 2,
                    onClick = { selectedNav = 2 },
                    badgeText = "3",
                )
                DPNavigationBarItem(
                    text = "Settings",
                    icon = Icons.Default.Settings,
                    selected = selectedNav == 3,
                    onClick = { selectedNav = 3 },
                )
            }
        }

        item {
            DPTextView(text = "Tab Row – Primary", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))
            var selectedTab by remember { mutableIntStateOf(0) }
            val tabs = listOf("Overview", "Details", "Reviews")
            DPTabRow(
                selectedTabIndex = selectedTab,
                variant = DPTabRowVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
            ) {
                tabs.forEachIndexed { index, title ->
                    DPTab(
                        text = title,
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                    )
                }
            }
        }

        item {
            DPTextView(text = "Tab Row – Secondary", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))
            var selectedTab by remember { mutableIntStateOf(1) }
            val tabs = listOf("Alpha", "Beta", "Gamma")
            DPTabRow(
                selectedTabIndex = selectedTab,
                variant = DPTabRowVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            ) {
                tabs.forEachIndexed { index, title ->
                    DPTab(
                        text = title,
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = Icons.Default.Home,
                    )
                }
            }
        }

        item {
            DPTextView(text = "Tab Row – Scrollable Primary", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))
            var selectedTab by remember { mutableIntStateOf(0) }
            val tabs = listOf("One", "Two", "Three", "Four", "Five", "Six", "Seven")
            DPTabRow(
                selectedTabIndex = selectedTab,
                variant = DPTabRowVariant.ScrollablePrimary,
                modifier = Modifier.fillMaxWidth(),
            ) {
                tabs.forEachIndexed { index, title ->
                    DPTab(
                        text = title,
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                    )
                }
            }
        }

        item {
            DPTextView(text = "Leading Icon Tab", variant = DPTextViewVariant.BodyMedium, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))
            var selectedTab by remember { mutableIntStateOf(0) }
            val tabs = listOf("Inbox" to Icons.Default.Mail, "Settings" to Icons.Default.Settings)
            DPTabRow(
                selectedTabIndex = selectedTab,
                variant = DPTabRowVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
            ) {
                tabs.forEachIndexed { index, (title, icon) ->
                    DPLeadingIconTab(
                        text = title,
                        icon = icon,
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                    )
                }
            }
        }
    }
}
