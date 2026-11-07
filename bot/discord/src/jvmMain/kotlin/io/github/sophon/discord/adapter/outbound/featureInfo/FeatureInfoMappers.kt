package io.github.sophon.discord.adapter.outbound.featureInfo

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.app.model.response.ModulesResponse
import io.github.sophon.wiki.model.wiki.Game

internal fun FeatureInfo.toModule(supportedGameSet: Set<Game>): ModulesResponse.Module {
    val module = ModulesResponse.Module(
        name = name,
        url = url,
        version = version,
        gameSet = supportedGameSet.mapTo(mutableSetOf()) { game ->
            ModulesResponse.Module.Game(name = game.displayName, id = game.id)
        },
    )
    return module
}
