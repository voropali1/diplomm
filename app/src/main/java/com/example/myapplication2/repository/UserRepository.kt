package com.example.myapplication2.repository

import android.content.SharedPreferences
import com.example.myapplication2.model.UserProfile
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences // Используем SharedPreferences для примера
) {

    fun getUserProfile(): UserProfile {
        // Здесь получаем данные пользователя (вместо SharedPreferences можно использовать базу данных или API)
        val username = sharedPreferences.getString("username", "User") ?: "User"
        val termsCount = sharedPreferences.getInt("termsCount", 0)
        val completedSets = sharedPreferences.getInt("completedSets", 0)
        val progress = sharedPreferences.getInt("progress", 0)

        return UserProfile(username, termsCount, completedSets, progress)
    }
}
