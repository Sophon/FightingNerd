package io.github.sophon.fightingnerd.adapter.outbound.share

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SharePort
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal class ShareAdapter(
    private val context: Context,
) : SharePort {

    override suspend fun shareImage(pngBytes: ByteArray, fileName: String): EmptyResult<AppError> {
        val result = try {
            val uri = withContext(Dispatchers.IO) {
                val sharedDir = File(context.cacheDir, SHARED_DIR).apply { mkdirs() }
                val file = File(sharedDir, fileName).apply { writeBytes(pngBytes) }
                val authority = "${context.packageName}.fileprovider"
                val fileUri = FileProvider.getUriForFile(context, authority, file)
                fileUri
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = MIME_PNG
                putExtra(Intent.EXTRA_STREAM, uri)
                clipData = ClipData.newUri(context.contentResolver, fileName, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(chooser)
            Result.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            Result.Error(AppError.ShareError(t.message ?: t::class.simpleName ?: "unknown"))
        }

        return result
    }
}


private const val SHARED_DIR = "shared"
private const val MIME_PNG = "image/png"
