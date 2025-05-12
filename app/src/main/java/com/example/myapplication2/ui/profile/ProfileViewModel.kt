package com.example.myapplication2.ui.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.UserProfile
import com.example.myapplication2.repository.StudySetRepository
import com.example.myapplication2.repository.UserProfileRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val repository: StudySetRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    private val _totalSets = MutableLiveData<Int>()
    val totalSets: LiveData<Int> get() = _totalSets

    init {
        loadProfile()
        repository.allStudySets.observeForever { sets ->
            _totalSets.value = sets?.size ?: 0
        }
    }

    // Загрузка данных профиля из SharedPreferences
    private fun loadProfile() {
        val username = sharedPreferences.getString("username", "Guest") ?: "Guest"
        val completedSets = sharedPreferences.getInt("completedSets", 0)

        _userProfile.value = UserProfile(username, completedSets)
    }

    // Обновление количества завершённых сетов
    fun updateCompletedSets(isSetCompletedSuccessfully: Boolean) {

        if (isSetCompletedSuccessfully) {
            val updatedProfile = _userProfile.value?.copy(
                completedSets = (_userProfile.value?.completedSets ?: 0) + 1
            )
            updatedProfile?.let {
                _userProfile.value = it
                saveProfile(it)
            }
        }
    }

    fun decreaseCompletedSets() {
        val updatedProfile = _userProfile.value?.copy(
            completedSets = (_userProfile.value?.completedSets ?: 0) - 1
        )
        updatedProfile?.let {
            _userProfile.value = it
            saveProfile(it)
        }
    }

    private fun saveProfile(profile: UserProfile) {
        with(sharedPreferences.edit()) {
            putString("username", profile.username)
            putInt("completedSets", profile.completedSets)
            apply()
        }
    }
}




