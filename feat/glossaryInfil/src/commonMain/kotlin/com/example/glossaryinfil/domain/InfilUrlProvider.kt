package com.example.glossaryinfil.domain

import com.example.core.util.urlEncode
import com.example.glossaryinfil.TERM_URL
import com.example.glossaryinfil.VIDEO_URL

class InfilUrlProvider {
    fun termUrl(item: GlossaryItem): String {
        return TERM_URL + item.term.urlEncode()
    }

    fun videoUrl(item: GlossaryItem): String? {
        if (item.video.isEmpty()) return null

        return VIDEO_URL + item.term.urlEncode() + ".mp4"
    }
}