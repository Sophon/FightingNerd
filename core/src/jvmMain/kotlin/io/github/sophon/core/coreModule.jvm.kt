package io.github.sophon.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

actual fun httpClientEngine(): HttpClientEngine = CIO.create()