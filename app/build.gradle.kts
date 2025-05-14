plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.noobdev.Zibby"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.noobdev.Zibby"
        minSdk = 24
        targetSdk = 35
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
    implementation("androidx.navigation:navigation-compose:2.7.3")
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("androidx.compose.material3:material3:1.3.0")
    implementation ("androidx.compose.foundation:foundation:1.7.0")// For Column, if used
    debugImplementation ("androidx.compose.ui:ui-tooling:1.7.0") // For previews
    implementation ("androidx.compose.ui:ui-tooling-preview:1.7.0")



    val camerax_version = "1.4.1"
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.maps.android:android-maps-utils:3.4.0")
    implementation("org.maplibre.gl:android-sdk:11.8.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation ("androidx.compose.material:material-icons-extended:1.7.3")

    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-auth")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.x")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.x")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.x")

    // MAPLIBRE
    implementation ("org.maplibre.gl:android-sdk:9.5.0")

// RETROFIT for API calls
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// GSON for parsing JSON
    implementation ("com.google.code.gson:gson:2.10.1")

// OKHTTP for logging (optional, for debug)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:5.8.0") // For Feature, Point, LineString


}