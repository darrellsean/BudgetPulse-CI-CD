plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.budgetpulse"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.budgetpulse"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    val roomVersion = "2.8.4"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Networking & Image Loading
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Jetpack DataStore (user preferences) + Navigation Compose (multi-screen navigation)
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.navigation:navigation-compose:2.9.5")

    // Core Android & Jetpack Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Local Unit Testing (JVM)
    testImplementation(libs.junit)

    // Automated UI Testing (Emulator / Device - updated versions for API 35-37 compatibility)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")

    // Debugging Tools for UI Testing
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation(libs.androidx.compose.ui.tooling)
}

configurations.all {
    resolutionStrategy {
        force("androidx.test.espresso:espresso-core:3.7.0")
        force("androidx.test:runner:1.7.0")
        force("androidx.test:rules:1.7.0")
        force("androidx.test.ext:junit:1.3.0")
    }
}