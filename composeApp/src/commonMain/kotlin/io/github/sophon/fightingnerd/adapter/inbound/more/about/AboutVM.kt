package io.github.sophon.fightingnerd.adapter.inbound.more.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.fightingnerd.core.usecase.OpenUrlUseCase
import io.github.sophon.fightingnerd.core.usecase.RequestReviewUseCase
import io.github.sophon.fightingnerd.core.util.ScreenStopWatch
import io.github.sophon.fightingnerd.feat.review.SessionContext
import io.github.sophon.fightingnerd.inPort.SubscribeToWikisUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AboutVM(
    private val openUrlUseCase: OpenUrlUseCase,
    private val requestReviewUseCase: RequestReviewUseCase,
    private val subscribeToWikisUseCase: SubscribeToWikisUseCase,
) : ViewModel() {
    private val screenStopWatch = ScreenStopWatch()
    private val _state = MutableStateFlow(AboutState())
    val state: StateFlow<AboutState> = _state.asStateFlow()

    init {
        loadWikis()
    }


    fun openUrl(url: String) {
        openUrlUseCase(url)
    }

    fun onScreenExit() {
        val sessionDuration = screenStopWatch.elapsed()
        val sessionContext = SessionContext.About(duration = sessionDuration)
        requestReviewUseCase(sessionContext)
    }


    private fun loadWikis() {
        viewModelScope.launch {
            val wikiSet = subscribeToWikisUseCase().first()
            val list = wikiSet
                .map { wiki ->
                    AboutState.UiWiki(
                        iconUrl = wiki.iconUrl,
                        url = wiki.url,
                    )
                }
                .toImmutableList()
            _state.update { it.copy(uiWikiList = list) }
        }
    }
}
