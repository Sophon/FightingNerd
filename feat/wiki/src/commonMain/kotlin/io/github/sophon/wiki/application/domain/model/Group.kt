package io.github.sophon.wiki.application.domain.model

interface Group {
    val id: String
    val predicate: (Move) -> Boolean
}

object Default: Group {
    override val id: String = "Other"
    override val predicate: (Move) -> Boolean = { true }
}
