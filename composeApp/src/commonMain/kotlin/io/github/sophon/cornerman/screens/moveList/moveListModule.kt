package io.github.sophon.cornerman.screens.moveList

import androidx.room.RoomDatabase
import io.github.sophon.cornerman.infrastructure.createDataStore
import io.github.sophon.cornerman.screens.moveList.data.MoveListDatabase
import io.github.sophon.cornerman.screens.moveList.data.RoomMoveListDB
import io.github.sophon.cornerman.screens.moveList.data.getMoveListDatabase
import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchCharacterListUseCase
import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchMoveListUseCase
import io.github.sophon.cornerman.screens.moveList.ui.MoveListVM
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.cornerman.screens.moveList.domain.MoveListError
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val moveListModule = module {
    viewModelOf(::MoveListVM)

    singleOf(::FetchMoveListUseCase)
    singleOf(::FetchCharacterListUseCase)

    single { get<MoveListDatabase>().moveListDao() }
    single { getMoveListDatabase(get<RoomDatabase.Builder<MoveListDatabase>>()) }
    singleOf(::RoomMoveListDB).bind<MoveListDB>()

    single { createDataStore() }
}