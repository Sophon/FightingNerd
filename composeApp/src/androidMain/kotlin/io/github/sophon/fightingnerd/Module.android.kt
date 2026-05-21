package io.github.sophon.fightingnerd

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.fightingnerd.infrastructure.createDataStore
import io.github.sophon.fightingnerd.feat.home.data.RoomCharacterListDB
import io.github.sophon.fightingnerd.feat.home.data.getCharacterListDatabase
import io.github.sophon.fightingnerd.feat.home.data.getCharacterListDatabaseBuilder
import io.github.sophon.fightingnerd.screens.moveList.data.RoomMoveListDB
import io.github.sophon.fightingnerd.screens.moveList.data.getMoveListDatabase
import io.github.sophon.fightingnerd.screens.moveList.data.getMoveListDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal actual val platformModule = module {
    single { createDataStore() }

    // Single factory that creates both DBs for any game
    single<(Game) -> Pair<CharacterListDB, MoveListDB>> {
        { game ->
            val charBuilder = getCharacterListDatabaseBuilder(
                androidContext(),
                "${game.id.lowercase()}_characters.db"
            )
            val charDatabase = getCharacterListDatabase(charBuilder)
            val characterDB = RoomCharacterListDB(charDatabase.characterListDao())

            val moveBuilder = getMoveListDatabaseBuilder(
                androidContext(),
                "${game.id.lowercase()}_moves.db"
            )
            val moveDatabase = getMoveListDatabase(moveBuilder)
            val moveDB = RoomMoveListDB(moveDatabase.moveListDao(), get())

            val dbPair = characterDB to moveDB
            dbPair
        }
    }
}
