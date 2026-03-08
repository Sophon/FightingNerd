package io.github.sophon.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyReport(
    val date: String,
    val hitMap: Map<Hit, Long>,
)
