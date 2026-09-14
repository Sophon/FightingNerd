package io.github.sophon.fightingnerd.core.domain

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

internal class UrlOpenerIos: UrlOpener {
    override fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(
            url = nsUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = null,
        )
    }
}
