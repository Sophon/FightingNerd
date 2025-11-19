package io.github.sophon.xko

import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.xko.data.XkoWikiDataSource
import io.github.sophon.xko.data.XkoWikiDataSourceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun xkoModule() = module {
    singleOf(::XkoWikiDataSourceImpl).bind<XkoWikiDataSource>()
    singleOf(::XkoWikiClient).bind<WikiClient>()

    factory<WikiClient>(named("xko")) { params ->
        val gameId: String = params.get()
        val charListDB: CharacterListDB = params.get()
        val moveListDB: MoveListDB = params.get()

        XkoWikiClient(
            gameId = gameId,
        )
    }
}