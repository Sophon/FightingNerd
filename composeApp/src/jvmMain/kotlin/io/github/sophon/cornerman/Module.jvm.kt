package io.github.sophon.cornerman

import io.github.sophon.cornerman.data.getMoveListDatabaseBuilder
import io.github.sophon.cornerman.screens.home.data.getCharacterListDatabase
import io.github.sophon.cornerman.screens.home.data.getCharacterListDatabaseBuilder
import io.github.sophon.cornerman.screens.moveList.data.getMoveListDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { getMoveListDatabase(getMoveListDatabaseBuilder()) }
    single { getCharacterListDatabase(getCharacterListDatabaseBuilder()) }
}