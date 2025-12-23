package io.github.sophon.glossaryinfil.domain

import io.github.sophon.core.util.urlEncode
import io.github.sophon.glossaryinfil.TERM_URL
import io.github.sophon.glossaryinfil.VIDEO_URL

class InfilUrlProvider {
    fun termUrl(item: GlossaryItem): String {
        return TERM_URL + item.term.urlEncode()
    }
}