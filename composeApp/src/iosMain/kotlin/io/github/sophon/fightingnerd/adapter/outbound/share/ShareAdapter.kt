package io.github.sophon.fightingnerd.adapter.outbound.share

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SharePort
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class ShareAdapter : SharePort {

    override suspend fun shareImage(pngBytes: ByteArray, fileName: String): EmptyResult<AppError> {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: return Result.Error(AppError.ShareError("no root view controller"))

        val image = withContext(Dispatchers.IO) {
            val nsData = pngBytes.usePinned { pinned ->
                NSData.dataWithBytes(pinned.addressOf(0), pngBytes.size.toULong())
            }
            val uiImage = UIImage(data = nsData)
            uiImage
        }

        val activityVc = UIActivityViewController(
            activityItems = listOf(image),
            applicationActivities = null,
        )
        rootViewController.presentViewController(activityVc, animated = true, completion = null)
        return Result.Success(Unit)
    }
}
