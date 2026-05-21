package io.github.sophon.fightingnerd.featureRegistry.wavuWiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.fightingnerd.core.model.Module
import io.github.sophon.fightingnerd.feat.module.ModuleRepo
import io.github.sophon.fightingnerd.featureRegistry.usecase.FetchCharacterListUseCase
import io.github.sophon.fightingnerd.featureRegistry.usecase.SyncDataIfOldUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class WavuHomeScreenVM(
    private val moduleRepo: ModuleRepo,
    private val syncDataIfOldUseCase: SyncDataIfOldUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
): ViewModel() {
    private val wavuFeature: Module? by lazy {
//        moduleRepo.getEnabledModules()
//            .find { it.featureInfo.name == "Wavu Wiki" }
        TODO()
    }
    private val _state = MutableStateFlow(WavuHomeScreenViewState())
    val state = _state
        .onStart {
            startWavuSession()
            fetchCharacterList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = WavuHomeScreenViewState()
        )


    fun onExpandClick() {
        _state.update { it.copy(isExpanded = it.isExpanded.not()) }
    }


    private suspend fun startWavuSession() {
        _state.update { it.copy(isLoading = true) }

        wavuFeature?.let { feature ->
            feature.featureInfo.supportedGameSet.forEach { game ->
                feature.getWikiClient(game.id)?.let { wiki ->
                    syncDataIfOldUseCase.invoke(wiki)
                }
            }
        }
        _state.update { it.copy(isLoading = false) }
    }

    private suspend fun fetchCharacterList() {
        val allCharacters = mutableListOf<Character>()

        wavuFeature?.let { feature ->
            feature.featureInfo.supportedGameSet.forEach { game ->
                feature.getWikiClient(game.id)?.let { wiki ->
                    fetchCharacterListUseCase.invoke(wiki)
                        .onSuccess { characterList ->
                            allCharacters.addAll(characterList)
                        }
                        .onError { error ->
                            Napier.e(tag = TAG) { "fetchCharacterList: $error" }
                            _state.update { it.copy(error = error.toString()) }
                        }
                }
            }
        }

        _state.update { it.copy(characterList = allCharacters) } //TODO: split per game eventually
    }


    private companion object {
        const val TAG = "WavuHomeScreenVM"
    }
}