package io.github.sophon.fightingnerd.app.service

import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.inPort.StartRefreshUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn

internal class StartRefreshService(
    private val refreshWikiPort: RefreshWikiPort,
    private val appScope: CoroutineScope,
): StartRefreshUseCase {
    override fun invoke() {
        refreshWikiPort.refresh().launchIn(appScope)
    }

    override fun invoke(gameIdSet: Set<String>) {
        refreshWikiPort.refresh(gameIdSet).launchIn(appScope)
    }
}
