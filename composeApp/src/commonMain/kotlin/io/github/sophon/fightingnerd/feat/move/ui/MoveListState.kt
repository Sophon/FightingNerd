package io.github.sophon.fightingnerd.feat.move.ui

import androidx.compose.runtime.Immutable
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.fightingnerd.feat.move.model.Bookmark
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@Immutable
internal data class MoveListState(
    val character: MoveListCharacter? = null,

    val moveDetail: MoveDetail? = null,

    val searchQuery: String? = null,
    val filterSheet: FilterSheet = FilterSheet(),

    val expandedMoveId: String? = null,

    val bookmarks: Bookmarks = Bookmarks(),
) {
    @Immutable
    data class MoveListCharacter(
        val displayName: String,
    )

    @Immutable
    data class MoveDetail(
        val id: String,
        val name: String?,
    )

    @Immutable
    data class FilterSheet(
        val isVisible: Boolean = false,
        val filterSet: ImmutableSet<Filter> = persistentSetOf(),

        val startup: MinMax? = null,
        val onHit: MinMax? = null,
        val onBlock: MinMax? = null,

        val activeFilterSet: ImmutableSet<Filter> = persistentSetOf(),
    ) {
        val isFilterActive: Boolean get() {
            return activeFilterSet.isNotEmpty()
                    || (startup != null)
                    || (onHit != null)
                    || (onBlock != null)
        }

        @Immutable
        data class MinMax(
            val min: Int? = null,
            val max: Int? = null,
        ) {
            val isValid: Boolean
                get() {
                    val result = min == null || max == null || min <= max
                    return result
                }
        }
    }

    @Immutable
    data class Bookmarks(
        val isExpanded: Boolean = false,
        val bookmarkList: ImmutableList<Bookmark> = persistentListOf(),
    )


    companion object {
        val PREVIEW = MoveListState(
            character = MoveListCharacter(displayName = "Nina"),
        )

        const val FRAME_MIN_STARTUP = 3
        const val FRAME_MIN = -20
        const val FRAME_MAX = 20
    }
}
