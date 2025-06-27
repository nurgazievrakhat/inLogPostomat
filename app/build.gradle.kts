import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
//    alias(libs.plugins.safe.args.kotlin)
}

android {
    namespace = "com.example.sampleusbproject"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.sampleusbproject"
        minSdk = 24
        targetSdk = 35
        versionCode = 13
        versionName = "1.1.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            buildConfigField ("String", "URL_BASE",  "\"https://postomat-2.ooba.kg\"")
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = false
            buildConfigField ("String", "URL_BASE",  "\"https://postomat-2.ooba.kg\"")
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets.named("main") {
        this.jniLibs.srcDirs("src/main/jniLibs")
    }
    hilt {
        enableAggregatingTask = false
    }

}

dependencies {

implementation(libs.firebase.config)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    //    implementation(files("/Users/rahatnurgaziev/AndroidStudioProjects/sampleUSBproject/app/libs/serialport_x_V1.01.05_20241022.aar"))
//    implementation(files("/Users/rahatnurgaziev/AndroidStudioProjects/sampleUSBproject/app/libs/serialport_x_V1.01.01_20230524.aar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.timber)
    implementation(libs.vbpd)
    implementation(libs.hilt.android)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
//    implementation(libs.navigation.safe.args.gradle.plugin)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.finik)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.decoro)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.flex)

    implementation(libs.socket.io.client) {
        exclude(group = "org.json", module = "json")
    }
    
    kapt(libs.room.compiler)
    kapt(libs.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)
}
kapt {
    correctErrorTypes = true
}