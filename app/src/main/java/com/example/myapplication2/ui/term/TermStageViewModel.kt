package com.example.myapplication2.ui.term

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import com.example.myapplication2.repository.StudySetRepository
import com.example.myapplication2.ui.profile.ProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermStageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StudySetRepository,
) : ViewModel() {

    private val wordList: List<Word> = (savedStateHandle.get<ArrayList<Word>>("words") ?: arrayListOf())
    private var currentIndex = 0

    private val _currentStudySet = MutableLiveData<StudySet>()
    val currentStudySet: LiveData<StudySet> get() = _currentStudySet

    private val _currentWord = MutableLiveData<Word>()
    val currentWord: LiveData<Word> get() = _currentWord

    private val _isCompleted = MutableLiveData<Boolean>()
    val isCompleted: LiveData<Boolean> get() = _isCompleted

    init {
        if (wordList.isNotEmpty()) {
            _currentWord.value = wordList[currentIndex]
        }
    }

    fun checkAnswer(answer: String) {
        val correct = currentWord.value?.term.equals(answer.trim(), ignoreCase = true)
        if (correct) {
            nextWord()
        }
    }

    private fun nextWord() {
        if (currentIndex + 1 < wordList.size) {
            currentIndex++
            _currentWord.value = wordList[currentIndex]
        } else {
            // Завершаем обучение — вызываем suspend-функцию в корутине
            viewModelScope.launch {
                onCardsModeCompleted()
            }
        }
    }

    fun setCurrentStudySet(studySet: StudySet) {
        _currentStudySet.value = studySet
    }

    // Добавляем метод для получения текущего StudySet
    fun getCurrentStudySet(): StudySet? {
        return _currentStudySet.value
    }

    private suspend fun onCardsModeCompleted() {
        val setId = _currentStudySet.value?.id ?: return
        repository.updateSetFinishedStatus(setId)
        _isCompleted.postValue(true)
    }
}





