package io.github.sophon

import io.github.sophon.domain.StatsFeatureInfo
import org.koin.dsl.module

fun statsModule() = module {
    single { StatsFeatureInfo }
}
