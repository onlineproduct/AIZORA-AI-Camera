plugins { id("com.android.application") }
android {
    namespace = "com.aizora.aicamera"
    compileSdk = 35
    defaultConfig { applicationId = "com.aizora.aicamera"; minSdk = 26; targetSdk = 28; versionCode = 2; versionName = "1.1" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
dependencies {
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-exoplayer-rtsp:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
}
