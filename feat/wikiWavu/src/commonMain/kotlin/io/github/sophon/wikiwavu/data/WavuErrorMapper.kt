package io.github.sophon.wikiwavu.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.wikiwavu.WavuError

internal fun DataError.Remote.toDomain(): WavuError = WavuError.DOWNLOAD_ERROR