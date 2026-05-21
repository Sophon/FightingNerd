package io.github.sophon.fightingnerd.featureRegistry.superComboWiki.ui

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

internal class SuperComboHomeVM(
    private val moduleRepo: ModuleRepo,
    private val syncDataIfOldUseCase: SyncDataIfOldUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
): ViewModel() {
    private val superComboFeature: Module? by lazy {
        moduleRepo.getEnabledModules()
            .find { it.featureInfo.name == "SuperCombo Wiki" }
    }
    private val _state = MutableStateFlow(SuperComboHomeScreenViewState())
    val state = _state
        .onStart {
            startSuperComboSession()
            fetchCharacterList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SuperComboHomeScreenViewState()
        )


    fun onExpandClick() {
        _state.update { it.copy(isExpanded = it.isExpanded.not()) }
    }


    private suspend fun startSuperComboSession() {
        _state.update { it.copy(isLoading = true) }

        superComboFeature?.let { feature ->
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

        superComboFeature?.let { feature ->
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

        _state.update { it.copy(characterList = allCharacters) }
    }


    private companion object {
        const val TAG = "SuperComboHomeScreenVM"
    }
}