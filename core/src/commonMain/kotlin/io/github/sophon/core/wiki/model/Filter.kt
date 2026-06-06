package io.github.sophon.core.wiki.model

interface Filter {
    val predicate: (Move) -> Boolean

    object None: Filter {
        override val predicate: (Move) -> Boolean = { true }
    }
}