package io.github.sophon.fightingnerd.adapter.inbound.more.updates

import androidx.compose.runtime.Immutable
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.general_unit_day
import fightingnerd.composeapp.generated.resources.general_unit_hour
import fightingnerd.composeapp.generated.resources.general_unit_month
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

internal data class UpdatesState(
    val currentAutoUpdateSettings: AutoUpdateSettings = AutoUpdateSettings(),
    val updatedAutoUpdateSettings: AutoUpdateSettings = AutoUpdateSettings(),

    val featureList: ImmutableList<UiFeatureSetting> = persistentListOf(),
) {
    val isChanged: Boolean get() = (currentAutoUpdateSettings != updatedAutoUpdateSettings)

    @Immutable
    internal data class UiFeatureSetting(
        val name: String,
        val iconUrl: String,
        val gameList: ImmutableList<UiGame>,
    ) {
        val isRefreshing: Boolean
            get() = gameList.isNotEmpty() && gameList.all { it.isRefreshing }

        data class UiGame(
            val name: String,
            val id: String,
            val lastUpdatedTimeStamp: String,
            val isRefreshing: Boolean = false,
        )
    }

    @Immutable
    data class AutoUpdateSettings(
        val isEnabled: Boolean = false,
        val period: Int? = 7,
        val unit: TimeUnit = TimeUnit.DAY,
    ) {
        fun toDuration(): Duration? {
            if (period == null || period < 1) return null
            val duration = when (unit) {
                TimeUnit.HOUR -> period.hours
                TimeUnit.DAY -> period.days
                TimeUnit.MONTH -> (period * DAYS_PER_MONTH).days
            }
            return duration
        }

        enum class TimeUnit(val stringResource: StringResource) {
            HOUR(Res.string.general_unit_hour),
            DAY(Res.string.general_unit_day),
            MONTH(Res.string.general_unit_month),
        }

        companion object {
            private const val DAYS_PER_MONTH = 30
            private const val HOURS_PER_DAY = 24L
            private const val HOURS_PER_MONTH = HOURS_PER_DAY * DAYS_PER_MONTH

            fun fromDuration(duration: Duration): AutoUpdateSettings {
                val totalHours = duration.inWholeHours
                val settings = when {
                    totalHours >= HOURS_PER_MONTH && totalHours % HOURS_PER_MONTH == 0L -> {
                        AutoUpdateSettings(
                            isEnabled = true,
                            period = (totalHours / HOURS_PER_MONTH).toInt(),
                            unit = TimeUnit.MONTH,
                        )
                    }
                    totalHours >= HOURS_PER_DAY && totalHours % HOURS_PER_DAY == 0L -> {
                        AutoUpdateSettings(
                            isEnabled = true,
                            period = (totalHours / HOURS_PER_DAY).toInt(),
                            unit = TimeUnit.DAY,
                        )
                    }
                    else -> {
                        AutoUpdateSettings(
                            isEnabled = true,
                            period = totalHours.toInt().coerceAtLeast(1),
                            unit = TimeUnit.HOUR,
                        )
                    }
                }
                return settings
            }
        }
    }


    companion object {
        val features = persistentListOf(
            UiFeatureSetting(
                name = "Wavu Wiki",
                iconUrl = "",
                gameList = persistentListOf(
                    UiFeatureSetting.UiGame(
                        name = "Tekken 8",
                        id = "T8",
                        lastUpdatedTimeStamp = "2026-09-01 08:15",
                    ),
                ),
            ),
            UiFeatureSetting(
                name = "SuperCombo",
                iconUrl = "",
                gameList = persistentListOf(
                    UiFeatureSetting.UiGame(
                        name = "Street Fighter 6",
                        id = "SF6",
                        lastUpdatedTimeStamp = "2026-08-28 14:42",
                    ),
                    UiFeatureSetting.UiGame(
                        name = "Mortal Kombat 1",
                        id = "MK1",
                        lastUpdatedTimeStamp = "2026-07-19 21:03",
                    ),
                ),
            ),
            UiFeatureSetting(
                name = "Dustloop",
                iconUrl = "",
                gameList = persistentListOf(
                    UiFeatureSetting.UiGame(
                        name = "Guilty Gear Strive",
                        id = "GGST",
                        lastUpdatedTimeStamp = "2026-09-04 11:27",
                    ),
                    UiFeatureSetting.UiGame(
                        name = "Granblue Fantasy Versus Rising",
                        id = "GBVSR",
                        lastUpdatedTimeStamp = "2026-08-15 06:50",
                    ),
                    UiFeatureSetting.UiGame(
                        name = "BlazBlue Central Fiction",
                        id = "BBCF",
                        lastUpdatedTimeStamp = "2026-05-30 18:11",
                    ),
                ),
            ),
        )

        val PREVIEW_ENABLED = UpdatesState(
            currentAutoUpdateSettings = AutoUpdateSettings(isEnabled = true),
            updatedAutoUpdateSettings = AutoUpdateSettings(isEnabled = true),
            featureList = features,
        )

        val PREVIEW_DISABLED = UpdatesState(featureList = features,)
    }
}
