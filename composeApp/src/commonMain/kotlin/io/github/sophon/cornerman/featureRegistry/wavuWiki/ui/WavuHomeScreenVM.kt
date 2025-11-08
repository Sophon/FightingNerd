package io.github.sophon.cornerman.featureRegistry.wavuWiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.cornerman.featureRegistry.wavuWiki.usecase.SyncDataIfOldUseCase
import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchCharacterListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class WavuHomeScreenVM(
    private val syncDataIfOldUseCase: SyncDataIfOldUseCase,
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
): ViewModel() {
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
        syncDataIfOldUseCase.invoke()
        _state.update { it.copy(isLoading = false) }
    }

    private suspend fun fetchCharacterList() {
        when (val result = fetchCharacterListUseCase.invoke()) {
            is Result.Success -> {
                cacheCharacterList(characterList = result.data)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                _state.update { it.copy(error = result.error.toString()) }
            }
        }
    }

    private fun cacheCharacterList(characterList: List<Character>) {
        _state.update { it.copy(characterList = characterList) }
    }
}


private const val TAG = "WavuHomeScreenVM"