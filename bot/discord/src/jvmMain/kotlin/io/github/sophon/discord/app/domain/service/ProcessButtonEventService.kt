package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.ButtonEvent
import io.github.sophon.discord.app.domain.model.MoveId
import io.github.sophon.discord.app.port.inbound.ProcessButtonEventUseCase
import io.github.sophon.discord.app.port.outbound.FrameDataPort
import io.github.sophon.discord.app.domain.model.BotError

internal class ProcessButtonEventService(
    private val frameDataPort: FrameDataPort,
): ProcessButtonEventUseCase {
    override suspend fun invoke(buttonEvent: ButtonEvent): Result<BotResponse, BotError> {
        val result: Result<BotResponse, BotError> = when (buttonEvent) {
            is ButtonEvent.Expand -> expand(buttonEvent.moveId)
            is ButtonEvent.Text -> Result.Success(BotResponse.PlainText(text = buttonEvent.text))
        }
        return result
    }


    /**
     * The expanded embed has nothing left to expand, so the Details button goes.
     */
    private suspend fun expand(moveId: MoveId): Result<BotResponse.MoveResponse, BotError> {
        val result = frameDataPort.getFrameData(moveId)
            .map { moveResponse ->
                val expanded = moveResponse.copy(
                    forceExpand = true,
                    buttonSet = moveResponse.buttonSet
                        ?.filterNot { action -> action is BotResponse.EmbedButton.Action.Expand },
                )
                expanded
            }
        return result
    }

    private fun BotResponse.ButtonSet.filterNot(
        predicate: (BotResponse.EmbedButton.Action) -> Boolean,
    ): BotResponse.ButtonSet? {
        val buttonSet = buttonList
            .filterNot { button -> predicate(button.action) }
            .takeIf { it.isNotEmpty() }
            ?.let { remainingButtonList -> copy(buttonList = remainingButtonList) }
        return buttonSet
    }
}
