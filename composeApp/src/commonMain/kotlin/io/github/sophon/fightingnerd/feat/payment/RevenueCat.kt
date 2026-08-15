package io.github.sophon.fightingnerd.feat.payment

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import io.github.aakira.napier.Napier

internal fun initRevenueCat(apiKey: String) {
    if (apiKey.isBlank()) {
        Napier.w { "RevenueCat API key missing — skipping SDK init. Set REVENUECAT_API_KEY in local.properties or env." }
        return
    }

    Purchases.configure(PurchasesConfiguration.Builder(apiKey = apiKey).build())
}
