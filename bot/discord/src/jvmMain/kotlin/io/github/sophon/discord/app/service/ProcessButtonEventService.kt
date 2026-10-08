package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.ButtonEvent
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.frameData.MoveId
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.MoveResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.RedirectResponse
import io.github.sophon.discord.app.outPort.ForwardPort
import io.github.sophon.discord.app.outPort.FrameDataPort
import io.github.sophon.discord.app.outPort.StatsPort
import io.github.sophon.discord.inPort.ProcessButtonEventUseCase

/**
 * - expand: [flow-expand.mmd](../../../docs/flow-expand.mmd)
 * - move list: [flow-move_list.mmd](../../../docs/flow-move_list.mmd)
 */
internal class ProcessButtonEventService(
    private val frameDataPort: FrameDataPort,
    private val commandRouterService: CommandRouterService,
    private val statsPort: StatsPort,
    private val forwardPort: ForwardPort,
): ProcessButtonEventUseCase {
    override suspend fun invoke(buttonEvent: ButtonEvent): Result<BotResponse, BotError> {
        val result: Result<BotResponse, BotError> = when (buttonEvent) {
            is ButtonEvent.Expand -> expand(buttonEvent.moveId)
            is ButtonEvent.Query -> {
                val queryResult = frameDataPort.getFrameData(buttonEvent.moveId)
                recordUsage(command = Command.Fd, result = queryResult)
                queryResult
            }
            is ButtonEvent.Text -> Result.Success(PlainTextResponse(text = buttonEvent.text))
            is ButtonEvent.Forward -> {
                val forwardResult = forwardPort.forward(
                    sourceChannelId = buttonEvent.sourceChannelId,
                    sourceMessageId = buttonEvent.sourceMessageId,
                    targetChannelId = buttonEvent.targetChannelId,
                ).map { RedirectResponse(channelId = buttonEvent.targetChannelId) }
                forwardResult
            }
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
     * The expanded embed shows everything, media included, so no buttons are left.
     */
    private suspend fun expand(moveId: MoveId): Result<MoveResponse, BotError> {
        val result = frameDataPort.getFrameData(moveId)
            .map { moveResponse ->
                val expanded = moveResponse.copy(
                    forceExpand = true,
                    buttonSet = null,
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
}
