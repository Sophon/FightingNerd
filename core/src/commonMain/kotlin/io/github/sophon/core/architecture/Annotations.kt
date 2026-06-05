package io.github.sophon.core.architecture

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ExcludeFromCoverage(val reason: String = "")