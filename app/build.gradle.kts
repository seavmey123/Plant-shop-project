plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "kh.rupp.edu.plantshopproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "kh.rupp.edu.plantshopproject"
        minSdk = 28
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Your existing ones
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.fragment)

    // Navigation
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // RecyclerView & CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Retrofit (API calls to FakeStoreAPI)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp logging (debug API in Logcat)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Glide (load product images from URL)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.3")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.3")

    // Room (cart local database)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // SharedPreferences
    implementation("androidx.preference:preference:1.2.1")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Testing (your existing ones)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}