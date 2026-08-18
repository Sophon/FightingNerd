package io.github.sophon.fightingnerd.feat.payment.model

import io.github.sophon.core.architecture.Error

internal sealed interface PaymentError : Error {
    data object NoCurrentOffering : PaymentError
    data object UserCancelled : PaymentError
    data class Unknown(val message: String) : PaymentError
}
