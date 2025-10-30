package com.example.cornerman.screens.moveList

import androidx.room.RoomDatabase
import com.example.cornerman.infrastructure.createDataStore
import com.example.cornerman.screens.moveList.data.MoveListDatabase
import com.example.cornerman.screens.moveList.data.RoomMoveListDB
import com.example.cornerman.screens.moveList.data.getMoveListDatabase
import com.example.cornerman.screens.moveList.domain.usecase.FetchCharacterListUseCase
import com.example.cornerman.screens.moveList.domain.usecase.FetchMoveListUseCase
import com.example.cornerman.screens.moveList.ui.MoveListVM
import com.example.wikiwavu.data.MoveListDB
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun moveListModule() = module {
    viewModelOf(::MoveListVM)

    singleOf(::FetchMoveListUseCase)
    singleOf(::FetchCharacterListUseCase)

    single { get<MoveListDatabase>().moveListDao() }
    single { getMoveListDatabase(get<RoomDatabase.Builder<MoveListDatabase>>()) }
    singleOf(::RoomMoveListDB).bind<MoveListDB>()

    single { createDataStore() }
}