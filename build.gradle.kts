plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.outrageousstorm.batterymonitor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.outrageousstorm.batterymonitor"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:1.5.3")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.core:core:1.10.1")
}
