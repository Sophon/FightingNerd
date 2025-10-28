package com.example.cornerman.screens.moveList

import com.example.cornerman.screens.moveList.data.InMemoryMoveListDB
import com.example.cornerman.screens.moveList.ui.MoveListVM
import com.example.cornerman.screens.moveList.domain.FetchMoveListUseCase
import com.example.wikiwavu.data.MoveListDB
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun moveListModule() = module {
    viewModelOf(::MoveListVM)

    singleOf(::FetchMoveListUseCase)

    singleOf(::InMemoryMoveListDB).bind<MoveListDB>()
}