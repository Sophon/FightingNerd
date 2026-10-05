package io.github.sophon.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class DailyReport(
    val date: LocalDate,
    val commandMap: Map<String, Map<String, Long>>, // feature -> commandName -> count
)
