// Файл: build.gradle.kts (Top-level)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false // Добавь alias для Hilt
    alias(libs.plugins.navigation.safe.args) apply false
}



