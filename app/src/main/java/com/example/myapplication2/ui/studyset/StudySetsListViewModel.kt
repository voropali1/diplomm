package com.example.myapplication2.ui.studyset

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
class StudySetsListViewModel @Inject constructor(
    private val repository: StudySetRepository
) : ViewModel() {

    // LiveData для хранения списка сетов
    private val _allStudySets = MutableLiveData<List<StudySet>>()
    val allStudySets: LiveData<List<StudySet>> get() = _allStudySets

    init {
        // Загружаем данные из репозитория в LiveData
        loadStudySets()
    }

    private fun loadStudySets() {
        // Предполагается, что репозиторий предоставляет LiveData, которую можно наблюдать
        repository.allStudySets.observeForever { sets ->
            _allStudySets.value = sets
            Log.d("StudySetsListViewModel", "Обновлено: $sets") // ✅ Проверяем обновление LiveData
        }
    }

    // Метод для удаления StudySet
    fun deleteStudySet(studySet: StudySet) = viewModelScope.launch {
        repository.delete(studySet)
    }
}


    //val allStudySets: LiveData<List<StudySet>> = repository.allStudySets
//
    //fun deleteStudySet(studySet: StudySet) = viewModelScope.launch {
    //    repository.delete(studySet)
    //}

