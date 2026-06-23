package io.github.sophon.fightingnerd.feat.more.model

import io.github.sophon.fightingnerd.feat.more.URL_BUY_ME_COFFEE
import io.github.sophon.fightingnerd.feat.more.URL_KOFI

internal enum class DonationMethod(val url: String) {
    KoFi(URL_KOFI),
    BuyMeCoffee(URL_BUY_ME_COFFEE),
}
