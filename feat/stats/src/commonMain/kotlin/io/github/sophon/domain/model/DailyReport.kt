package io.github.sophon.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class DailyReport(
    val date: LocalDate,
    val commandMap: Map<Command, Long>,
)
