package io.github.sophon.discord.app.outPort

import io.github.sophon.discord.app.model.response.ModulesResponse

internal interface FeatureInfoPort {
    fun getModule(featureName: String): ModulesResponse.Module?
}
