package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.util.orDash
import io.github.sophon.discord.app.model.response.BotResponse

internal fun mandatoryFieldOf(title: String, value: String?): BotResponse.Field{
    val field = BotResponse.Field(title, value.orDash())

    return field
}

internal fun mandatoryFieldOf(title: String, valueList: List<String>?): BotResponse.Field{
    val field = mandatoryFieldOf(title, valueList?.joinToString(", "))

    return field
}

internal fun fieldOf(title: String, value: String?): BotResponse.Field? {
    val field = value
        ?.takeIf { it.isNotBlank() }
        ?.let { BotResponse.Field(title, it) }

    return field
}

internal fun fieldOf(title: String, valueList: List<String>?): BotResponse.Field? {
    val field = fieldOf(title, valueList?.joinToString(", "))

    return field
}

/** `a | b | c` with blanks as dashes; null when every value is blank. */
internal fun mergedValueOf(vararg values: String?): String? {
    val mergedValue = values
        .takeIf { valueList -> valueList.any { !it.isNullOrBlank() } }
        ?.joinToString(" | ") { it.orDash() }

    return mergedValue
}
