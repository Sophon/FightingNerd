package io.github.sophon.wikidragdown.integration

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
import io.github.sophon.wikidragdown.data.DragDownDB
import io.github.sophon.wikidragdown.data.DragDownDataSource
import io.github.sophon.wikidragdown.data.DragDownDataSourceImpl
import io.github.sophon.wikidragdown.data.db.DragDownCharacterDbAdapter
import io.github.sophon.wikidragdown.data.db.DragDownMoveDbAdapter
import io.github.sophon.wikidragdown.data.remote.DragDownCharacterRemoteAdapter
import io.github.sophon.wikidragdown.data.remote.DragDownMoveRemoteAdapter
import io.github.sophon.wikidragdown.domain.DragDownWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun dragDownModule() = module {
    singleOf(::DragDownDataSourceImpl).bind<DragDownDataSource>()
    single { DragDownFeatureInfo }

    single<DragDownDB>(named(WikiClientFeature.DragDown.id)) {
        val driver = get<SqlDriver>(named(WikiClientFeature.DragDown.id))
        DragDownDB.Schema.create(driver)
        val db = DragDownDB(driver = driver)
        db
    }

    DragDownFeatureInfo.featureInfo.supportedGameSet.forEach { game ->
        val gameQualifier = named("${WikiClientFeature.DragDown.id}:${game.id}")

        single<CharacterDbAdapter>(gameQualifier) {
            DragDownCharacterDbAdapter(
                db = get(named(WikiClientFeature.DragDown.id)),
                gameId = game.id,
            )
        }
        single<MoveDbAdapter>(gameQualifier) {
            DragDownMoveDbAdapter(
                db = get(named(WikiClientFeature.DragDown.id)),
                game = game,
            )
        }
        single<CharacterRemoteAdapter>(gameQualifier) {
            DragDownCharacterRemoteAdapter(source = get(), game = game)
        }
        single<MoveRemoteAdapter>(gameQualifier) {
            DragDownMoveRemoteAdapter(source = get(), game = game)
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

    factory<WikiClient>(named(WikiClientFeature.DragDown.id)) { params ->
        val game = params.get<Game>()
        val gameQualifier = named("${WikiClientFeature.DragDown.id}:${game.id}")
        DragDownWikiClient(
            game = game,
            characterRepo = get(gameQualifier),
            moveRepo = get(gameQualifier),
        )
    }
}
