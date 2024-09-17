plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.acuityquick"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.acuityquick"
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
    implementation("junit:junit:4.12")
    implementation("junit:junit:4.12")
    testImplementation(libs.junit)
    testImplementation ("org.mockito:mockito-core:5.12.0")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation ("androidx.test:runner:1.4.0")
    androidTestImplementation ("adroidx.test:rules:1.4.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Room components
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

}