package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.feat.home.ui.HomeVM
import io.github.sophon.fightingnerd.feat.home.usecase.EnsureMoveListIsCached
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.more.ui.MoreVM
import io.github.sophon.fightingnerd.feat.move.ui.MoveListVM
import io.github.sophon.fightingnerd.feat.move.usecase.LoadMoveListDataUseCase
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsVM
import io.github.sophon.fightingnerd.feat.more.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToThemeUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SetThemeUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.ToggleFeatureUseCase
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
    singleOf(::LoadEmptyWidgetsUseCase)
    singleOf(::LoadGameCharacterListUseCase)
    singleOf(::EnsureMoveListIsCached)
    //endregion

    //region More
    viewModelOf(::MoreVM)

    singleOf(::GetAvailableFeaturesUseCase)
    singleOf(::ToggleFeatureUseCase)
    singleOf(::SetThemeUseCase)
    singleOf(::SubscribeToThemeUseCase)

    viewModelOf(::FeatureSettingsVM)
    //endregion

    //region Move list
    viewModel { (gameId: String, characterId: String) ->
        MoveListVM(
            gameId = gameId,
            characterId = characterId,
            loadMoveListDataUseCase = get(),
        )
    }
    singleOf(::LoadMoveListDataUseCase)
    //endregion
}
