package com.example.myapplication2.ui.studyset.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.repository.StudySetRepository
import com.example.myapplication2.ui.profile.ProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudySetsListViewModel @Inject constructor(
    private val repository: StudySetRepository,
) : ViewModel() {

    private val _allStudySets = MutableLiveData<List<StudySet>>()
    val allStudySets: LiveData<List<StudySet>> get() = _allStudySets
    private lateinit var profileViewModel: ProfileViewModel

    init {
        loadStudySets()
    }

    private fun loadStudySets() {
        repository.allStudySets.observeForever { sets ->
            _allStudySets.value = sets
        }
    }

    fun deleteStudySet(studySet: StudySet) = viewModelScope.launch {
        repository.delete(studySet)
    }
}



