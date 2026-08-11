package io.github.sophon.xko.integration

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
import io.github.sophon.xko.data.XkoDB
import io.github.sophon.xko.data.XkoWikiDataSource
import io.github.sophon.xko.data.XkoWikiDataSourceImpl
import io.github.sophon.xko.data.db.XkoCharacterDbAdapter
import io.github.sophon.xko.data.db.XkoMoveDbAdapter
import io.github.sophon.xko.data.remote.XkoCharacterRemoteAdapter
import io.github.sophon.xko.data.remote.XkoDataCache
import io.github.sophon.xko.data.remote.XkoMoveRemoteAdapter
import io.github.sophon.xko.domain.XkoWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun xkoModule() = module {
    singleOf(::XkoWikiDataSourceImpl).bind<XkoWikiDataSource>()
    single { XkoFeatureInfo }

    single<XkoDB>(named(WikiClientFeature.Xko.id)) {
        val driver = get<SqlDriver>(named(WikiClientFeature.Xko.id)) { parametersOf(XkoDB.Schema) }
        val db = XkoDB(driver = driver)
        db
    }

    XkoFeatureInfo.featureInfo.supportedGameSet.forEach { game ->
        val gameQualifier = named("${WikiClientFeature.Xko.id}:${game.id}")

        single(gameQualifier) {
            XkoDataCache(source = get())
        }

        single<CharacterDbAdapter>(gameQualifier) {
            XkoCharacterDbAdapter(
                db = get(named(WikiClientFeature.Xko.id)),
                gameId = game.id,
            )
        }
        single<MoveDbAdapter>(gameQualifier) {
            XkoMoveDbAdapter(
                db = get(named(WikiClientFeature.Xko.id)),
                gameId = game.id,
            )
        }
        single<CharacterRemoteAdapter>(gameQualifier) {
            XkoCharacterRemoteAdapter(cache = get(gameQualifier))
        }
        single<MoveRemoteAdapter>(gameQualifier) {
            XkoMoveRemoteAdapter(cache = get(gameQualifier))
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

    factory<WikiClient>(named(WikiClientFeature.Xko.id)) { params ->
        val game = params.get<Game>()
        val gameQualifier = named("${WikiClientFeature.Xko.id}:${game.id}")
        XkoWikiClient(
            game = game,
            source = get(),
            dataCache = get(gameQualifier),
            characterRepo = get(gameQualifier),
            moveRepo = get(gameQualifier),
        )
    }
}
