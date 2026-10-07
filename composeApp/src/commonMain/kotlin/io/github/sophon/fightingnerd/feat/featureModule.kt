package io.github.sophon.fightingnerd.feat

import io.github.sophon.fightingnerd.core.usecase.RecordInstallationUseCase
import io.github.sophon.fightingnerd.core.usecase.RequestReviewUseCase
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase
import io.github.sophon.fightingnerd.feat.payment.ui.TipVM
import io.github.sophon.fightingnerd.feat.payment.usecase.GetTipOptionsUseCase
import io.github.sophon.fightingnerd.feat.payment.usecase.PurchaseTipUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal fun featureModule() = module {
    //region Module
    singleOf(::LoadConfigUseCase)
    singleOf(::WikiClientFactory)
    //endregion

    //region Review
    singleOf(::RequestReviewUseCase)
    singleOf(::RecordInstallationUseCase)
    //endregion

    //region Payment
    singleOf(::GetTipOptionsUseCase)
    singleOf(::PurchaseTipUseCase)
    viewModelOf(::TipVM)
    //endregion
}
