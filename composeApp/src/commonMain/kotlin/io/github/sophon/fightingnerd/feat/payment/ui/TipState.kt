package io.github.sophon.fightingnerd.feat.payment.ui

import io.github.sophon.fightingnerd.feat.payment.model.TipOption

internal data class TipState(
    val isDialogVisible: Boolean = false,
    val tipOptionList: List<TipOption> = emptyList(),
    val isLoading: Boolean = false,
    val hasLoadError: Boolean = false,
)
