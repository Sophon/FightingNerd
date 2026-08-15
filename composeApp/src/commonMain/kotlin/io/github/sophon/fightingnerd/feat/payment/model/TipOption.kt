package io.github.sophon.fightingnerd.feat.payment.model

import com.revenuecat.purchases.kmp.models.Package

internal data class TipOption(
    val id: String,
    val formattedPrice: String,
    val rcPackage: Package,
)
