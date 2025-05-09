package com.example.myapplication2.di

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val COMPLETED_SETS_KEY = "completed_sets"
    }

    fun getCompletedSets(): Int {
        return sharedPreferences.getInt(COMPLETED_SETS_KEY, 0)
    }

    fun incrementCompletedSets() {
        val current = getCompletedSets()
        sharedPreferences.edit().putInt(COMPLETED_SETS_KEY, current + 1).apply()
    }

    fun resetCompletedSets() {
        sharedPreferences.edit().putInt(COMPLETED_SETS_KEY, 0).apply()
    }
}
