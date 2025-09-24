// FinancialApp/app/build.gradle.kts  (merged)

import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // from main
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
}

android {
    namespace = "com.example.financialapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.financialapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Tristan: read API key from local.properties -> BuildConfig
        val localProps = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) {
                load(FileInputStream(file))
            }
        }
        val alphaVantageKey: String = localProps.getProperty("ALPHA_VANTAGE_KEY") ?: ""
        buildConfigField("String", "ALPHA_VANTAGE_KEY", "\"$alphaVantageKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Use Java 17 + desugaring (from main)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true            // from both
        buildConfig = true        // from Tristan (needed for BuildConfig.*
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation (main)
    implementation(libs.androidx.navigation.compose)

    // Room (main)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    ksp(libs.androidx.room.compiler)

    // Coroutines (main)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // WorkManager (main)
    implementation(libs.androidx.work.runtime.ktx)

    // Desugaring for java.time on API 24–25 (main)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Retrofit (Tristan)
    val retrofitVersion = "3.0.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // UI constraint (Tristan)
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")
    // For navigation (Tristan)
    implementation("androidx.navigation:navigation-compose:2.8.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
