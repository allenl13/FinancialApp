import java.util.Properties

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
        applicationId = "com.example.financialapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // --- BuildConfig fields (from local.properties) ---
        val props = Properties().apply {
            val f = rootProject.file("local.properties")
            if (f.exists()) load(f.inputStream())
        }
        val alphaKey = props.getProperty("ALPHA_VANTAGE_KEY") ?: ""
        val geminiKey = props.getProperty("GEMINI_API_KEY") ?: ""

        buildConfigField("String", "ALPHA_VANTAGE_KEY", "\"$alphaKey\"")

        buildConfigField("String","FIREBASE_API_KEY","\"${props.getProperty("FIREBASE_API_KEY") ?: ""}\"")
        buildConfigField("String","FIREBASE_APP_ID","\"${props.getProperty("FIREBASE_APP_ID") ?: ""}\"")
        buildConfigField("String","FIREBASE_PROJECT_ID","\"${props.getProperty("FIREBASE_PROJECT_ID") ?: ""}\"")
        buildConfigField("String","FIREBASE_SENDER_ID","\"${props.getProperty("FIREBASE_SENDER_ID") ?: ""}\"")
        buildConfigField("String","FIREBASE_STORAGE_BUCKET","\"${props.getProperty("FIREBASE_STORAGE_BUCKET") ?: ""}\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")


    }

    tasks.register("writeGoogleServicesJson") {
        doLast {
            val props = Properties().apply {
                val f = rootProject.file("local.properties")
                if (f.exists()) f.inputStream().use { load(it) }
            }

            val projectId: String = props.getProperty("FIREBASE_PROJECT_ID") ?: ""
            val appId: String = props.getProperty("FIREBASE_APP_ID") ?: ""
            val apiKey: String = props.getProperty("FIREBASE_API_KEY") ?: ""
            val senderId: String = props.getProperty("FIREBASE_SENDER_ID") ?: ""
            val bucket: String = props.getProperty("FIREBASE_STORAGE_BUCKET") ?: ""

            val json = """
            {
              "project_info": {
                "project_id": "$projectId",
                "storage_bucket": "$bucket",
                "project_number": "$senderId"
              },
              "client": [{
                "client_info": {
                  "mobilesdk_app_id": "$appId",
                  "android_client_info": { "package_name": "com.example.financialapp" }
                },
                "api_key": [{ "current_key": "$apiKey" }]
              }],
              "configuration_version": "1"
            }
        """.trimIndent()

            // write into the *app* module
            val out = file("$projectDir/google-services.json")
            out.writeText(json)
            println("Wrote ${out.absolutePath}")
        }
    }

// run the writer before any build
    tasks.named("preBuild").configure {
        dependsOn("writeGoogleServicesJson")
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

    // --- Optional classic Views (only if used somewhere) ---
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

    // --- AI (kept from Login) ---
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
