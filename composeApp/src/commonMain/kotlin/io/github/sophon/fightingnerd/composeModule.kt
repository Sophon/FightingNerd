package io.github.sophon.fightingnerd

import io.github.sophon.core.coreModule
import io.github.sophon.dreamcancel.integration.dreamCancelModule
import io.github.sophon.fightingnerd.adapter.inbound.home.HomeVM
import io.github.sophon.fightingnerd.adapter.outbound.compose.ComposeAdapter
import io.github.sophon.fightingnerd.adapter.outbound.dataStore.DataStoreAdapter
import io.github.sophon.fightingnerd.adapter.outbound.wiki.WikiAdapter
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.EnabledGamesPort
import io.github.sophon.fightingnerd.app.outPort.FirstLaunchPort
import io.github.sophon.fightingnerd.app.outPort.LoadConfigPort
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import io.github.sophon.fightingnerd.app.service.CheckCharacterHasMovesService
import io.github.sophon.fightingnerd.app.service.OnLaunchSetupService
import io.github.sophon.fightingnerd.app.service.RefreshDataService
import io.github.sophon.fightingnerd.app.service.SubscribeToCharactersService
import io.github.sophon.fightingnerd.app.service.SubscribeToGamesService
import io.github.sophon.fightingnerd.core.coreModule
import io.github.sophon.fightingnerd.core.usecase.RefreshUseCase
import io.github.sophon.fightingnerd.feat.featureModule
import io.github.sophon.fightingnerd.inPort.CheckCharacterHasMovesUseCase
import io.github.sophon.fightingnerd.inPort.OnLaunchSetupUseCase
import io.github.sophon.fightingnerd.inPort.RefreshDataUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToCharactersUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToGamesUseCase
import io.github.sophon.wiki.wikiModule
import io.github.sophon.wikiSuperCombo.integration.superComboModule
import io.github.sophon.wikidragdown.integration.dragDownModule
import io.github.sophon.wikidustloop.integration.dustLoopModule
import io.github.sophon.wikimizuumi.integration.mizuumiModule
import io.github.sophon.wikiwavu.integration.wavuModule
import io.github.sophon.xko.integration.xkoModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        platformModule,

        coreModule,
        wavuModule(),
        superComboModule(),
        xkoModule(),
        dreamCancelModule(),
        dustLoopModule(),
        mizuumiModule(),
        dragDownModule(),
        wikiModule(),

        featureModule(),
        coreModule(),
        composeModule(),
    )
}

internal fun composeModule() = module {
    viewModelOf(::HomeVM)

    singleOf(::RefreshUseCase)

    singleOf(::OnLaunchSetupService).bind<OnLaunchSetupUseCase>()
    singleOf(::RefreshDataService).bind<RefreshDataUseCase>()
    singleOf(::SubscribeToGamesService).bind<SubscribeToGamesUseCase>()
    singleOf(::SubscribeToCharactersService).bind<SubscribeToCharactersUseCase>()
    singleOf(::CheckCharacterHasMovesService).bind<CheckCharacterHasMovesUseCase>()

    singleOf(::WikiAdapter) {
        bind<ConfigureWikiPort>()
        bind<RefreshWikiPort>()
        bind<AvailableGamesPort>()
        bind<CharacterPort>()
        bind<MovePort>()
    }
    singleOf(::ComposeAdapter).bind<LoadConfigPort>()
    singleOf(::DataStoreAdapter) {
        bind<FirstLaunchPort>()
        bind<SaveGameSettingsPort>()
        bind<EnabledGamesPort>()
        bind<SubscribeToGameSettingsPort>()
    }
}

internal expect val platformModule: Module
