package io.github.sophon.fightingnerd.feat.share

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
internal class ShareSheetImpl : ShareSheet {

    override suspend fun shareImage(pngBytes: ByteArray, fileName: String) {
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
        UIApplication.sharedApplication.keyWindow
            ?.rootViewController
            ?.presentViewController(activityVc, animated = true, completion = null)
    }
}
