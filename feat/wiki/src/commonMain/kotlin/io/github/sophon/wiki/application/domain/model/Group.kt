package io.github.sophon.wiki.application.domain.model

import io.github.sophon.core.wiki.model.Group
import io.github.sophon.core.wiki.model.Move

object Default: Group {
    override val id: String = "Other"
    override val predicate: (Move) -> Boolean = { true }
}
