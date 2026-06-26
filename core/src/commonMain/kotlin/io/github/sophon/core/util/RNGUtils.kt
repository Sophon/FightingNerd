package io.github.sophon.core.util

import kotlin.random.Random

fun rollChance(successPercentage: Int): Boolean {
    require(successPercentage in 0..100) { "Percentage must be between 0 and 100" }

    return Random.nextInt(until = 100) < successPercentage
}
