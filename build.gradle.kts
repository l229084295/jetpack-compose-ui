plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
}

extra["gCompileSdkVersion"] = 35
extra["gMinSdkVersion"] = 23
extra["gTargetSdkVersion"] = 34
