package io.github.sophon.cornerman.screens.home

import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.cornerman.QUALIFIER_WAVU
import io.github.sophon.cornerman.screens.home.data.CharacterListDatabase
import io.github.sophon.cornerman.screens.home.data.RoomCharacterListDB
import io.github.sophon.cornerman.screens.home.usecase.GetAvailableFeaturesUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeVM)

    singleOf(::GetAvailableFeaturesUseCase)

    single { get<CharacterListDatabase>().characterListDao() }
    single<CharacterListDB>(named(QUALIFIER_WAVU)) {
        RoomCharacterListDB(get())
    }
}