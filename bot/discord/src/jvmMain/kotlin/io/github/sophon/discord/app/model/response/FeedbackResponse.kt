package io.github.sophon.discord.app.model.response

import io.github.sophon.discord.app.model.UserRequest

data class FeedbackResponse(
    val author: UserRequest.Source,
    val message: String,
    val feedbackChannelIdList: List<String>,
    val dataSource: BotResponse.DataSource,
    val buttonSet: BotResponse.ButtonSet,
): BotResponse
