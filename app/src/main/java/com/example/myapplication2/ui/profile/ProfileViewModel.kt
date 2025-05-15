package com.example.myapplication2.ui.profile

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.UserProfile
import com.example.myapplication2.repository.FirebaseRepository
import com.example.myapplication2.repository.StudySetRepository
import com.example.myapplication2.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val repository: StudySetRepository,
    private val userProfileRepository: UserProfileRepository,
    private val firebaseRepository: FirebaseRepository,
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    val statistics: LiveData<Statistics>
        get() = repository.allStudySets.map { sets ->
            Statistics(totalSets = sets.size, completedSets = sets.count { it.isFinished })
        }

    val showLoader = MutableLiveData<Boolean?>(null)

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val username = sharedPreferences.getString("username", "Maksim") ?: "Maksim"

        _userProfile.value = UserProfile(username)
    }

    fun updateCompletedSets(studySet: StudySet) {
        viewModelScope.launch {
            repository.updateSetFinishedStatus(studySet.id)
        }
    }

    suspend fun signOut() {
        userProfileRepository.logout()
    }

    fun sync() {
        showLoader.value = true
        firebaseRepository.getAllStudySets(
            onSuccess = { studySets ->
                viewModelScope.launch {
                    repository.insertManyLocalOnly(studySets)
                    showLoader.value = false
                }
            },
            onError = {
                showLoader.value = false
            },
        )
    }
}

data class Statistics(
    val totalSets: Int,
    val completedSets: Int,
)



