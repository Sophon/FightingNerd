package io.github.sophon.domain

import io.github.sophon.core.domain.Error

sealed class StatsError(vararg val errors: String): Error {
    //
}