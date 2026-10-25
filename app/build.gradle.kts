plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.quiz"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.quiz"
        minSdk = 26
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Material Design et Drawer
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")

    // 🔥 GEMINI AI - VERSION STABLE
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Guava
    implementation("com.google.guava:guava:32.1.2-android")
}