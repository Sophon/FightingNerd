import model.Character
import model.Move

class UrlProvider {
    fun charUrl(charName: String): String = MOVE_URL + charName

    fun videoUrl(move: Move): String? {
        return move.videoId?.let { VIDEO_URL + it }
    }

    fun followUpUrl(query: String): String? {
        if (query.startsWith("[[").not() || query.endsWith("]]").not()) return null

        val formatted = query
            .substringAfter("[[")
            .substringBefore("|")
            .replace(" ", "_")

        return MOVE_URL + formatted
    }
}