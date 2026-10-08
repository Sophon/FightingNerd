package io.github.sophon.fightingnerd.adapter.outbound.url

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.UrlPort

internal class UrlAdapter(
    private val context: Context,
): UrlPort {
    override fun openUrl(url: String): EmptyResult<AppError> {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val result = try {
            context.startActivity(intent)
            Result.Success(Unit)
        } catch (e: ActivityNotFoundException) {
            Result.Error(AppError.UrlError(e.message ?: "no app can open $url"))
        }

        return result
    }
}
