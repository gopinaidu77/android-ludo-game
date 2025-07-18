plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.ludogame"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ludogame"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Compose BOM for consistent versions
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    // Jetpack Compose UI and Material 3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")

    // Optional but recommended
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
