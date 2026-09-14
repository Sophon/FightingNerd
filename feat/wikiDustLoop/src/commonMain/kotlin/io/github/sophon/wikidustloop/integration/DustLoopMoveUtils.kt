package io.github.sophon.wikidustloop.integration

import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikidustloop.integration.model.DBFZMoveProperties
import io.github.sophon.wikidustloop.integration.model.GGMoveProperties

fun Move.getLevel(): String? {
    return (gameProperties as? GGMoveProperties)?.level
        ?: (gameProperties as? DBFZMoveProperties)?.level
}
