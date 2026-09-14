package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.BuildKonfig
import io.github.sophon.fightingnerd.core.model.AppVersion
import io.github.sophon.fightingnerd.core.usecase.RefreshUseCase
import io.github.sophon.fightingnerd.feat.changelog.ChangelogClient
import io.github.sophon.fightingnerd.feat.changelog.ChangelogClientImpl
import io.github.sophon.fightingnerd.feat.changelog.data.ChangelogRemoteSource
import io.github.sophon.fightingnerd.feat.changelog.data.ChangelogRemoteSourceImpl
import io.github.sophon.fightingnerd.feat.changelog.usecase.GetUnseenReleaseUseCase
import io.github.sophon.fightingnerd.feat.changelog.usecase.SaveReleaseAsSeenUseCase
import io.github.sophon.fightingnerd.feat.home.ui.HomeVM
import io.github.sophon.fightingnerd.feat.home.usecase.CheckCharacterHasMovesUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.PerformFirstTimeConfigUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.SubscribeToCharacterListUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.SubscribeToGamesUseCase
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.more.ui.MoreVM
import io.github.sophon.fightingnerd.feat.more.ui.about.AboutVM
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsVM
import io.github.sophon.fightingnerd.feat.more.ui.updates.UpdatesVM
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.ManualRefreshUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SaveFeatureConfigUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SetUpdatePeriodUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToThemeUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToUpdatePeriodUseCase
import io.github.sophon.fightingnerd.feat.move.ui.MoveListVM
import io.github.sophon.fightingnerd.feat.move.usecase.DownloadMediaUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.GroupMovesUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveFiltersUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveGroupsUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.NormalizeSliderUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.SubscribeToMoveListUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.SubscribeToOfflineMediaAvailability
import io.github.sophon.fightingnerd.feat.move.usecase.WipeMediaUseCase
import io.github.sophon.fightingnerd.feat.payment.ui.TipVM
import io.github.sophon.fightingnerd.feat.payment.usecase.GetTipOptionsUseCase
import io.github.sophon.fightingnerd.feat.payment.usecase.PurchaseTipUseCase
import io.github.sophon.fightingnerd.feat.quiz.ui.overview.QuizOverviewVM
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.QuizVM
import io.github.sophon.fightingnerd.feat.quiz.usecase.GenerateQuestionsUseCase
import io.github.sophon.fightingnerd.feat.quiz.usecase.SubscribeGameWidgetsUseCase
import io.github.sophon.fightingnerd.core.usecase.RecordInstallationUseCase
import io.github.sophon.fightingnerd.core.usecase.RequestReviewUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun featureModule() = module {
    //region Module
    singleOf(::LoadConfigUseCase)
    singleOf(::WikiClientFactory)
    //endregion

    //region Home
    viewModelOf(::HomeVM)
    singleOf(::PerformFirstTimeConfigUseCase)
    singleOf(::SubscribeToGamesUseCase)
    singleOf(::SubscribeToCharacterListUseCase)
    singleOf(::RefreshUseCase)
    singleOf(::CheckCharacterHasMovesUseCase)
    //endregion

    //region More
    viewModelOf(::MoreVM)

    singleOf(::SubscribeToAvailableFeaturesUseCase)
    singleOf(::SubscribeToThemeUseCase)
    singleOf(::SaveFeatureConfigUseCase)
    singleOf(::SubscribeToUpdatePeriodUseCase)
    singleOf(::SetUpdatePeriodUseCase)
    singleOf(::ManualRefreshUseCase)

    viewModelOf(::FeatureSettingsVM)
    viewModelOf(::UpdatesVM)
    viewModelOf(::AboutVM)
    //endregion

    //region Move
    viewModel { (gameId: String, characterId: String) ->
        MoveListVM(
            gameId = gameId,
            characterId = characterId,
            overlayService = get(),
            subscribeToMoveListUseCase = get(),
            loadMoveFiltersUseCase = get(),
            loadMoveGroupsUseCase = get(),
            normalizeSliderUseCase = get(),
            groupMovesUseCase = get(),
            downloadMediaUseCase = get(),
            wipeMediaUseCase = get(),
            subscribeToOfflineMediaAvailability = get(),
            requestReviewUseCase = get(),
        )
    }
    singleOf(::SubscribeToMoveListUseCase)
    singleOf(::LoadMoveFiltersUseCase)
    singleOf(::NormalizeSliderUseCase)
    singleOf(::LoadMoveGroupsUseCase)
    singleOf(::GroupMovesUseCase)
    singleOf(::DownloadMediaUseCase)
    singleOf(::WipeMediaUseCase)
    singleOf(::SubscribeToOfflineMediaAvailability)
    //endregion

    //region Review
    singleOf(::RequestReviewUseCase)
    singleOf(::RecordInstallationUseCase)
    //endregion

    //region Quiz
    viewModelOf(::QuizOverviewVM)
    viewModel { (gameId: String, characterId: String, onExit: () -> Unit) ->
        QuizVM(
            gameId = gameId,
            characterId = characterId,
            onExit = onExit,
            overlayService = get(),
            generateQuestionsUseCase = get(),
            requestReviewUseCase = get(),
        )
    }

    singleOf(::GenerateQuestionsUseCase)
    singleOf(::SubscribeGameWidgetsUseCase)
    //endregion

    //region Payment
    singleOf(::GetTipOptionsUseCase)
    singleOf(::PurchaseTipUseCase)
    viewModelOf(::TipVM)
    //endregion

    //region Changelog
    single { AppVersion(BuildKonfig.VERSION) }
    singleOf(::ChangelogRemoteSourceImpl).bind<ChangelogRemoteSource>()
    singleOf(::SaveReleaseAsSeenUseCase)
    singleOf(::GetUnseenReleaseUseCase)
    singleOf(::ChangelogClientImpl).bind<ChangelogClient>()
    //endregion
}
