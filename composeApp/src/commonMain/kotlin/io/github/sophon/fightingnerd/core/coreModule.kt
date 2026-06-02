package io.github.sophon.fightingnerd.core

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.fightingnerd.core.data.DatabaseDriverFactory
import io.github.sophon.fightingnerd.core.data.SqlCharacterDB
import io.github.sophon.fightingnerd.core.data.TmpMemoryMoveListDB
import io.github.sophon.fightingnerd.db.character.CharacterDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun coreModule() = module {
    singleOf(::MoveRepository)
    singleOf(::TmpMemoryMoveListDB).bind<MoveListDB>()

    single<(Game) -> Pair<CharacterListDB, MoveListDB>> {
        val driverFactory: DatabaseDriverFactory = get()
        val moveListDB: MoveListDB = get()

        return@single { game ->
            val charDbName = "${game.wiki.id}_${game.id}_characters.db"
            val charDriver = driverFactory.create(charDbName)
            val charDb = CharacterDatabase(charDriver)
            val characterListDB: CharacterListDB = SqlCharacterDB(charDb, game.id)

            characterListDB to moveListDB
        }
    }
}
