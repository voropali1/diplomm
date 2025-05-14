package com.example.myapplication2.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfilePreferencesManager: UserProfilePreferencesManager,
    private val studySetRepository: StudySetRepository,
) {
    fun isLoggedIn(): Boolean {
        return userProfilePreferencesManager.isLoggedIn()
    }

    suspend fun logout() {
        userProfilePreferencesManager.clearLoginEmail()
        studySetRepository.deleteAll()
    }
}

