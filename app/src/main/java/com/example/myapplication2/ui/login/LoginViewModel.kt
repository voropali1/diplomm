package com.example.myapplication2.ui.login

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.MainActivity
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.repository.FirebaseRepository
import com.example.myapplication2.repository.StudySetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val studySetRepository: StudySetRepository,
) : ViewModel() {

    val loader = MutableLiveData(false)
    val startMainActivity = MutableLiveData<Unit?>(null)
    val showError = MutableLiveData<String?>(null)

    fun login(email: String) {
        showLoader()
        firebaseRepository.createUser(
            email = email,
            onSuccess = { handleCreateUserSuccess() },
            onError = { handleCreateUserFailure() },
        )
    }

    private fun handleCreateUserSuccess() {
        firebaseRepository.getAllStudySets(
            onSuccess = { handleGetAllStudySetsSuccess(it) },
            onError = { handleGetAllStudySetsFailed() },
        )
    }

    private fun handleGetAllStudySetsFailed() {
        hideLoader()
        showError.value = "Failed to fetch study sets"
        startMainActivity()
    }

    private fun handleGetAllStudySetsSuccess(studySets: List<StudySet>) {
        viewModelScope.launch {
            studySetRepository.insertManyLocalOnly(studySets)
            hideLoader()
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startMainActivity.value = Unit
    }

    private fun handleCreateUserFailure() {
        hideLoader()
        showError.value = "Failed to create user"
    }

    private fun hideLoader() {
        loader.value = false
    }

    private fun showLoader() {
        loader.value = true
    }
}