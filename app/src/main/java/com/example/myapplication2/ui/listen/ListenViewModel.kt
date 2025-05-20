package com.example.myapplication2.ui.listen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import com.example.myapplication2.repository.StudySetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListenViewModel @Inject constructor(private val repository: StudySetRepository) : ViewModel() {

    private var wordList: List<Word> = emptyList()
    private var currentIndex = 0

    private val _currentWord = MutableLiveData<Word?>()
    val currentWord: LiveData<Word?> = _currentWord
    private val _isCompleted = MutableLiveData<Boolean>()
    val isCompleted: LiveData<Boolean> = _isCompleted
    private val _currentStudySet = MutableLiveData<StudySet>()

    fun setWords(words: List<Word>) {
        wordList = words
        currentIndex = 0
        if (wordList.isNotEmpty()) {
            _currentWord.value = wordList[currentIndex]
        }
    }

    fun setCurrentStudySet(set: StudySet) {
        _currentStudySet.value = set
    }

    fun checkAnswer(answer: String): Boolean {
        val correct = _currentWord.value?.term?.trim()?.equals(answer.trim(), ignoreCase = true) == true
        return correct
    }

    fun nextWord() {
        if (currentIndex + 1 < wordList.size) {
            currentIndex++
            _currentWord.value = wordList[currentIndex]
        } else {
            viewModelScope.launch {
                onListenModeCompleted()
            }
        }
    }

    fun isLastWord(): Boolean {
        return currentIndex == wordList.size - 1
    }

    fun isLastWordTrue() {
        viewModelScope.launch {
            onListenModeCompleted()
        }
    }

    private suspend fun onListenModeCompleted() {
        val setId = _currentStudySet.value?.id ?: return
        repository.updateSetFinishedStatus(setId)
        _isCompleted.postValue(true)
    }
}

