package io.github.sophon.fightingnerd.feat.payment.usecase

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.models.PurchasesErrorCode
import com.revenuecat.purchases.kmp.models.PurchasesException
import com.revenuecat.purchases.kmp.models.PurchasesTransactionException
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.feat.payment.model.PaymentError
import io.github.sophon.fightingnerd.feat.payment.model.TipOption

internal class PurchaseTipUseCase {
    suspend fun invoke(option: TipOption): EmptyResult<PaymentError> {
        val result = try {
            Purchases.sharedInstance.awaitPurchase(option.rcPackage)
            Result.Success(Unit)
        } catch (e: PurchasesTransactionException) {
            val error = if (e.code == PurchasesErrorCode.PurchaseCancelledError) {
                PaymentError.UserCancelled
            } else {
                PaymentError.Unknown(e.message)
            }
            Result.Error(error)
        } catch (e: PurchasesException) {
            Result.Error(PaymentError.Unknown(e.message))
        }
        return result
    }
}
