package io.github.sophon.cornerman

import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.cornerman.infrastructure.createDataStore
import io.github.sophon.cornerman.screens.home.data.RoomCharacterListDB
import io.github.sophon.cornerman.screens.moveList.data.getMoveListDatabaseBuilder
import io.github.sophon.cornerman.screens.home.data.getCharacterListDatabase
import io.github.sophon.cornerman.screens.home.data.getCharacterListDatabaseBuilder
import io.github.sophon.cornerman.screens.moveList.data.RoomMoveListDB
import io.github.sophon.cornerman.screens.moveList.data.getMoveListDatabase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module = module {
    // DataStore (shared across features)
    single { createDataStore() }

    // Wavu databases (separate files)
    single<CharacterListDB>(named(QUALIFIER_WAVU)) {
        val database = getCharacterListDatabase(
            getCharacterListDatabaseBuilder("wavu_characters.db")
        )
        RoomCharacterListDB(database.characterListDao())
    }
    single<MoveListDB>(named(QUALIFIER_WAVU)) {
        val database = getMoveListDatabase(
            getMoveListDatabaseBuilder("wavu_moves.db")
        )
        RoomMoveListDB(database.moveListDao(), get())
    }

    // SuperCombo databases (separate files)
    single<CharacterListDB>(named(QUALIFIER_SC)) {
        val database = getCharacterListDatabase(
            getCharacterListDatabaseBuilder("supercombo_characters.db")
        )
        RoomCharacterListDB(database.characterListDao())
    }
    single<MoveListDB>(named(QUALIFIER_SC)) {
        val database = getMoveListDatabase(
            getMoveListDatabaseBuilder("supercombo_moves.db")
        )
        RoomMoveListDB(database.moveListDao(), get())
    }
}