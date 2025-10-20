import model.GlossaryItem

class InfilUrlProvider {
    fun termUrl(item: GlossaryItem): String {
        return TERM_URL + item.term
    }

    fun videoUrl(item: GlossaryItem): String? {
        if (item.video.isEmpty()) return null

        return VIDEO_URL + item.term + ".mp4"
    }
}