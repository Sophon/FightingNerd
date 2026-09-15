package io.github.sophon.fightingnerd.feat.more.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.usecase.OpenUrlUseCase
import io.github.sophon.fightingnerd.core.usecase.RequestReviewUseCase
import io.github.sophon.fightingnerd.core.util.ScreenStopWatch
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.review.SessionContext
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
    private val subscribeToAvailableFeaturesUseCase: SubscribeToAvailableFeaturesUseCase,
) : ViewModel() {
    private val screenStopWatch = ScreenStopWatch()
    private val _state = MutableStateFlow(AboutState())
    val state: StateFlow<AboutState> = _state.asStateFlow()

    init {
        getEnabledFeatures()
    }


    fun openUrl(url: String) {
        openUrlUseCase(url)
    }

    fun onScreenExit() {
        val sessionDuration = screenStopWatch.elapsed()
        val sessionContext = SessionContext.About(duration = sessionDuration)
        requestReviewUseCase(sessionContext)
    }


    private fun getEnabledFeatures() {
        viewModelScope.launch {
            subscribeToAvailableFeaturesUseCase().first()
                .onSuccess { featureList ->
                    val list = featureList
                        .map { feature ->
                            AboutState.UiWiki(
                                iconUrl = feature.iconUrl,
                                url = feature.url,
                            )
                        }
                        .toImmutableList()
                    _state.update { it.copy(uiWikiList = list) }
                }
                .onError { error ->
                    Napier.e(tag = TAG) { error.toString() }
                }
        }
    }


    private companion object {
        const val TAG = "AboutVM"
    }
}
