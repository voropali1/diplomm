package com.example.myapplication2.ui.speech

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SpeechStageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val wordList: List<Word> = savedStateHandle.get<ArrayList<Word>>("words") ?: emptyList()
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

    fun nextWord() {
        if (currentIndex + 1 < wordList.size) {
            currentIndex++
            _currentWord.value = wordList[currentIndex]
        } else {
            // Конец сета
        }
    }

    fun getExpectedTerm(): String? {
        return _currentWord.value?.translation
    }

    // Метод для установки текущего сета
    fun setCurrentStudySet(studySet: StudySet) {
        _currentStudySet.value = studySet
    }

    // Метод для получения текущего сета
    fun getCurrentStudySet(): StudySet? {
        return _currentStudySet.value
    }
}


