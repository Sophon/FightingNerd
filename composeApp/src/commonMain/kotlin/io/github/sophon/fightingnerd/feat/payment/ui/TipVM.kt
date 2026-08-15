package io.github.sophon.fightingnerd.feat.payment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.tip_error_purchase
import fightingnerd.composeapp.generated.resources.tip_thank_you
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.payment.model.TipOption
import io.github.sophon.fightingnerd.feat.payment.model.TipPurchaseResult
import io.github.sophon.fightingnerd.feat.payment.usecase.GetTipOptionsUseCase
import io.github.sophon.fightingnerd.feat.payment.usecase.PurchaseTipUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class TipVM(
    private val getTipOptions: GetTipOptionsUseCase,
    private val purchaseTip: PurchaseTipUseCase,
    private val overlay: OverlayService,
) : ViewModel() {
    private val _state = MutableStateFlow(TipState())
    val state = _state.asStateFlow()

    init {
        loadOptions()
    }

    fun onTipButtonClick() {
        _state.update { current ->
            val next = current.copy(isDialogVisible = true)
            next
        }
        val current = _state.value
        if (current.options.isEmpty() && current.isLoading.not()) {
            loadOptions()
        }
    }

    fun onDismissDialog() {
        _state.update { current ->
            val next = current.copy(isDialogVisible = false)
            next
        }
    }

    fun onRetryLoad() {
        loadOptions()
    }

    fun onTipOptionSelected(option: TipOption) {
        viewModelScope.launch {
            val result = purchaseTip(option)
            handlePurchaseResult(result)
        }
    }

    private fun loadOptions() {
        viewModelScope.launch {
            _state.update { current ->
                val next = current.copy(isLoading = true, hasLoadError = false)
                next
            }
            val result = getTipOptions()
            val loaded = result.getOrDefault(emptyList())
            _state.update { current ->
                val next = current.copy(
                    isLoading = false,
                    options = loaded,
                    hasLoadError = result.isFailure || loaded.isEmpty(),
                )
                next
            }
        }
    }

    private suspend fun handlePurchaseResult(result: TipPurchaseResult) {
        _state.update { current ->
            val next = current.copy(isDialogVisible = false)
            next
        }
        when (result) {
            TipPurchaseResult.Success -> {
                val message = getString(Res.string.tip_thank_you)
                overlay.show(Toast(message = message, type = Toast.Type.SUCCESS))
            }
            TipPurchaseResult.UserCancelled -> Unit
            is TipPurchaseResult.Error -> {
                val message = getString(Res.string.tip_error_purchase)
                overlay.show(Toast(message = message, type = Toast.Type.ERROR))
            }
        }
    }
}
