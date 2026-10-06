package io.github.sophon.app.util

/**
 * Tekken IDs are displayed as `xxxx-xxxx-xxxx`, ewgf.gg expects them without the dashes.
 */
internal fun String.toPolarisId(): String = replace("-", "")
