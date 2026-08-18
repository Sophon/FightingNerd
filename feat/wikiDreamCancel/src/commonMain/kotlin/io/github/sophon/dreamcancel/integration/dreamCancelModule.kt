package io.github.sophon.dreamcancel.integration

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
import io.github.sophon.dreamcancel.data.DreamCancelDB
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSource
import io.github.sophon.dreamcancel.data.DreamCancelWikiDataSourceImpl
import io.github.sophon.dreamcancel.data.db.DreamCancelCharacterDbAdapter
import io.github.sophon.dreamcancel.data.db.DreamCancelMoveDbAdapter
import io.github.sophon.dreamcancel.data.remote.DreamCancelCharacterRemoteAdapter
import io.github.sophon.dreamcancel.data.remote.DreamCancelDataCache
import io.github.sophon.dreamcancel.data.remote.DreamCancelMoveRemoteAdapter
import io.github.sophon.dreamcancel.domain.DreamCancelWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun dreamCancelModule() = module {
    singleOf(::DreamCancelWikiDataSourceImpl).bind<DreamCancelWikiDataSource>()
    single { DreamCancelFeatureInfo }

    single<DreamCancelDB>(named(WikiClientFeature.DreamCancel.id)) {
        val driver = get<SqlDriver>(named(WikiClientFeature.DreamCancel.id)) { parametersOf(DreamCancelDB.Schema) }
        val db = DreamCancelDB(driver = driver)
        db
    }

    DreamCancelFeatureInfo.featureInfo.supportedGameSet.forEach { game ->
        val gameQualifier = named("${WikiClientFeature.DreamCancel.id}:${game.id}")

        single(gameQualifier) {
            DreamCancelDataCache(source = get(), game = game)
        }

        single<CharacterDbAdapter>(gameQualifier) {
            DreamCancelCharacterDbAdapter(
                db = get(named(WikiClientFeature.DreamCancel.id)),
                gameId = game.id,
            )
        }
        single<MoveDbAdapter>(gameQualifier) {
            DreamCancelMoveDbAdapter(
                db = get(named(WikiClientFeature.DreamCancel.id)),
                game = game,
            )
        }
        single<CharacterRemoteAdapter>(gameQualifier) {
            DreamCancelCharacterRemoteAdapter(cache = get(gameQualifier))
        }
        single<MoveRemoteAdapter>(gameQualifier) {
            DreamCancelMoveRemoteAdapter(cache = get(gameQualifier))
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

    factory<WikiClient>(named(WikiClientFeature.DreamCancel.id)) { params ->
        val game = params.get<Game>()
        val gameQualifier = named("${WikiClientFeature.DreamCancel.id}:${game.id}")
        DreamCancelWikiClient(
            game = game,
            dataCache = get(gameQualifier),
            characterRepo = get(gameQualifier),
            moveRepo = get(gameQualifier),
            scope = get(),
        )
    }
}
