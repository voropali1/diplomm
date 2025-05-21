package com.example.myapplication2.ui.studyset.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.repository.StudySetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateStudySetViewModel @Inject constructor(private val repository: StudySetRepository) : ViewModel() {

    private val _studySetLiveData = MutableLiveData<StudySet>()
    val studySetLiveData: LiveData<StudySet> = _studySetLiveData

    val studySetsLiveData: LiveData<List<StudySet>> = repository.allStudySets

    suspend fun addStudySet(studySet: StudySet): StudySet {
        val newId = repository.insert(studySet)
        studySet.id = newId.toInt()
        loadStudySet(studySet.id)
        return studySet
    }

    suspend fun updateStudySet(studySet: StudySet) {
        repository.update(studySet)
    }

    fun loadStudySet(studySetId: Int) {
        viewModelScope.launch {
            try {
                val studySet = repository.getStudySetById(studySetId.toLong())
                _studySetLiveData.postValue(studySet!!)
            } catch (e: Exception) {
                Log.e("CreateStudySetViewModel", "Error loading study set", e)
            }
        }
    }
}



