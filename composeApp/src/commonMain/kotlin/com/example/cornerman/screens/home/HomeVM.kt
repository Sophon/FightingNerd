package com.example.cornerman.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.Result
import com.example.cornerman.screens.home.domain.FetchCharacterListUseCase
import com.example.wikiwavu.domain.model.Character
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeVM(
    private val fetchCharacterListUseCase: FetchCharacterListUseCase,
): ViewModel() {
    private val _state = MutableStateFlow<HomeViewState>(HomeViewState())
    val state = _state
        .onStart {
            fetchCharacterList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeViewState()
        )


    fun onCharacterClick(index: Int) {
        val character = _state.value.characterList.getOrNull(index)
        Napier.d(tag = TAG) { "clicked on: ${character?.name}" }
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

private const val TAG = "HomeVM"