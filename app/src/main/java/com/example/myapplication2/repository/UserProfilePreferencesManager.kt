package com.example.myapplication2.repository

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit
import javax.inject.Singleton

@Singleton
class UserProfilePreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString(KEY_IS_EMAIL, null) != null
    }

    fun setLoginEmail(email: String) {
        sharedPreferences.edit { putString(KEY_IS_EMAIL, email) }
    }

    fun setUserDocumentId(userDocumentId: String) {
        sharedPreferences.edit { putString(KEY_USER_DOCUMENT_ID, userDocumentId) }
    }

    fun getUserDocumentId(): String? {
        return sharedPreferences.getString(KEY_USER_DOCUMENT_ID, null)
    }

    fun clearLoginEmail() {
        sharedPreferences.edit {
            remove(KEY_IS_EMAIL)
            remove(KEY_USER_DOCUMENT_ID)
        }
    }

    companion object {
        private const val KEY_IS_EMAIL = "key_email"
        private const val KEY_USER_DOCUMENT_ID = "key_user_document_id"
    }
}

