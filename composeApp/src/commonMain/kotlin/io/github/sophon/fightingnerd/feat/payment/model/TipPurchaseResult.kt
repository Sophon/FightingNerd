package io.github.sophon.fightingnerd.feat.payment.model

internal sealed interface TipPurchaseResult {
    data object Success : TipPurchaseResult
    data object UserCancelled : TipPurchaseResult
    data class Error(val message: String) : TipPurchaseResult
}
