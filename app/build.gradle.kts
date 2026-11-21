plugins {
    id("com.android.application")
}

android {
    namespace = "ca.cuniversel.locamobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "ca.cuniversel.locamobile"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    implementation("androidx.appcompat:appcompat:1.7.0")
    // Exercice 1, étape 1
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
}
