package io.github.sophon.fightingnerd.core.ui

import androidx.compose.runtime.Composable
import io.github.sophon.fightingnerd.core.model.AppError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.plus

internal class OverlayService {
    private val _toast = Channel<Toast>(Channel.BUFFERED)
    val toast = _toast.receiveAsFlow()
    private val _dialogStack = MutableStateFlow<List<Dialog>>(emptyList())
    val currentDialog: MutableStateFlow<Dialog?> = MutableStateFlow(null)

    fun show(toast: Toast) {
        _toast.trySend(toast)
    }

    fun show(error: AppError) {
        show(Toast(message = error.errorMessage, type = Toast.Type.ERROR))
    }

    fun show(dialog: Dialog) {
        _dialogStack.update { it + dialog }
        updateVisibleDialog()
    }

    fun popDialog() {
        _dialogStack.update {
            if (it.isNotEmpty()) it.dropLast(1) else it
        }
        updateVisibleDialog()
    }


    private fun updateVisibleDialog() {
        currentDialog.value = _dialogStack.value.lastOrNull()
    }
}


internal data class Toast(
    val message: String,
    val type: Type,
) {
    enum class Type {
        INFO,
        WARNING,
        ERROR,
    }
}

internal data class Dialog(
    val content: @Composable (onDismiss: () -> Unit) -> Unit,
)
