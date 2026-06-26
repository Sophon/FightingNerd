package io.github.sophon.core.wiki.util

import io.github.sophon.core.wiki.model.Move

fun Move.getLevel(): String? {
    return ggstProperties?.level
        ?: dbfzProperties?.level
}