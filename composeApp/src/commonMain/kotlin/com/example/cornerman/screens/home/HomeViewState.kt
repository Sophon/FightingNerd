package com.example.cornerman.screens.home

import com.example.wikiwavu.domain.model.Character

data class HomeViewState(
    val characterList: List<Character> = listOf(),

    val isLoading: Boolean = false,
    val error: String? = null,
)
