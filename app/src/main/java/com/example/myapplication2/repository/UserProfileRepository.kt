package com.example.myapplication2.repository

import android.content.SharedPreferences
import javax.inject.Inject

class UserProfileRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val TERMS_COUNT_KEY = "terms_count"
        private const val PROGRESS_KEY = "progress"
    }

    // Получаем количество выученных терминов
    fun getTermsCount(): Int {
        return sharedPreferences.getInt(TERMS_COUNT_KEY, 0)
    }

    // Получаем прогресс
    fun getProgress(): Int {
        return sharedPreferences.getInt(PROGRESS_KEY, 0)
    }

    // Обновляем данные
    fun updateProgress(termsCount: Int, progress: Int) {
        sharedPreferences.edit().apply {
            putInt(TERMS_COUNT_KEY, termsCount)
            putInt(PROGRESS_KEY, progress)
            apply()
        }
    }
}

