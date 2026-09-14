package io.github.sophon.fightingnerd.feat.move.ui

import androidx.compose.runtime.Immutable
import io.github.sophon.core.wiki.model.CoreFilters
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.fightingnerd.feat.move.model.Bookmark
import io.github.sophon.fightingnerd.feat.move.model.MediaAvailability
import io.github.sophon.fightingnerd.feat.move.model.Property
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import org.jetbrains.compose.resources.StringResource

@Immutable
internal data class MoveListState(
    val character: UiCharacter? = null,
    val mediaCount: Int = 0,

    val moveDetail: MoveDetail? = null,

    val searchQuery: String? = null,
    val filterSheet: FilterSheet = FilterSheet(),

    val expandedMoveId: String? = null,

    val bookmarks: Bookmarks = Bookmarks(),

    val mediaAvailability: MediaAvailability = MediaAvailability.NotDownloaded,
) {
    @Immutable
    data class UiCharacter(
        val displayName: String,
        val propertyFields: ImmutableList<Field> = persistentListOf(),
        val isExpanded: Boolean = false,
    ) {
        val canExpand: Boolean get() {
            return propertyFields.isNotEmpty()
        }
    }

    @Immutable
    data class Field(
        val label: StringResource,
        val value: String?,
    )

    @Immutable
    data class UiMove(
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

    @Immutable
    data class MoveDetail(
        val id: String,
        val name: String?,
    )

    @Immutable
    data class FilterSheet(
        val isVisible: Boolean = false,
        val filterSet: ImmutableSet<Filter> = persistentSetOf(),

        val sliders: ImmutableMap<FrameSlider, SliderData> = FrameSlider.defaultSliders,

        val activeFilterSet: ImmutableSet<Filter> = persistentSetOf(),
    ) {
        val isFilterActive: Boolean get() {
            return activeFilterSet.isNotEmpty() || sliders.values.any { it.minMax != null }
        }

        fun sliderData(type: FrameSlider): SliderData = sliders.getValue(type)

        fun withSliderData(type: FrameSlider, data: SliderData): FilterSheet {
            val newSliders = sliders.toPersistentMap().put(type, data)
            return copy(sliders = newSliders)
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

        @Immutable
        data class SliderData(
            val minMax: MinMax? = null,
            val thumbs: Pair<Int, Int>,
        )

        enum class FrameSlider(
            val rawMin: Int,
            val rawMax: Int,
            val toCoreFilter: (MinMax) -> Filter,
        ) {
            Startup(FRAME_MIN_STARTUP, FRAME_MAX, { CoreFilters.Startup(it.min, it.max) }),
            OnHit(FRAME_MIN, FRAME_MAX, { CoreFilters.OnHit(it.min, it.max) }),
            OnBlock(FRAME_MIN, FRAME_MAX, { CoreFilters.OnBlock(it.min, it.max) });

            val sliderMin: Int get() = rawMin - 1
            val sliderMax: Int get() = rawMax + 1

            companion object {
                val defaultSliders: ImmutableMap<FrameSlider, SliderData> =
                    entries.associateWith { SliderData(thumbs = it.sliderMin to it.sliderMax) }
                        .toImmutableMap()
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
            character = UiCharacter(displayName = "Nina"),
        )

        const val FRAME_MIN_STARTUP = 3
        const val FRAME_MIN = -20
        const val FRAME_MAX = 20
    }
}
