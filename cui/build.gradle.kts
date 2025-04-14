plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    id("kotlin-parcelize")
}

val gCompileSdkVersion: Int by project
val gMinSdkVersion: Int by project
val gTargetSdkVersion: Int by project

android {
    namespace = "cn.idesign.cui"
    compileSdk = gCompileSdkVersion
    defaultConfig {
        minSdk = gMinSdkVersion
        targetSdk = gTargetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("com.github.promeg:tinypinyin:2.0.3")
    api("androidx.compose.ui:ui-util:1.7.8")
    api("com.google.accompanist:accompanist-pager:0.36.0")
    api("com.google.accompanist:accompanist-flowlayout:0.36.0")
    api("androidx.compose.foundation:foundation:1.2.0-alpha05")
    api("androidx.compose.foundation:foundation-layout:1.2.0-alpha05")
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.compose)
    implementation(libs.coil.video)

    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation(libs.androidx.constraintlayout.compose)

    //图片加载
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")
}

//tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).configureEach {
//    kotlinOptions {
//        freeCompilerArgs += "-opt-in=androidx.constraintlayout.compose.ExperimentalMotionApi"
//    }
//}
