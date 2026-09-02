package io.github.sophon.fightingnerd.feat.payment.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.payment_tip_error_purchase
import fightingnerd.composeapp.generated.resources.payment_tip_thank_you
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.payment.model.PaymentError
import io.github.sophon.fightingnerd.feat.payment.model.TipOption
import io.github.sophon.fightingnerd.feat.payment.usecase.GetTipOptionsUseCase
import io.github.sophon.fightingnerd.feat.payment.usecase.PurchaseTipUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class TipVM(
    private val getTipOptionsUseCase: GetTipOptionsUseCase,
    private val purchaseTipUseCase: PurchaseTipUseCase,
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
        if (current.tipOptionList.isEmpty() && current.isLoading.not()) {
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

    fun onTipOptionSelected(tipOption: TipOption) {
        viewModelScope.launch {
            purchaseTipUseCase(tipOption)
                .onSuccess {
                    val message = getString(Res.string.payment_tip_thank_you)
                    overlay.show(Toast(message = message, type = Toast.Type.SUCCESS))
                }
                .onError { error ->
                    when (error) {
                        PaymentError.UserCancelled -> Unit
                        is PaymentError.Unknown,
                        PaymentError.NoCurrentOffering -> {
                            val message = getString(Res.string.payment_tip_error_purchase)
                            overlay.show(Toast(message = message, type = Toast.Type.ERROR))
                        }
                    }
                }

            _state.update { it.copy(isDialogVisible = false) }
        }
    }


    private fun loadOptions() {
        viewModelScope.launch {
            _state.update { current ->
                val next = current.copy(isLoading = true, hasLoadError = false)
                next
            }
            val result = getTipOptionsUseCase()
            val loaded = when (result) {
                is Result.Success -> result.data
                is Result.Error -> emptyList()
            }
            _state.update { current ->
                val next = current.copy(
                    isLoading = false,
                    tipOptionList = loaded,
                    hasLoadError = result is Result.Error || loaded.isEmpty(),
                )
                next
            }
        }
    }
}
