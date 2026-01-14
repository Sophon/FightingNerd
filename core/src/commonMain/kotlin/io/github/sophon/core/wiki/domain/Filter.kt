package io.github.sophon.core.wiki.domain

import io.github.sophon.core.wiki.domain.model.Move

interface Filter {
    val predicate: (Move) -> Boolean

    object None: Filter {
        override val predicate: (Move) -> Boolean = { true }
    }
}