package io.github.sophon.fightingnerd.feat.move.ui

import io.github.sophon.fightingnerd.feat.move.model.Property
import org.jetbrains.compose.resources.StringResource

internal data class UiMove(
    val id: String,
    val input: String,
    val name: String?,

    val propertySet: Set<Property> = emptySet(),
    val coreFields: List<Field>,
    val optionalFields: List<Field>,
    val notes: List<String> = emptyList(),
    val urls: Urls = Urls(),
) {
    data class Field(
        val label: StringResource,
        val value: String?,
    )
    data class Urls(
        val videoUrl: String? = null,
        val hitboxImageList: List<String> = listOf(),
        val moveImageList: List<String> = listOf(),
    )

    fun isExpandable(): Boolean {
        return notes.isNotEmpty() || urls.videoUrl.isNullOrEmpty().not() || urls.hitboxImageList.isNotEmpty()
    }
}
