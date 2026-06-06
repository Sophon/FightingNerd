package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.feat.bottomBar.ui.BottomBarVM
import io.github.sophon.fightingnerd.feat.home.ui.HomeVM
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.EnsureMoveListIsCached
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.moveList.ui.MoveListVM
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun featureModule() = module {
    viewModelOf(::BottomBarVM)

    //region Module
    singleOf(::LoadConfigUseCase)
    singleOf(::WikiClientFactory)
    //endregion

    //region Home
    viewModelOf(::HomeVM)
    singleOf(::LoadEmptyWidgetsUseCase)
    singleOf(::LoadGameCharacterListUseCase)
    //endregion

    //region
    viewModel { (gameId: String, characterId: String) ->
        MoveListVM(
            gameId = gameId,
            characterId = characterId,
        )
    }
    singleOf(::EnsureMoveListIsCached)
    //endregion
}
