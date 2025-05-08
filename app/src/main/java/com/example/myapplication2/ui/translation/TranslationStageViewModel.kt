package com.example.myapplication2.ui.translation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TranslationStageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val wordList: List<Word> = (savedStateHandle.get<ArrayList<Word>>("words") ?: arrayListOf())
    private var currentIndex = 0
    private val _currentStudySet = MutableLiveData<StudySet>()
    val currentStudySet: LiveData<StudySet> get() = _currentStudySet

    private val _currentWord = MutableLiveData<Word>()
    val currentWord: LiveData<Word> = _currentWord

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
            // Можно показать "Конец"
        }
    }

    fun checkTranslationAnswer(answer: String) {
        val correct = currentWord.value?.translation.equals(answer.trim(), ignoreCase = true)
        if (correct) {
            nextWord()
        }
    }

    fun setCurrentStudySet(studySet: StudySet) {
        _currentStudySet.value = studySet
    }

    // Метод для получения текущего сета
    fun getCurrentStudySet(): StudySet? {
        return _currentStudySet.value
    }

}

