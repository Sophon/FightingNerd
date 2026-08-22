package io.github.sophon.core.wiki.model

interface Group {
    val id: String
    val predicate: (Move) -> Boolean
}

object Default: Group {
    override val id: String = "Other"
    override val predicate: (Move) -> Boolean = { true }
}
