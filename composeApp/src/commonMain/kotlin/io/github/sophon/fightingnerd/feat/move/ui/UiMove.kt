package io.github.sophon.fightingnerd.feat.move.ui

import io.github.sophon.core.wiki.model.Move
import io.github.sophon.fightingnerd.feat.move.model.Property
import org.jetbrains.compose.resources.StringResource

internal data class UiMove(
    val move: Move,

    val propertySet: Set<Property> = emptySet(),

    val coreFields: List<Field>,
    val optionalFields: List<Field>,
) {
    data class Field(
        val label: StringResource,
        val value: String?,
    )

    data class CoreFields(
        val startup: Field,
        val guard: Field,
        val damage: Field,
        val onBlock: Field,
        val onHit: Field,
        val onCH: Field,
    )

    fun isExpandable(): Boolean {
        return move.notes.isNotEmpty() || move.urls.videoId.isNullOrEmpty().not()
    }
}
