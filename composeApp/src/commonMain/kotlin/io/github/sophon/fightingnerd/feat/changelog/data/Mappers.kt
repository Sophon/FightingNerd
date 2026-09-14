package io.github.sophon.fightingnerd.feat.changelog.data

import io.github.sophon.fightingnerd.feat.changelog.model.Release
import kotlinx.collections.immutable.toImmutableList

internal fun List<ReleaseDto>.toDomain(): List<Release> {
    val releases = mapNotNull { it.toRelease() }
    return releases
}

private fun ReleaseDto.toRelease(): Release? {
    val (type, version) = when {
        tagName.startsWith(PREFIX_APP) -> Release.Type.APP to tagName.removePrefix(PREFIX_APP)
        tagName.startsWith(PREFIX_BOT) -> Release.Type.BOT to tagName.removePrefix(PREFIX_BOT)
        else -> return null
    }
    val changeList = body
        .lines()
        .map {
            it
                .trim()
                .removePrefix("- ")
                .removePrefix("* ")
                .replace("`", "")
                .trim()
        }
        .filter { it.isNotBlank() }
        .toImmutableList()
    val release = Release(
        version = version,
        isPreRelease = prerelease,
        type = type,
        changeList = changeList,
    )
    return release
}


private const val PREFIX_APP = "app-v"
private const val PREFIX_BOT = "bot-v"
