package io.github.sophon.discord.adapter.outbound.featureInfo

import io.github.sophon.EwgfFeatureInfo
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.discord.app.outPort.FeatureInfoPort
import io.github.sophon.glossaryinfil.GlossaryFeatureInfo
import io.github.sophon.wiki.WikiFeatureInfo
import io.github.sophon.wiki.model.wiki.Game

internal class FeatureInfoAdapter(
    private val ewgfFeatureInfo: EwgfFeatureInfo,
    private val glossaryFeatureInfo: GlossaryFeatureInfo,
    private val wikiFeatureInfo: WikiFeatureInfo,
): FeatureInfoPort {
    override fun getModule(featureName: String): ModulesResponse.Module? {
        val isWiki = Game.entries.any { game -> game.wiki.displayName == featureName }
        val featureInfo = when {
            isWiki -> wikiFeatureInfo.featureInfo
            (featureName == ewgfFeatureInfo.featureInfo.name) -> ewgfFeatureInfo.featureInfo
            (featureName == glossaryFeatureInfo.featureInfo.name) -> glossaryFeatureInfo.featureInfo
            else -> null
        }
        val module = featureInfo?.toModule()
        return module
    }
}
