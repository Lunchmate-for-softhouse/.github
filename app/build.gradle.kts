plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.lunchmate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lunchmate"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

dependencies {
    dependencies {
        // Compose BOM to manage all Compose versions
        implementation(platform("androidx.compose:compose-bom:2024.01.00"))

        // Jetpack Compose core and Material libraries
        implementation("androidx.compose.material3:material3:1.1.0-beta01")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.foundation:foundation")
        implementation("androidx.compose.ui:ui-tooling-preview")
        debugImplementation("androidx.compose.ui:ui-tooling")

        // Material icons
        implementation("androidx.compose.material:material-icons-core:1.5.0")
        implementation("androidx.compose.material:material-icons-extended:1.5.0")

        // Navigation for Compose
        implementation("androidx.navigation:navigation-compose:2.8.2")

        // AndroidX libraries
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.core:core-ktx:1.10.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
        implementation("androidx.activity:activity-compose:1.7.2")

        // Testing libraries
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        debugImplementation("androidx.compose.ui:ui-test-manifest")

        // Google Maps SDK and related dependencies
        implementation("com.google.android.gms:play-services-maps:19.0.0")
        implementation("com.google.maps.android:android-maps-utils:2.3.0")
        implementation("com.google.android.libraries.places:places:3.1.0")

        // Firebase dependencies (with BOM)
        implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-firestore-ktx")
        implementation("com.google.firebase:firebase-auth-ktx")

        // Accompanist for permissions
        implementation("com.google.accompanist:accompanist-permissions:0.31.3-beta")

        // Google Sign-In
        implementation("com.google.android.gms:play-services-auth:20.7.0")

        // OkHttp for networking
        implementation("com.squareup.okhttp3:okhttp:4.9.3")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

        // WorkManager
        implementation("androidx.work:work-runtime-ktx:2.8.0")

        // Maps Compose for composable map views
        implementation("com.google.maps.android:maps-compose:2.5.0")
    }




}

