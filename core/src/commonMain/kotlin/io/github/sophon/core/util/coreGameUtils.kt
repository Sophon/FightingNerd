package io.github.sophon.core.util

import io.github.sophon.core.feature.Game

fun String.getGame(): Game? {
    return Game.entries.find { it.id == this }
}