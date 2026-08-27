package io.github.sophon.wikiwavu.integration

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
import io.github.sophon.wikiwavu.data.WavuDB
import io.github.sophon.wikiwavu.data.db.WavuCharacterDbAdapter
import io.github.sophon.wikiwavu.data.db.WavuMoveDbAdapter
import io.github.sophon.wikiwavu.data.remote.WavuCharacterRemoteAdapter
import io.github.sophon.wikiwavu.data.remote.WavuMoveRemoteAdapter
import io.github.sophon.wikiwavu.data.remote.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.remote.WavuWikiDataSourceImpl
import io.github.sophon.wikiwavu.domain.WavuWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun wavuModule() = module {
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    single { WavuFeatureInfo }

    single<WavuDB>(named(WikiClientFeature.Wavu.id)) {
        val driver = get<SqlDriver>(named(WikiClientFeature.Wavu.id)) { parametersOf(WavuDB.Schema) }
        val db = WavuDB(driver = driver)
        db
    }

    single<CharacterDbAdapter>(named(WikiClientFeature.Wavu.id)) { params ->
        WavuCharacterDbAdapter(
            db = get(named(WikiClientFeature.Wavu.id)) { params },
            game = Game.Tekken8,
        )
    }
    single<MoveDbAdapter>(named(WikiClientFeature.Wavu.id)) { params ->
        WavuMoveDbAdapter(
            db = get(named(WikiClientFeature.Wavu.id)) { params },
        )
    }
    single<CharacterRemoteAdapter>(named(WikiClientFeature.Wavu.id)) {
        WavuCharacterRemoteAdapter(source = get())
    }
    single<MoveRemoteAdapter>(named(WikiClientFeature.Wavu.id)) { params ->
        WavuMoveRemoteAdapter(
            source = get(),
            game = params.get(),
        )
    }

    single<CharacterRepo>(named(WikiClientFeature.Wavu.id)) { params ->
        CharacterRepoImpl(
            dbAdapter = get(named(WikiClientFeature.Wavu.id)) { params },
            remoteAdapter = get(named(WikiClientFeature.Wavu.id)),
        )
    }
    single<MoveRepo>(named(WikiClientFeature.Wavu.id)) { params ->
        MoveRepoImpl(
            dbAdapter = get(named(WikiClientFeature.Wavu.id)) { params },
            remoteAdapter = get(named(WikiClientFeature.Wavu.id)) { params },
        )
    }

    factory<WikiClient>(named(WikiClientFeature.Wavu.id)) { params ->
        WavuWikiClient(
            game = params.get(),
            characterRepo = get(named(WikiClientFeature.Wavu.id)) { params },
            moveRepo = get(named(WikiClientFeature.Wavu.id)) { params },
            scope = get(),
        )
    }
}
