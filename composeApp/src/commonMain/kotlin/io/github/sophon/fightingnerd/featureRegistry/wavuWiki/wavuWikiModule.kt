package io.github.sophon.fightingnerd.featureRegistry.wavuWiki

import io.github.sophon.fightingnerd.QUALIFIER_WAVU
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.ui.WavuHomeScreenVM
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.usecase.FetchCharacterListUseCase
import io.github.sophon.fightingnerd.featureRegistry.wavuWiki.usecase.SyncDataIfOldUseCase
import io.github.sophon.fightingnerd.screens.moveList.domain.usecase.FetchMoveListUseCase
import io.github.sophon.wikiwavu.WavuWikiClient
import io.github.sophon.wikiwavu.infrastructure.FileReader
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

internal val wavuWikiFeatureModule = module {
    viewModelOf(::WavuHomeScreenVM)

    singleOf(::SyncDataIfOldUseCase)
    singleOf(::FetchCharacterListUseCase)

    singleOf(::FileReaderKMP).bind<FileReader>()

    single<FetchMoveListUseCase>(named(QUALIFIER_WAVU)) {
        FetchMoveListUseCase(get<WavuWikiClient>()::getMoveListFor)
    }
}