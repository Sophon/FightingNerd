package io.github.sophon.fightingnerd.featureRegistry.superComboWiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.usecase.FetchCharacterListUseCase
import io.github.sophon.fightingnerd.featureRegistry.superComboWiki.usecase.SyncDataIfOldUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class SuperComboHomeVM(
    private val syncDataIfOldUseCase: SyncDataIfOldUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
): ViewModel() {
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
        syncDataIfOldUseCase.invoke()
        _state.update { it.copy(isLoading = false) }
    }

    private suspend fun fetchCharacterList() {
        fetchCharacterListUseCase.invoke()
            .onSuccess { characterList ->
                cacheCharacterList(characterList)
            }
            .onError { error ->
                Napier.e(tag = TAG) { error.toString() }
                _state.update { it.copy(error = error.toString()) }
            }
    }

    private fun cacheCharacterList(characterList: List<Character>) {
        _state.update { it.copy(characterList = characterList) }
    }


    private companion object {
        const val TAG = "SuperComboHomeScreenVM"
    }
}