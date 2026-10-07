package io.github.sophon.fightingnerd

import io.github.sophon.core.coreModule
import io.github.sophon.dreamcancel.integration.dreamCancelModule
import io.github.sophon.fightingnerd.adapter.inbound.home.HomeVM
import io.github.sophon.fightingnerd.adapter.inbound.more.about.AboutVM
import io.github.sophon.fightingnerd.adapter.inbound.more.featureSettings.FeatureSettingsVM
import io.github.sophon.fightingnerd.adapter.inbound.more.MoreVM
import io.github.sophon.fightingnerd.adapter.inbound.more.updates.UpdatesVM
import io.github.sophon.fightingnerd.adapter.inbound.payment.TipVM
import io.github.sophon.fightingnerd.adapter.inbound.move.MoveListVM
import io.github.sophon.fightingnerd.adapter.inbound.quiz.QuizOverviewVM
import io.github.sophon.fightingnerd.adapter.inbound.quiz.QuizGameVM
import io.github.sophon.fightingnerd.adapter.outbound.compose.ComposeAdapter
import io.github.sophon.fightingnerd.adapter.outbound.dataStore.DataStoreAdapter
import io.github.sophon.fightingnerd.adapter.outbound.ktor.KtorAdapter
import io.github.sophon.fightingnerd.adapter.outbound.media.MediaAdapter
import io.github.sophon.fightingnerd.adapter.outbound.revenueCat.RevenueCatAdapter
import io.github.sophon.fightingnerd.adapter.outbound.wiki.WikiAdapter
import io.github.sophon.fightingnerd.app.model.AppVersion
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.EnabledGamesPort
import io.github.sophon.fightingnerd.app.outPort.FirstLaunchPort
import io.github.sophon.fightingnerd.app.outPort.InstallationPort
import io.github.sophon.fightingnerd.app.outPort.LoadConfigPort
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.AvailableWikisPort
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.LastUpdatePort
import io.github.sophon.fightingnerd.app.outPort.LastSeenReleasePort
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.app.outPort.MoveFilterPort
import io.github.sophon.fightingnerd.app.outPort.MoveGroupPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.ReleasePort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToGameSettingsPort
import io.github.sophon.fightingnerd.app.outPort.TipPort
import io.github.sophon.fightingnerd.app.service.CheckCharacterHasMovesService
import io.github.sophon.fightingnerd.app.service.DownloadMediaService
import io.github.sophon.fightingnerd.app.service.GenerateQuestionsService
import io.github.sophon.fightingnerd.app.service.GetTipOptionsService
import io.github.sophon.fightingnerd.app.service.GroupMovesService
import io.github.sophon.fightingnerd.app.service.LoadMoveFiltersService
import io.github.sophon.fightingnerd.app.service.PurchaseTipService
import io.github.sophon.fightingnerd.app.service.RecordInstallationService
import io.github.sophon.fightingnerd.app.service.RequestReviewService
import io.github.sophon.fightingnerd.app.service.ShareImageService
import io.github.sophon.fightingnerd.app.service.OnLaunchSetupService
import io.github.sophon.fightingnerd.app.service.RefreshGamesService
import io.github.sophon.fightingnerd.app.service.RefreshDataService
import io.github.sophon.fightingnerd.app.service.SaveGameSettingsService
import io.github.sophon.fightingnerd.app.service.SaveReleaseAsSeenService
import io.github.sophon.fightingnerd.app.service.SetUpdatePeriodService
import io.github.sophon.fightingnerd.app.service.SubscribeToCharactersService
import io.github.sophon.fightingnerd.app.service.SubscribeToGameSettingsService
import io.github.sophon.fightingnerd.app.service.SubscribeToGamesService
import io.github.sophon.fightingnerd.app.service.SubscribeToLastUpdatesService
import io.github.sophon.fightingnerd.app.service.SubscribeToMoveListService
import io.github.sophon.fightingnerd.app.service.SubscribeToOfflineMediaService
import io.github.sophon.fightingnerd.app.service.SubscribeToUnseenReleaseService
import io.github.sophon.fightingnerd.app.service.SubscribeToUpdatePeriodService
import io.github.sophon.fightingnerd.app.service.SubscribeToWikisService
import io.github.sophon.fightingnerd.app.service.WipeMediaService
import io.github.sophon.fightingnerd.core.coreModule
import io.github.sophon.fightingnerd.core.usecase.RefreshUseCase
import io.github.sophon.fightingnerd.feat.featureModule
import io.github.sophon.fightingnerd.inPort.CheckCharacterHasMovesUseCase
import io.github.sophon.fightingnerd.inPort.DownloadMediaUseCase
import io.github.sophon.fightingnerd.inPort.GenerateQuestionsUseCase
import io.github.sophon.fightingnerd.inPort.GetTipOptionsUseCase
import io.github.sophon.fightingnerd.inPort.GroupMovesUseCase
import io.github.sophon.fightingnerd.inPort.LoadMoveFiltersUseCase
import io.github.sophon.fightingnerd.inPort.PurchaseTipUseCase
import io.github.sophon.fightingnerd.inPort.RecordInstallationUseCase
import io.github.sophon.fightingnerd.inPort.RequestReviewUseCase
import io.github.sophon.fightingnerd.inPort.ShareImageUseCase
import io.github.sophon.fightingnerd.inPort.OnLaunchSetupUseCase
import io.github.sophon.fightingnerd.inPort.RefreshGamesUseCase
import io.github.sophon.fightingnerd.inPort.RefreshDataUseCase
import io.github.sophon.fightingnerd.inPort.SaveGameSettingsUseCase
import io.github.sophon.fightingnerd.inPort.SaveReleaseAsSeenUseCase
import io.github.sophon.fightingnerd.inPort.SetUpdatePeriodUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToCharactersUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToGameSettingsUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToGamesUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToLastUpdatesUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToMoveListUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToOfflineMediaUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToUnseenReleaseUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToUpdatePeriodUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToWikisUseCase
import io.github.sophon.fightingnerd.inPort.WipeMediaUseCase
import io.github.sophon.wiki.wikiModule
import io.github.sophon.wikiSuperCombo.integration.superComboModule
import io.github.sophon.wikidragdown.integration.dragDownModule
import io.github.sophon.wikidustloop.integration.dustLoopModule
import io.github.sophon.wikimizuumi.integration.mizuumiModule
import io.github.sophon.wikiwavu.integration.wavuModule
import io.github.sophon.xko.integration.xkoModule
import okio.FileSystem
import okio.SYSTEM
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
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
    viewModel { (gameId: String, characterId: String) ->
        MoveListVM(
            gameId = gameId,
            characterId = characterId,
            subscribeToOfflineMediaUseCase = get(),
            overlayService = get(),
            subscribeToMoveListUseCase = get(),
            loadMoveFiltersUseCase = get(),
            groupMovesUseCase = get(),
            downloadMediaUseCase = get(),
            wipeMediaUseCase = get(),
            requestReviewUseCase = get(),
            shareImageUseCase = get(),
        )
    }
    viewModelOf(::QuizOverviewVM)
    viewModel { (gameId: String, characterId: String?, onExit: () -> Unit) ->
        QuizGameVM(
            gameId = gameId,
            characterId = characterId,
            onExit = onExit,
            overlayService = get(),
            generateQuestionsUseCase = get(),
            requestReviewUseCase = get(),
        )
    }
    viewModelOf(::MoreVM)
    viewModelOf(::FeatureSettingsVM)
    viewModelOf(::UpdatesVM)
    viewModelOf(::AboutVM)
    viewModelOf(::TipVM)

    single { AppVersion(BuildKonfig.VERSION) }

    singleOf(::RefreshUseCase)

    singleOf(::OnLaunchSetupService).bind<OnLaunchSetupUseCase>()
    singleOf(::RefreshDataService).bind<RefreshDataUseCase>()
    singleOf(::SubscribeToGamesService).bind<SubscribeToGamesUseCase>()
    singleOf(::SubscribeToCharactersService).bind<SubscribeToCharactersUseCase>()
    singleOf(::CheckCharacterHasMovesService).bind<CheckCharacterHasMovesUseCase>()
    singleOf(::SubscribeToMoveListService).bind<SubscribeToMoveListUseCase>()
    singleOf(::LoadMoveFiltersService).bind<LoadMoveFiltersUseCase>()
    singleOf(::GroupMovesService).bind<GroupMovesUseCase>()
    singleOf(::DownloadMediaService).bind<DownloadMediaUseCase>()
    singleOf(::WipeMediaService).bind<WipeMediaUseCase>()
    singleOf(::SubscribeToOfflineMediaService).bind<SubscribeToOfflineMediaUseCase>()
    singleOf(::GenerateQuestionsService).bind<GenerateQuestionsUseCase>()
    singleOf(::SubscribeToGameSettingsService).bind<SubscribeToGameSettingsUseCase>()
    singleOf(::SaveGameSettingsService).bind<SaveGameSettingsUseCase>()
    singleOf(::SubscribeToLastUpdatesService).bind<SubscribeToLastUpdatesUseCase>()
    singleOf(::RefreshGamesService).bind<RefreshGamesUseCase>()
    singleOf(::SubscribeToUpdatePeriodService).bind<SubscribeToUpdatePeriodUseCase>()
    singleOf(::SetUpdatePeriodService).bind<SetUpdatePeriodUseCase>()
    singleOf(::SubscribeToWikisService).bind<SubscribeToWikisUseCase>()
    singleOf(::SubscribeToUnseenReleaseService).bind<SubscribeToUnseenReleaseUseCase>()
    singleOf(::SaveReleaseAsSeenService).bind<SaveReleaseAsSeenUseCase>()
    singleOf(::GetTipOptionsService).bind<GetTipOptionsUseCase>()
    singleOf(::PurchaseTipService).bind<PurchaseTipUseCase>()
    singleOf(::RequestReviewService).bind<RequestReviewUseCase>()
    singleOf(::RecordInstallationService).bind<RecordInstallationUseCase>()
    singleOf(::ShareImageService).bind<ShareImageUseCase>()

    singleOf(::WikiAdapter) {
        bind<ConfigureWikiPort>()
        bind<RefreshWikiPort>()
        bind<AvailableGamesPort>()
        bind<AvailableWikisPort>()
        bind<CharacterPort>()
        bind<MovePort>()
        bind<MoveFilterPort>()
        bind<MoveGroupPort>()
        bind<LastUpdatePort>()
    }
    singleOf(::ComposeAdapter).bind<LoadConfigPort>()
    singleOf(::KtorAdapter).bind<ReleasePort>()
    singleOf(::RevenueCatAdapter).bind<TipPort>()
    single<MediaPort> {
        MediaAdapter(
            fs = FileSystem.SYSTEM,
            baseDir = get(),
            http = get(),
            store = get(),
        )
    }
    singleOf(::DataStoreAdapter) {
        bind<FirstLaunchPort>()
        bind<SaveGameSettingsPort>()
        bind<EnabledGamesPort>()
        bind<SubscribeToGameSettingsPort>()
        bind<LastSeenReleasePort>()
        bind<InstallationPort>()
    }
}

internal expect val platformModule: Module
