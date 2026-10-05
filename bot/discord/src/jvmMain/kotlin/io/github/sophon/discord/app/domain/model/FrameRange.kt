package io.github.sophon.discord.app.domain.model

/**
 * Inclusive range of [type] frame values; [Int.MIN_VALUE] and [Int.MAX_VALUE] stand for -INF and INF.
 */
data class FrameRange(
    val type: Type,
    val from: Int,
    val to: Int,
) {
    enum class Type {
        STARTUP,
        ON_HIT,
        ON_BLOCK,
        ON_COUNTER,
    }
}
