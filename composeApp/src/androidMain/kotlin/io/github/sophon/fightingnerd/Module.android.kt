package io.github.sophon.fightingnerd

import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import io.github.sophon.fightingnerd.screens.home.data.RoomCharacterListDB
import io.github.sophon.fightingnerd.screens.moveList.data.getMoveListDatabaseBuilder
import io.github.sophon.fightingnerd.screens.home.data.getCharacterListDatabase
import io.github.sophon.fightingnerd.screens.home.data.getCharacterListDatabaseBuilder
import io.github.sophon.fightingnerd.screens.moveList.data.RoomMoveListDB
import io.github.sophon.fightingnerd.screens.moveList.data.getMoveListDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule = module {
    // DataStore (shared across features)
    single { createDataStore() }

    // Wavu databases (separate files)
    single<CharacterListDB>(named(QUALIFIER_WAVU)) {
        val database = getCharacterListDatabase(
            getCharacterListDatabaseBuilder(androidContext(), "wavu_characters.db")
        )
        RoomCharacterListDB(database.characterListDao())
    }
    single<MoveListDB>(named(QUALIFIER_WAVU)) {
        val database = getMoveListDatabase(
            getMoveListDatabaseBuilder(androidContext(), "wavu_moves.db")
        )
        RoomMoveListDB(database.moveListDao(), get())
    }

    // SuperCombo databases (separate files)
    single<CharacterListDB>(named(QUALIFIER_SC)) {
        val database = getCharacterListDatabase(
            getCharacterListDatabaseBuilder(androidContext(), "supercombo_characters.db")
        )
        RoomCharacterListDB(database.characterListDao())
    }
    single<MoveListDB>(named(QUALIFIER_SC)) {
        val database = getMoveListDatabase(
            getMoveListDatabaseBuilder(androidContext(), "supercombo_moves.db")
        )
        RoomMoveListDB(database.moveListDao(), get())
    }
}