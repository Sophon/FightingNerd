package io.github.sophon.core.domain

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ExcludeFromCoverage(val reason: String = "")