import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.financialapp"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.example.financialapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        //accessing API key
        val localProps = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()){
                load(FileInputStream(file))
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    val retrofitVersion = "3.0.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
//    // OkHttp for networking
//    implementation("com.squareup.okhttp3:okhttp:5.1.0")
//    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")
//    // Coroutines for async operations
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
//    // ViewModel
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.3")
//    implementation("androidx.compose.material3:material3:1.3.2")
}