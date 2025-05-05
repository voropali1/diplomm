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

    // Для списка всех сетов
    val studySetsLiveData: LiveData<List<StudySet>> = repository.allStudySets

    // Метод для добавления нового набора
    fun addStudySet(studySet: StudySet) = viewModelScope.launch {
        Log.d("CreateStudySetViewModel", "Добавляем сет: $studySet")
        val newId = repository.insert(studySet) // Получаем ID после вставки
        studySet.id = newId.toInt() // Обновляем ID в объекте studySet
        loadStudySet(studySet.id) // Загружаем новый сет с актуальным ID
    }

    // Метод для обновления существующего набора
    fun updateStudySet(studySet: StudySet) = viewModelScope.launch {
        Log.d("CreateStudySetViewModel", "Обновляем сет: $studySet")
        repository.update(studySet)
    }

    // Метод для загрузки данных с помощью ID
    fun loadStudySet(studySetId: Int) {
        viewModelScope.launch {
            try {
                val studySet = repository.getStudySetById(studySetId.toLong())  // Получаем набор данных по ID из репозитория
                _studySetLiveData.postValue(studySet!!)  // Обновляем LiveData
            } catch (e: Exception) {
                Log.e("CreateStudySetViewModel", "Error loading study set", e)
            }
        }
    }
}






//@HiltViewModel
//class CreateStudySetViewModel @Inject constructor(private val repository: StudySetRepository) : ViewModel() {
//
//    fun addStudySet(studySet: StudySet) = viewModelScope.launch {
//        repository.insert(studySet)
//    }
//}
