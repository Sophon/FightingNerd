package io.github.sophon.fightingnerd.inPort

/**
 * Starts a refresh of every enabled game and returns right away - the refresh runs in the app scope,
 * so it outlives the screen that started it. Its events come through [SubscribeToRefreshEventsUseCase].
 */
interface StartRefreshUseCase {
    operator fun invoke()

    /**
     * Same as the parameterless refresh, limited to the enabled games of [gameIdSet].
     */
    operator fun invoke(gameIdSet: Set<String>)
}
