package io.github.sophon.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual fun httpClientEngine(): HttpClientEngine = Android.create()