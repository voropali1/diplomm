plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.hilt) // Подключаем Hilt через TOML
  alias(libs.plugins.navigation.safe.args)
  kotlin("kapt") // Подключаем KAPT для аннотаций
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example.myapplication2"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.example.myapplication2"
    minSdk = 24
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
    viewBinding = true
  }

}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.lifecycle.livedata.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.navigation.fragment.ktx)
  implementation(libs.androidx.navigation.ui.ktx)
  implementation(libs.androidx.room.common)
  implementation(libs.androidx.room.ktx)
  implementation(libs.play.services.mlkit.text.recognition.common)
  testImplementation(libs.junit)
  testImplementation("io.mockk:mockk:1.14.2")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
  testImplementation("org.robolectric:robolectric:4.10")
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  implementation("com.github.PhilJay:MPAndroidChart:3.1.0")


  implementation(libs.text.recognition)
  implementation(libs.android.image.cropper)
  implementation(libs.translate)
  implementation(libs.mpandroidchart)

  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services.auth)
  implementation(libs.googleid)

  implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

  // Declare the dependency for the Cloud Firestore library
  // When using the BoM, you don't specify versions in Firebase library dependencies
  implementation("com.google.firebase:firebase-firestore")

  // Dagger Hilt
  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler) // Компилятор Hilt

  // Room
  implementation("androidx.room:room-runtime:2.6.1")
  kapt("androidx.room:room-compiler:2.6.1")
}
