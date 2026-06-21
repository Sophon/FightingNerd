package io.github.sophon.fightingnerd.core.ui

import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

internal class OverlayService {
    private val _toast = Channel<Toast>(Channel.BUFFERED)
    val toast = _toast.receiveAsFlow()

    fun show(toast: Toast) {
        _toast.trySend(toast)
    }

    fun show(error: AppError) {
        show(Toast(message = error.errorMessage, type = Toast.Type.ERROR))
    }
}


data class Toast(
    val message: String,
    val type: Type,
) {
    enum class Type {
        INFO,
        WARNING,
        ERROR,
    }
}
