package io.github.sophon.fightingnerd

import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import io.github.sophon.fightingnerd.screens.home.data.RoomCharacterListDB
import io.github.sophon.fightingnerd.screens.home.data.getCharacterListDatabase
import io.github.sophon.fightingnerd.screens.home.data.getCharacterListDatabaseBuilder
import io.github.sophon.fightingnerd.screens.moveList.data.RoomMoveListDB
import io.github.sophon.fightingnerd.screens.moveList.data.getMoveListDatabase
import io.github.sophon.fightingnerd.screens.moveList.data.getMoveListDatabaseBuilder
import org.koin.dsl.module

internal actual val platformModule = module {
    single { createDataStore() }

    // Single factory that creates both DBs for any gameId
    single<(String) -> Pair<CharacterListDB, MoveListDB>> {
        { gameId ->
            val charBuilder = getCharacterListDatabaseBuilder(
                "${gameId.lowercase()}_characters.db"
            )
            val charDatabase = getCharacterListDatabase(charBuilder)
            val characterDB = RoomCharacterListDB(charDatabase.characterListDao())

            val moveBuilder = getMoveListDatabaseBuilder(
                "${gameId.lowercase()}_moves.db"
            )
            val moveDatabase = getMoveListDatabase(moveBuilder)
            val moveDB = RoomMoveListDB(moveDatabase.moveListDao(), get())

            characterDB to moveDB
        }
    }
}