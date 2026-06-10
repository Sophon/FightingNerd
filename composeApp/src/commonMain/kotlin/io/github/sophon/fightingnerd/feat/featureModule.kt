package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.feat.home.ui.HomeVM
import io.github.sophon.fightingnerd.feat.home.usecase.EnsureMoveListIsCached
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.moveList.ui.MoveListVM
import io.github.sophon.fightingnerd.feat.moveList.usecase.LoadMoveListDataUseCase
import io.github.sophon.fightingnerd.feat.settings.ui.SettingsVM
import io.github.sophon.fightingnerd.feat.settings.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.settings.usecase.ToggleFeatureUseCase
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

    //region Settings
    singleOf(::GetAvailableFeaturesUseCase)
    singleOf(::ToggleFeatureUseCase)

    viewModelOf(::SettingsVM)
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
