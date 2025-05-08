package com.example.myapplication2.model

data class UserProfile(
    val username: String,
    val termsCount: Int,
    val completedSets: Int,
    val progress: Int // Процент завершенности
)
