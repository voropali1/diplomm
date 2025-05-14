package com.example.myapplication2.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfilePreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    fun isLoggedIn(): Boolean {
        return getUserDocumentId() != null
    }

    fun setUserDocumentId(userDocumentId: String) {
        sharedPreferences.edit { putString(KEY_USER_DOCUMENT_ID, userDocumentId) }
    }

    fun getUserDocumentId(): String? {
        return sharedPreferences.getString(KEY_USER_DOCUMENT_ID, null)
    }

    fun clearLoginEmail() {
        sharedPreferences.edit {
            remove(KEY_USER_DOCUMENT_ID)
        }
    }

    companion object {
        private const val KEY_USER_DOCUMENT_ID = "key_user_document_id"
    }
}

