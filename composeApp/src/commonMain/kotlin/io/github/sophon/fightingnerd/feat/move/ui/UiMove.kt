package io.github.sophon.fightingnerd.feat.move.ui

import androidx.compose.runtime.Immutable
import io.github.sophon.fightingnerd.feat.move.model.Property
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.StringResource

@Immutable
internal data class UiMove(
    val id: String,
    val input: String,
    val name: String?,

    val propertySet: ImmutableSet<Property> = persistentSetOf(),
    val coreFields: ImmutableList<Field>,
    val optionalFields: ImmutableList<Field>,
    val notes: ImmutableList<String> = persistentListOf(),
    val urls: Urls = Urls(),
) {
    @Immutable
    data class Field(
        val label: StringResource,
        val value: String?,
    )
    @Immutable
    data class Urls(
        val videoUrl: String? = null,
        val hitboxImageList: ImmutableList<String> = persistentListOf(),
        val moveImageList: ImmutableList<String> = persistentListOf(),
    )

    fun isExpandable(): Boolean {
        val result = notes.isNotEmpty() || urls.videoUrl.isNullOrEmpty().not() || urls.hitboxImageList.isNotEmpty()
        return result
    }
}
