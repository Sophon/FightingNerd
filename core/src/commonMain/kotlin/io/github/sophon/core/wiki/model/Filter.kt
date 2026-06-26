package io.github.sophon.core.wiki.model

interface Filter {
    val name: String
    val predicate: (Move) -> Boolean

    object None: Filter {
        override val name: String = "None"
        override val predicate: (Move) -> Boolean = { true }
    }
}