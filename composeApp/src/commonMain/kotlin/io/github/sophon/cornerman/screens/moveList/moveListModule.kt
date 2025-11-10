package io.github.sophon.cornerman.screens.moveList

import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.cornerman.QUALIFIER_WAVU
import io.github.sophon.cornerman.infrastructure.createDataStore
import io.github.sophon.cornerman.screens.moveList.data.MoveListDatabase
import io.github.sophon.cornerman.screens.moveList.data.RoomMoveListDB
import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchCharacterListUseCase
import io.github.sophon.cornerman.screens.moveList.domain.usecase.FetchMoveListUseCase
import io.github.sophon.cornerman.screens.moveList.ui.MoveListVM
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val moveListModule = module {
    viewModelOf(::MoveListVM)

    singleOf(::FetchMoveListUseCase)
    singleOf(::FetchCharacterListUseCase)

    single { get<MoveListDatabase>().moveListDao() }
    single<MoveListDB>(named(QUALIFIER_WAVU)) {
        RoomMoveListDB(get(), get())
    }

    single { createDataStore() }
}