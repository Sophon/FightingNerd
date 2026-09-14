# Project-specific ProGuard/R8 rules.
# Add keeps here as R8 surfaces issues in release builds.
# Most libraries (Compose, Coil, Koin, SQLDelight, Ktor, kotlinx-serialization)
# ship their own consumer rules in their AAR/JAR.

# Play Core KTX references a Play Services annotation that isn't on the classpath.
-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite