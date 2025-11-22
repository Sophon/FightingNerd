package io.github.sophon.core.domain

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ExcludeFromCoverage(val reason: String = "")