package io.github.sophon.core.wiki.util

import io.github.sophon.core.wiki.domain.model.Move

fun Move.getLevel(): String? {
    return ggstProperties?.level
        ?: dbfzProperties?.level
}