package com.example.myapplication2.ui.listen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListenViewModel @Inject constructor() : ViewModel() {

    private var wordList: List<Word> = emptyList()
    private var currentIndex = 0
    private var currentStudySet: StudySet? = null

    private val _currentWord = MutableLiveData<Word?>()
    val currentWord: LiveData<Word?> = _currentWord


    fun setWords(words: List<Word>) {
        wordList = words
        currentIndex = 0
        if (wordList.isNotEmpty()) {
            _currentWord.value = wordList[currentIndex]
        }
    }

    fun setCurrentStudySet(set: StudySet) {
        currentStudySet = set
    }

    fun checkAnswer(answer: String): Boolean {
        val correct = _currentWord.value?.term?.trim()?.equals(answer.trim(), ignoreCase = true) == true
        if (correct) nextWord()
        return correct
    }

    fun skipWord() {
        nextWord()
    }

    private fun nextWord() {
        if (currentIndex + 1 < wordList.size) {
            currentIndex++
            _currentWord.value = wordList[currentIndex]
        } else {
            _currentWord.value = null // например, для завершения
        }
    }
}

