package io.github.sophon.fightingnerd.feat.payment.usecase

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.models.PurchasesException
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.feat.payment.model.PaymentError
import io.github.sophon.fightingnerd.feat.payment.model.TipOption

@ExcludeFromCoverage("TODO: later")
internal class GetTipOptionsUseCase {
    suspend operator fun invoke(): Result<List<TipOption>, PaymentError> {
        val result = try {
            val offerings = Purchases.sharedInstance.awaitOfferings()
            val current = offerings.current
            if (current == null) {
                Result.Error(PaymentError.NoCurrentOffering)
            } else {
                val options = current.availablePackages.map { rcPackage ->
                    TipOption(
                        id = rcPackage.identifier,
                        title = rcPackage.storeProduct.title,
                        formattedPrice = rcPackage.storeProduct.price.formatted,
                        rcPackage = rcPackage,
                    )
                }
                Result.Success(options)
            }
        } catch (e: PurchasesException) {
            Result.Error(PaymentError.Unknown(e.message))
        }
        return result
    }
}
