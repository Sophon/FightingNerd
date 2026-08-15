package io.github.sophon.fightingnerd.feat.payment.usecase

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import io.github.sophon.fightingnerd.feat.payment.model.TipOption

internal class GetTipOptionsUseCase {
    suspend operator fun invoke(): Result<List<TipOption>> = runCatching {
        val offerings = Purchases.sharedInstance.awaitOfferings()
        val packages = offerings.current?.availablePackages.orEmpty()
        val options = packages.map { rcPackage ->
            TipOption(
                id = rcPackage.identifier,
                formattedPrice = rcPackage.storeProduct.price.formatted,
                rcPackage = rcPackage,
            )
        }
        options
    }
}
