package com.example.myapplication2.ui.detail

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
class StudySetDetailsViewModel @Inject constructor(
    private val studySetRepository: StudySetRepository // Репозиторий для получения данных
) : ViewModel() {

    private val _studySet = MutableLiveData<StudySet>()
    val studySet: LiveData<StudySet> get() = _studySet

    // Загрузка данных для деталей StudySet
    fun loadStudySet(studySetId: Long) {
        viewModelScope.launch {
            // Получаем StudySet по ID
            val fetchedStudySet = studySetRepository.getStudySetById(studySetId)

            // Проверяем, что результат не равен null
            if (fetchedStudySet != null) {
                _studySet.postValue(fetchedStudySet) // Устанавливаем значение, если оно не null
            } else {
                // Можно обработать случай, когда объект не найден, например, отправить пустой объект или null в другое поле
                // _studySet.postValue(null) // Если хотите позволить null значения
                // Или вы можете отправить какое-то дефолтное значение
                // _studySet.postValue(StudySet(...)) // С дефолтными значениями
            }
        }
    }

    // Удаление StudySet
    fun deleteStudySet(studySet: StudySet) {
        viewModelScope.launch {
            // Удаление из репозитория
            studySetRepository.delete(studySet)
        }
    }
}
