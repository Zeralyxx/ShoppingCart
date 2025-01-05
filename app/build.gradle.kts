plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.shoppingcart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shoppingcart"
        minSdk = 27
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.analytics.impl)
    implementation("androidx.cardview:cardview:1.0.0") // Add this line
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    implementation ("com.google.firebase:firebase-database:20.3.0") // Or the latest version
    implementation ("com.google.firebase:firebase-auth:22.3.0") // Or the latest version
    implementation ("com.google.firebase:firebase-firestore:24.10.1") // Or the latest version
    implementation ("com.google.firebase:firebase-storage:20.3.0") // Or the latest version
    implementation ("com.google.firebase:firebase-messaging:23.4.0") // Or the latest version
    implementation ("com.google.firebase:firebase-analytics:21.5.0") // Or the latest version
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
}
