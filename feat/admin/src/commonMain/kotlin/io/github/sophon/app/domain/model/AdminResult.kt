package io.github.sophon.app.domain.model

import io.github.sophon.integration.model.Source

data class AdminResult(
    val source: Source,
    val message: String? = null,
)