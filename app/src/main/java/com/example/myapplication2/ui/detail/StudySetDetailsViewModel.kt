package com.example.myapplication2.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StudySetDetailsViewModel @Inject constructor(
) : ViewModel() {

    private val _isStudyMarked = MutableLiveData<Boolean>()
    val isStudyMarked: LiveData<Boolean> get() = _isStudyMarked

    fun markStudyMode(marked: Boolean) {
        _isStudyMarked.value = marked
    }
}

