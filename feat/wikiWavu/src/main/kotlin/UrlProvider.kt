import model.Character
import model.Move

class UrlProvider {
    fun moveUrl(character: Character, move: Move): String {
        return MOVE_URL + "${character.name}_movelist" + "#${character.name}-${move.id}"
    }

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