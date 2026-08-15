package io.github.sophon.fightingnerd.feat.payment.usecase

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitPurchase
import com.revenuecat.purchases.kmp.models.PurchasesErrorCode
import com.revenuecat.purchases.kmp.models.PurchasesException
import com.revenuecat.purchases.kmp.models.PurchasesTransactionException
import io.github.sophon.fightingnerd.feat.payment.model.TipOption
import io.github.sophon.fightingnerd.feat.payment.model.TipPurchaseResult

internal class PurchaseTipUseCase {
    suspend operator fun invoke(option: TipOption): TipPurchaseResult {
        val result = try {
            Purchases.sharedInstance.awaitPurchase(option.rcPackage)
            TipPurchaseResult.Success
        } catch (e: PurchasesTransactionException) {
            if (e.code == PurchasesErrorCode.PurchaseCancelledError) {
                TipPurchaseResult.UserCancelled
            } else {
                TipPurchaseResult.Error(e.message)
            }
        } catch (e: PurchasesException) {
            TipPurchaseResult.Error(e.message)
        }
        return result
    }
}
