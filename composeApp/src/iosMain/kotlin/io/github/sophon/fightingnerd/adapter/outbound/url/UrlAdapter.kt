package io.github.sophon.fightingnerd.adapter.outbound.url

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.UrlPort
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

internal class UrlAdapter: UrlPort {
    override fun openUrl(url: String): EmptyResult<AppError> {
        val nsUrl = NSURL.URLWithString(url)
            ?: return Result.Error(AppError.UrlError("invalid url: $url"))

        UIApplication.sharedApplication.openURL(
            url = nsUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = null,
        )
        return Result.Success(Unit)
    }
}
