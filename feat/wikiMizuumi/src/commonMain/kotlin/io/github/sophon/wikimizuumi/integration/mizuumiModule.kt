package io.github.sophon.wikimizuumi.integration

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
import io.github.sophon.wikimizuumi.data.MizuumiDB
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSource
import io.github.sophon.wikimizuumi.data.MizuumiWikiDataSourceImpl
import io.github.sophon.wikimizuumi.data.db.MizuumiCharacterDbAdapter
import io.github.sophon.wikimizuumi.data.db.MizuumiMoveDbAdapter
import io.github.sophon.wikimizuumi.data.remote.MizuumiCharacterRemoteAdapter
import io.github.sophon.wikimizuumi.data.remote.MizuumiDataCache
import io.github.sophon.wikimizuumi.data.remote.MizuumiMoveRemoteAdapter
import io.github.sophon.wikimizuumi.domain.MizuumiWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun mizuumiModule() = module {
    singleOf(::MizuumiWikiDataSourceImpl).bind<MizuumiWikiDataSource>()
    single { MizuumiFeatureInfo }

    single<MizuumiDB>(named(WikiClientFeature.Mizuumi.id)) {
        val driver = get<SqlDriver>(named(WikiClientFeature.Mizuumi.id))
        MizuumiDB.Schema.create(driver)
        val db = MizuumiDB(driver = driver)
        db
    }

    MizuumiFeatureInfo.featureInfo.supportedGameSet.forEach { game ->
        val gameQualifier = named("${WikiClientFeature.Mizuumi.id}:${game.id}")

        single(gameQualifier) {
            MizuumiDataCache(source = get(), game = game)
        }

        single<CharacterDbAdapter>(gameQualifier) {
            MizuumiCharacterDbAdapter(
                db = get(named(WikiClientFeature.Mizuumi.id)),
                gameId = game.id,
            )
        }
        single<MoveDbAdapter>(gameQualifier) {
            MizuumiMoveDbAdapter(
                db = get(named(WikiClientFeature.Mizuumi.id)),
                game = game,
            )
        }
        single<CharacterRemoteAdapter>(gameQualifier) {
            MizuumiCharacterRemoteAdapter(
                source = get(),
                game = game,
                cache = get(gameQualifier),
            )
        }
        single<MoveRemoteAdapter>(gameQualifier) {
            MizuumiMoveRemoteAdapter(
                source = get(),
                game = game,
                cache = get(gameQualifier),
            )
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

    factory<WikiClient>(named(WikiClientFeature.Mizuumi.id)) { params ->
        val game = params.get<Game>()
        val gameQualifier = named("${WikiClientFeature.Mizuumi.id}:${game.id}")
        MizuumiWikiClient(
            game = game,
            dataCache = get(gameQualifier),
            characterRepo = get(gameQualifier),
            moveRepo = get(gameQualifier),
        )
    }
}
