package io.github.sophon.fightingnerd.adapter.inbound.payment

import io.github.sophon.fightingnerd.app.model.TipOption

internal data class TipState(
    val isDialogVisible: Boolean = false,
    val tipOptionList: List<TipOption> = emptyList(),
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false,
)
