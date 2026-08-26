package io.github.sophon.wikidustloop.integration

import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wikidustloop.integration.model.DBFZMoveProperties
import io.github.sophon.wikidustloop.integration.model.GGSTMoveProperties

fun Move.getLevel(): String? {
    return (gameProperties as? GGSTMoveProperties)?.level
        ?: (gameProperties as? DBFZMoveProperties)?.level
}
