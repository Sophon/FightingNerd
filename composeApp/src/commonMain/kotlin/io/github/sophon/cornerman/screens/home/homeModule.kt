package io.github.sophon.cornerman.screens.home

import androidx.room.RoomDatabase
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.cornerman.screens.home.data.CharacterListDatabase
import io.github.sophon.cornerman.screens.home.data.RoomCharacterListDB
import io.github.sophon.cornerman.screens.home.data.getCharacterListDatabase
import io.github.sophon.cornerman.screens.home.usecase.GetAvailableFeaturesUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val homeModule = module {
    viewModelOf(::HomeVM)

    singleOf(::GetAvailableFeaturesUseCase)

    single { get<CharacterListDatabase>().characterListDao() }
    single { getCharacterListDatabase(get<RoomDatabase.Builder<CharacterListDatabase>>()) }
    singleOf(::RoomCharacterListDB).bind<CharacterListDB>()
}