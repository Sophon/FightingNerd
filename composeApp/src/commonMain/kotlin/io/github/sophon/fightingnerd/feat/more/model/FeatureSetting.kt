package io.github.sophon.fightingnerd.feat.more.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
internal data class FeatureSetting(
    val name: String,
    val iconUrl: String,
    val url: String,
    val version: String,
    val gameList: ImmutableList<FeatureGame>,
) {
    val isEnabled: Boolean get() = gameList.any { it.isEnabled }

    @Serializable
    data class FeatureGame(
        val name: String,
        val id: String,
        val isEnabled: Boolean,
        val lastUpdatedTimeStamp: Instant? = null,
    )
}