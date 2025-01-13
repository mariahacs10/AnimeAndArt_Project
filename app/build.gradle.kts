import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.practice_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.practice_app"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        android.buildFeatures.buildConfig =true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Use kotlin.properties for reading properties files
        val properties = Properties()

        // Load from local.properties
        val propFile = project.rootProject.file("local.properties")
        properties.load(propFile.inputStream())

        // Get the property value
        val apiKey = properties.getProperty("API_KEY")

        // Set buildConfigField
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.0")
    // Add this line in your dependencies block
    implementation("androidx.compose.material:material:1.6.1")
    implementation("androidx.test.ext:junit-ktx:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.room:room-testing:2.5.2") // For Room database testing
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    implementation ("androidx.compose.runtime:runtime-livedata:1.5.0")  // Ensure this is present
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1") // For LiveData support

    implementation ("androidx.navigation:navigation-compose:2.7.1")

    //coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")


    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Room
    implementation ("androidx.room:room-runtime:2.6.0-rc01")
    kapt("androidx.room:room-compiler:2.5.2")


    // Kotlin Extensions and Coroutines support for Room
    implementation ("androidx.room:room-ktx:2.2.5")

    //google sign in import
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.8.8")

    //Ok Http Dependencies
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation ("com.google.accompanist:accompanist-pager:0.28.0")

}