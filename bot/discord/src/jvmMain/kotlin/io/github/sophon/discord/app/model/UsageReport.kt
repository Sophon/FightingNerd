package io.github.sophon.discord.app.model

import kotlinx.datetime.LocalDate

internal data class UsageReport(
    val date: LocalDate,
    val usageList: List<Usage>,
) {
    data class Usage(
        val game: String?,
        val command: String,
        val count: Long,
    )
}
