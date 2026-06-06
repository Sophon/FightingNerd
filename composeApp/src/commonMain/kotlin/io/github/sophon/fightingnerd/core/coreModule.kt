package io.github.sophon.fightingnerd.core

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.fightingnerd.core.data.DatabaseDriverFactory
import io.github.sophon.fightingnerd.core.data.SqlCharacterDB
import io.github.sophon.fightingnerd.core.data.SqlMoveDB
import io.github.sophon.fightingnerd.db.character.CharacterDatabase
import io.github.sophon.fightingnerd.db.move.MoveDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal fun coreModule() = module {
    singleOf(::MoveRepository)

    single<(Game) -> Pair<CharacterListDB, MoveListDB>> {
        val driverFactory: DatabaseDriverFactory = get()

        return@single { game ->
            val charDbName = "${game.wiki.id}_${game.id}_characters.db"
            val charDriver = driverFactory.create(databaseName = charDbName, schema = CharacterDatabase.Schema)
            val charDb = CharacterDatabase(charDriver)
            val characterListDB: CharacterListDB = SqlCharacterDB(charDb, game.id)

            val moveDbName = "${game.wiki.id}_${game.id}_moves.db"
            val moveDriver = driverFactory.create(databaseName = moveDbName, schema = MoveDatabase.Schema)
            val moveDb = MoveDatabase(moveDriver)
            val moveListDB: MoveListDB = SqlMoveDB(moveDb, game.id)

            characterListDB to moveListDB
        }
    }
}
