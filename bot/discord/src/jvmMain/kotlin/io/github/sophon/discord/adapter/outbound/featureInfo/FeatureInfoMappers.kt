package io.github.sophon.discord.adapter.outbound.featureInfo

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.app.model.response.ModulesResponse

internal fun FeatureInfo.toModule(): ModulesResponse.Module {
    val module = ModulesResponse.Module(
        name = name,
        url = url,
        version = version,
    )
    return module
}
