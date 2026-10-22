package io.github.sophon.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class DailyReport(
    val date: LocalDate,
    val usageList: List<Usage>,
)
