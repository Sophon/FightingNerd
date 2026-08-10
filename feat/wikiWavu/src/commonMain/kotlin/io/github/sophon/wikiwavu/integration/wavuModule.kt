package io.github.sophon.wikiwavu.integration

import app.cash.sqldelight.db.SqlDriver
import io.github.sophon.core.featureConfig.model.WikiClientFeature
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.wikiwavu.data.CharacterRepo
import io.github.sophon.wikiwavu.data.CharacterRepoImpl
import io.github.sophon.wikiwavu.data.MoveRepo
import io.github.sophon.wikiwavu.data.MoveRepoImpl
import io.github.sophon.wikiwavu.data.WavuDB
import io.github.sophon.wikiwavu.data.remote.WavuWikiDataSource
import io.github.sophon.wikiwavu.data.remote.WavuWikiDataSourceImpl
import io.github.sophon.wikiwavu.domain.WavuWikiClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun wavuModule() = module {
    singleOf(::WavuWikiDataSourceImpl).bind<WavuWikiDataSource>()
    single<WavuDB>(named(WikiClientFeature.Wavu.id)) { params ->
        WavuDB(driver = params.get<SqlDriver>())
    }
    single<MoveRepo>(named(WikiClientFeature.Wavu.id)) { params ->
        MoveRepoImpl(
            db = get(named(WikiClientFeature.Wavu.id)) { params },
            source = get(),
        )
    }
    single<CharacterRepo>(named(WikiClientFeature.Wavu.id)) { params ->
        CharacterRepoImpl(
            db = get(named(WikiClientFeature.Wavu.id)) { params },
            source = get(),
        )
    }

    single { WavuFeatureInfo }

    factory<WikiClient>(named(WikiClientFeature.Wavu.id)) { params ->
        WavuWikiClient(
            game = params.get(),
            source = get(),
            characterDB = params.get(),
            moveDB = params.get(),
        )
    }
}
