plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
apply(plugin = "com.google.gms.google-services")

android {
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("mozilla/public-suffix-list.txt")
    }
    namespace = "com.example.japonnews"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.japonnews"
        minSdk = 23
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
        viewBinding = true
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.volley)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.inappmessaging.display)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.viewpager2)
    implementation (libs.firebase.auth.v2211)
    implementation (libs.firebase.database)
    implementation (libs.picasso)
    implementation(libs.firebase.bom)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.storage.v2021)
    implementation (libs.firebase.firestore)
    implementation (libs.glide)
    implementation (libs.gson)
    annotationProcessor(libs.compiler)
    implementation(libs.firebase.messaging)
    implementation(libs.google.auth.library.oauth2.http)
    implementation("com.amazonaws:aws-android-sdk-core:2.79.0")
    implementation("com.amazonaws:aws-android-sdk-sns:2.79.0")
    implementation ("com.amazonaws:aws-android-sdk-cognitoidentityprovider:2.79.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation("com.google.api-client:google-api-client:1.32.1")
}
