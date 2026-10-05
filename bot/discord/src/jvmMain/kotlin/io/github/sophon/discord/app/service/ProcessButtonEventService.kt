package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.BotResponse
import io.github.sophon.discord.app.model.ButtonEvent
import io.github.sophon.discord.app.model.Command
import io.github.sophon.discord.app.model.MoveId
import io.github.sophon.discord.inPort.ProcessButtonEventUseCase
import io.github.sophon.discord.app.outPort.FrameDataPort
import io.github.sophon.discord.app.outPort.StatsPort

/**
 * - expand: [flow-expand.mmd](../../../docs/flow-expand.mmd)
 * - move list: [flow-move_list.mmd](../../../docs/flow-move_list.mmd)
 */
internal class ProcessButtonEventService(
    private val frameDataPort: FrameDataPort,
    private val commandRouterService: CommandRouterService,
    private val statsPort: StatsPort,
): ProcessButtonEventUseCase {
    override suspend fun invoke(buttonEvent: ButtonEvent): Result<BotResponse, BotError> {
        val result: Result<BotResponse, BotError> = when (buttonEvent) {
            is ButtonEvent.Expand -> expand(buttonEvent.moveId)
            is ButtonEvent.Query -> {
                val queryResult = frameDataPort.getFrameData(buttonEvent.moveId)
                recordUsage(command = Command.Fd, result = queryResult)
                queryResult
            }
            is ButtonEvent.Text -> Result.Success(BotResponse.PlainText(text = buttonEvent.text))
            is ButtonEvent.Forward -> Result.Success(BotResponse.Redirect(channelId = buttonEvent.channelId))
            is ButtonEvent.Command -> {
                val commandResult = commandRouterService(
                    command = buttonEvent.command,
                    query = buttonEvent.query,
                    source = null,
                )
                recordUsage(command = buttonEvent.command, result = commandResult)
                commandResult
            }
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

    /**
     * Stats are best-effort - a failed record is logged by the stats feature and never fails the response.
     */
    private suspend fun recordUsage(
        command: Command,
        result: Result<BotResponse, BotError>,
    ) {
        result
            .onSuccess { response -> statsPort.register(command = command, game = response.game) }
            .onError { statsPort.registerFailure() }
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
