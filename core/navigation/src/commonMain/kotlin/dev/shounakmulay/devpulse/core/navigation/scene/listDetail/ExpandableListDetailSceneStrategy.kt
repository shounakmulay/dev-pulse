package dev.shounakmulay.devpulse.core.navigation.scene.listDetail

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass


class ExpandableListDetailSceneStrategy<T : Any>(
    val windowSizeClass: WindowSizeClass,
    val config: ListDetailScreenStrategyConfig = ExpandableListDetailSceneStrategyDefaults.defaultConfig
) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {

        if (!windowSizeClass.isWidthAtLeastBreakpoint(config.widthBreakpoint)) {
            return null
        }

        val detailEntry =
            entries.lastOrNull()?.takeIf { it.metadata.contains(DetailKey.toString()) }
        val draggable = detailEntry?.metadata[DetailDraggable.toString()] as? Boolean ?: false

        val listEntry = entries.findLast { it.metadata.contains(ListKey.toString()) } ?: return null

        // We use the list's contentKey to uniquely identify the scene.
        // This allows the detail panes to be displayed instantly through recomposition, rather than
        // having NavDisplay animate the whole scene out when the selected detail item changes.
        val sceneKey = listEntry.contentKey

        return ExpandableListDetailScene(
            key = sceneKey,
            previousEntries = entries.dropLast(1),
            listEntry = listEntry,
            detailEntry = detailEntry,
            draggable = draggable
        )
    }

    data object ListKey : NavMetadataKey<Boolean>
    data object DetailKey : NavMetadataKey<Boolean>
    data object DetailDraggable : NavMetadataKey<Boolean>
    companion object {

        fun listPane() = metadata {
            put(ListKey, true)
        }

        fun detailPane(draggable: Boolean = false) = metadata {
            put(DetailKey, true)
            put(DetailDraggable, draggable)
        }
    }
}