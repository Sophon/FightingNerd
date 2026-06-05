package io.github.sophon.discord.feat.admin

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.integration.model.AdminResult

internal  fun createFeedbackEmbed(
    adminResult: AdminResult,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    adminResult.apply {
        title = "${source.username}-${source.id}-${source.channelId}"
        color = Color(TURQUOISE)

        mandatoryField(
            name = "${source.username} from ${source.serverName}",
            value = message,
            inline = false,
        )

        featureFooter(featureInfo)
    }
}


private const val TURQUOISE = 0x0000CED1