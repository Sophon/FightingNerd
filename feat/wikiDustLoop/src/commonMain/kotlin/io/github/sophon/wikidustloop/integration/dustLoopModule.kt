package io.github.sophon.wikidustloop.integration

import app.cash.sqldelight.db.SqlDriver
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.data.CharacterDbAdapter
import io.github.sophon.core.wiki.data.CharacterRemoteAdapter
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.CharacterRepoImpl
import io.github.sophon.core.wiki.data.MoveDbAdapter
import io.github.sophon.core.wiki.data.MoveRemoteAdapter
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.data.MoveRepoImpl
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.wikidustloop.data.DustLoopDB
import io.github.sophon.wikidustloop.data.remote.DustLoopDataSource
import io.github.sophon.wikidustloop.data.remote.DustLoopDataSourceImpl
import io.github.sophon.wikidustloop.data.db.DustLoopCharacterDbAdapter
import io.github.sophon.wikidustloop.data.db.DustLoopMoveDbAdapter
import io.github.sophon.wikidustloop.data.remote.DustLoopCharacterRemoteAdapter
import io.github.sophon.wikidustloop.data.remote.DustLoopMoveRemoteAdapter
import io.github.sophon.wikidustloop.domain.DustLoopWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun dustLoopModule() = module {
    singleOf(::DustLoopDataSourceImpl).bind<DustLoopDataSource>()
    single { DustLoopFeatureInfo }

    single<DustLoopDB>(named(WikiClientFeature.DustLoop.id)) {
        val driver = get<SqlDriver>(named(WikiClientFeature.DustLoop.id)) { parametersOf(DustLoopDB.Schema) }
        val db = DustLoopDB(driver = driver)
        db
    }

    DustLoopFeatureInfo.featureInfo.supportedGameSet.forEach { game ->
        val gameQualifier = named("${WikiClientFeature.DustLoop.id}:${game.id}")

        single<CharacterDbAdapter>(gameQualifier) {
            DustLoopCharacterDbAdapter(
                db = get(named(WikiClientFeature.DustLoop.id)),
                gameId = game.id,
            )
        }
        single<MoveDbAdapter>(gameQualifier) {
            DustLoopMoveDbAdapter(
                db = get(named(WikiClientFeature.DustLoop.id)),
                game = game,
            )
        }
        single<CharacterRemoteAdapter>(gameQualifier) {
            DustLoopCharacterRemoteAdapter(source = get(), game = game)
        }
        single<MoveRemoteAdapter>(gameQualifier) {
            DustLoopMoveRemoteAdapter(source = get(), game = game)
        }
        single<CharacterRepo>(gameQualifier) {
            CharacterRepoImpl(
                dbAdapter = get(gameQualifier),
                remoteAdapter = get(gameQualifier),
            )
        }
        single<MoveRepo>(gameQualifier) {
            MoveRepoImpl(
                dbAdapter = get(gameQualifier),
                remoteAdapter = get(gameQualifier),
            )
        }
    }

    factory<WikiClient>(named(WikiClientFeature.DustLoop.id)) { params ->
        val game = params.get<Game>()
        val gameQualifier = named("${WikiClientFeature.DustLoop.id}:${game.id}")
        DustLoopWikiClient(
            game = game,
            characterRepo = get(gameQualifier),
            moveRepo = get(gameQualifier),
            scope = get(),
        )
    }
}
