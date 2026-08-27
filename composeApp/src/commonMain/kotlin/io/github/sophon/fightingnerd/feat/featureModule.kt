package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.feat.home.ui.HomeVM
import io.github.sophon.fightingnerd.feat.home.usecase.CheckCharacterHasMovesUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.CheckIfFirstLaunchUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.RefreshUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.SubscribeToCharacterListUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.SubscribeToGamesUseCase
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.more.ui.MoreVM
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsVM
import io.github.sophon.fightingnerd.feat.more.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SaveFeatureConfigUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToThemeUseCase
import io.github.sophon.fightingnerd.feat.move.ui.MoveListVM
import io.github.sophon.fightingnerd.feat.move.usecase.DownloadMediaUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.GroupMovesUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveFiltersUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveGroupsUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.NormalizeSliderUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.SubscribeToMoveListUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.WipeMediaUseCase
import io.github.sophon.fightingnerd.feat.payment.ui.TipVM
import io.github.sophon.fightingnerd.feat.payment.usecase.GetTipOptionsUseCase
import io.github.sophon.fightingnerd.feat.payment.usecase.PurchaseTipUseCase
import io.github.sophon.fightingnerd.feat.quiz.ui.overview.QuizOverviewVM
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.QuizVM
import io.github.sophon.fightingnerd.feat.quiz.usecase.GenerateQuestionsUseCase
import io.github.sophon.fightingnerd.feat.quiz.usecase.SubscribeGameWidgetsUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun featureModule() = module {
    //region Module
    singleOf(::LoadConfigUseCase)
    singleOf(::WikiClientFactory)
    //endregion

    //region Home
    viewModelOf(::HomeVM)
    singleOf(::CheckIfFirstLaunchUseCase)
    singleOf(::SubscribeToGamesUseCase)
    singleOf(::SubscribeToCharacterListUseCase)
    singleOf(::RefreshUseCase)
    singleOf(::CheckCharacterHasMovesUseCase)
    //endregion

    //region More
    viewModelOf(::MoreVM)

    singleOf(::GetAvailableFeaturesUseCase)
    singleOf(::SubscribeToThemeUseCase)
    singleOf(::SaveFeatureConfigUseCase)

    viewModelOf(::FeatureSettingsVM)
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
        )
    }
    singleOf(::SubscribeToMoveListUseCase)
    singleOf(::LoadMoveFiltersUseCase)
    singleOf(::NormalizeSliderUseCase)
    singleOf(::LoadMoveGroupsUseCase)
    singleOf(::GroupMovesUseCase)
    singleOf(::DownloadMediaUseCase)
    singleOf(::WipeMediaUseCase)
    //endregion

    //region Quiz
    viewModelOf(::QuizOverviewVM)
    viewModel { (gameId: String, onExit: () -> Unit) ->
        QuizVM(
            gameId = gameId,
            onExit = onExit,
            overlayService = get(),
            generateQuestionsUseCase = get(),
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
}
