package io.github.sophon.discord.feat.bot.usecase

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.BOT_NAME
import io.github.sophon.discord.BuildKonfig
import io.github.sophon.discord.URL_IMG_FIGHTING_NERD
import io.github.sophon.discord.URL_REPO

internal class GetBotFeatureInfoUseCase {
    fun invoke(): FeatureInfo {
        return FeatureInfo(
            name = BOT_NAME,
            url = URL_REPO,
            iconUrl = URL_IMG_FIGHTING_NERD,
            version = BuildKonfig.VERSION,
        )
    }
}