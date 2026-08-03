package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.feat.home.ui.HomeVM
import io.github.sophon.fightingnerd.feat.home.usecase.CheckIfFirstLaunchUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadMoveListUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.more.ui.MoreVM
import io.github.sophon.fightingnerd.feat.move.ui.MoveListVM
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveListDataUseCase
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsVM
import io.github.sophon.fightingnerd.feat.more.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SaveFeatureConfigUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToThemeUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveFiltersUseCase
import io.github.sophon.fightingnerd.feat.move.usecase.NormalizeSliderUseCase
import io.github.sophon.fightingnerd.feat.quiz.ui.overview.QuizOverviewVM
import io.github.sophon.fightingnerd.feat.quiz.ui.quiz.QuizVM
import io.github.sophon.fightingnerd.feat.quiz.usecase.GenerateQuestionsUseCase
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
    singleOf(::LoadEmptyWidgetsUseCase)
    singleOf(::LoadGameCharacterListUseCase)
    singleOf(::LoadMoveListUseCase)
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
            loadMoveListDataUseCase = get(),
            loadMoveFiltersUseCase = get(),
            normalizeSliderUseCase = get(),
        )
    }
    singleOf(::LoadMoveListDataUseCase)
    singleOf(::LoadMoveFiltersUseCase)
    singleOf(::NormalizeSliderUseCase)
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
    //endregion
}
