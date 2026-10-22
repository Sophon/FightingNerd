package io.github.sophon.discord.app.model.response

sealed interface MediaResponse: BotResponse {
    /**
     * [url] is the character's wiki url - the main embed and every image embed must share it,
     * so Discord groups them into one post.
     */
    data class ImagesMediaResponse(
        val characterName: String,
        val input: String,
        val url: String,
        val imageList: List<String> = emptyList(),
        val dataSource: BotResponse.DataSource,
    ): MediaResponse

    data class VideoMediaResponse(
        val characterName: String,
        val input: String,
        val videoUrl: String? = null,
        val dataSource: BotResponse.DataSource,
    ): MediaResponse

    object NoMedia: MediaResponse
}
