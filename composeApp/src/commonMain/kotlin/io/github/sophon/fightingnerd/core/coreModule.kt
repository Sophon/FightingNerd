package io.github.sophon.fightingnerd.core

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.core.data.db.DatabaseDriverFactory
import io.github.sophon.fightingnerd.core.data.db.SqlCharacterDB
import io.github.sophon.fightingnerd.core.data.db.SqlMoveDB
import io.github.sophon.fightingnerd.core.data.store.PreferenceRepoImpl
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.usecase.OpenUrlUseCase
import io.github.sophon.fightingnerd.db.character.CharacterDatabase
import io.github.sophon.fightingnerd.db.move.MoveDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun coreModule() = module {
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

    singleOf(::PreferenceRepoImpl).bind<PreferenceRepo>()

    singleOf(::OpenUrlUseCase)

    singleOf(::OverlayService)
}
