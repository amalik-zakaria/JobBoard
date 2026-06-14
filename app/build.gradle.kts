plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.jobboard"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.jobboard"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Fragment
    implementation(libs.fragment)

    // RecyclerView
    implementation(libs.recyclerview)

    // Retrofit 2 avec Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)

    // Google Maps SDK
    implementation(libs.play.services.maps)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}