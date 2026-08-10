package io.github.sophon.wikiSuperCombo.integration

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
import io.github.sophon.wikiSuperCombo.data.SuperComboDB
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSourceImpl
import io.github.sophon.wikiSuperCombo.data.db.SuperComboCharacterDbAdapter
import io.github.sophon.wikiSuperCombo.data.db.SuperComboMoveDbAdapter
import io.github.sophon.wikiSuperCombo.data.remote.SuperComboCharacterRemoteAdapter
import io.github.sophon.wikiSuperCombo.data.remote.SuperComboMoveRemoteAdapter
import io.github.sophon.wikiSuperCombo.domain.SuperComboWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun superComboModule() = module {
    singleOf(::SuperComboDataSourceImpl).bind<SuperComboDataSource>()
    single { SuperComboFeatureInfo }

    single<SuperComboDB>(named(WikiClientFeature.SuperCombo.id)) {
        val driver = get<SqlDriver>(named(WikiClientFeature.SuperCombo.id))
        SuperComboDB.Schema.create(driver)
        val db = SuperComboDB(driver = driver)
        db
    }

    SuperComboFeatureInfo.featureInfo.supportedGameSet.forEach { game ->
        val gameQualifier = named("${WikiClientFeature.SuperCombo.id}:${game.id}")

        single<CharacterDbAdapter>(gameQualifier) {
            SuperComboCharacterDbAdapter(
                db = get(named(WikiClientFeature.SuperCombo.id)),
                gameId = game.id,
            )
        }
        single<MoveDbAdapter>(gameQualifier) {
            SuperComboMoveDbAdapter(
                db = get(named(WikiClientFeature.SuperCombo.id)),
                game = game,
            )
        }
        single<CharacterRemoteAdapter>(gameQualifier) {
            SuperComboCharacterRemoteAdapter(source = get(), game = game)
        }
        single<MoveRemoteAdapter>(gameQualifier) {
            SuperComboMoveRemoteAdapter(source = get(), game = game)
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

    factory<WikiClient>(named(WikiClientFeature.SuperCombo.id)) { params ->
        val game = params.get<Game>()
        val gameQualifier = named("${WikiClientFeature.SuperCombo.id}:${game.id}")
        SuperComboWikiClient(
            game = game,
            source = get(),
            characterDB = params.get(),
            moveDB = params.get(),
            characterRepo = get(gameQualifier),
            moveRepo = get(gameQualifier),
        )
    }
}
