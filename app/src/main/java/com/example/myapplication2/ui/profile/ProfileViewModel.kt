package com.example.myapplication2.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication2.model.UserProfile
import com.example.myapplication2.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository // Зависимость для получения данных о пользователе
) : ViewModel() {

    // Объект, который будет содержать данные профиля пользователя
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // Загружаем данные о пользователе (можно заменить на реальную логику получения данных)
        // Например, извлечение данных из SharedPreferences или базы данных.
        val user = userRepository.getUserProfile() // Пример получения данных о пользователе из репозитория
        _userProfile.value = user
    }
}
