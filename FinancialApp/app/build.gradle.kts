import java.util.Properties
import groovy.json.JsonOutput

val APP_PKG        = "com.example.financialapp"

val FB_API_KEY     = System.getenv("FIREBASE_API_KEY")      ?: "AIzaSyDWT6duYZziRfooWRNVbWUXTKB2b5-Z-T0"
val FB_APP_ID      = System.getenv("FIREBASE_APP_ID")       ?: "1:393051596324:android:0d1fa68dfb5cf6c1a09ab1"
val FB_PROJECT_ID  = System.getenv("FIREBASE_PROJECT_ID")   ?: "financiallogin-64260"
val FB_SENDER_ID   = System.getenv("FIREBASE_SENDER_ID")    ?: "393051596324"
val FB_BUCKET      = System.getenv("FIREBASE_STORAGE_BUCKET") ?: "financiallogin-64260.firebasestorage.app"
val ALPHA_VANTAGE_KEY_DEFAULT = System.getenv("ALPHA_VANTAGE_KEY")?: "GQ7IP0EF6N68ZSEX"
val GEMINI_API_KEY_DEFAULT = System.getenv("GEMINI_API_KEY") ?: "AIzaSyD0F9PgEg5w3gOkka-sbanLwc6sMjCq5yo"

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Use the version-catalog managed KSP & Google Services plugins
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.gms.google.services)
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
        applicationId = APP_PKG
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // --- BuildConfig fields ---
        val props = Properties().apply {
            val f = rootProject.file("local.properties")
            if (f.exists()) load(f.inputStream())
        }

        val alphaKey = props.getProperty("ALPHA_VANTAGE_KEY") ?: ALPHA_VANTAGE_KEY_DEFAULT
        val geminiKey = props.getProperty("GEMINI_API_KEY") ?: GEMINI_API_KEY_DEFAULT

        buildConfigField("String", "ALPHA_VANTAGE_KEY", "\"$alphaKey\"")

        // Firebase (from constants/env so a fresh clone still builds)
        buildConfigField("String", "FIREBASE_API_KEY", "\"$FB_API_KEY\"")
        buildConfigField("String", "FIREBASE_APP_ID", "\"$FB_APP_ID\"")
        buildConfigField("String", "FIREBASE_PROJECT_ID", "\"$FB_PROJECT_ID\"")
        buildConfigField("String", "FIREBASE_SENDER_ID", "\"$FB_SENDER_ID\"")
        buildConfigField("String", "FIREBASE_STORAGE_BUCKET", "\"$FB_BUCKET\"")

        // Gemini
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
    }

    // Generate google-services.json before the Google Services plugin runs
    val writeGoogleServicesJson by tasks.registering {
        doLast {
            val json = mapOf(
                "project_info" to mapOf(
                    "project_number" to FB_SENDER_ID,
                    "project_id" to FB_PROJECT_ID,
                    "storage_bucket" to FB_BUCKET
                ),
                "client" to listOf(
                    mapOf(
                        "client_info" to mapOf(
                            "mobilesdk_app_id" to FB_APP_ID,
                            "android_client_info" to mapOf("package_name" to APP_PKG)
                        ),
                        "api_key" to listOf(mapOf("current_key" to FB_API_KEY))
                    )
                ),
                "configuration_version" to "1"
            )
            val out = file("$projectDir/google-services.json") // app/google-services.json
            out.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(json)))
            println("Wrote ${out.absolutePath}")
        }
    }

    // Ensure our JSON exists before all google-services tasks
    tasks.matching { it.name.endsWith("GoogleServices") }.configureEach {
        dependsOn(writeGoogleServicesJson)
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // --- Core + Compose ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.runtime.livedata)

    // --- Room + KSP ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- Firebase (Auth + Firestore via BoM) ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // --- Optional classic Views ---
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)

    // --- WorkManager ---
    implementation(libs.androidx.work.runtime.ktx)

    // --- Desugaring for java.time on API 24–25 ---
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // ConstraintLayout for Compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0")

    // --- AI ---
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // --- Tests ---
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
}
