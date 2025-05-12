package com.example.myapplication2.ui.listen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
    val currentStudySet: LiveData<StudySet> get() = _currentStudySet


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
        if (correct) nextWord()
        return correct
    }

    fun skipWord() {
        nextWord()
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
            onListenModeCompleted()// Вызов внутри корутины
        }
    }




    fun getCurrentStudySet(): StudySet? {
        return _currentStudySet.value
    }

    fun updateSetFinishedStatus(setId: Int) {
        viewModelScope.launch {
            try {
                // Обновляем статус сета в репозитории
                repository.updateSetFinishedStatus(setId)
                // После успешного обновления можно установить флаг завершенности
                _isCompleted.postValue(true)
            } catch (e: Exception) {
                // Логирование ошибки, если нужно
                Log.e("ListenViewModel", "Ошибка обновления статуса сета", e)
            }
        }
    }



    private suspend fun onListenModeCompleted() {
        Log.d("ListenViewModel", "currentStudySet: ${_currentStudySet.value}")
        val setId = _currentStudySet.value?.id
        if (setId == null) {
            Log.d("ListenViewModel", "setId is null, returning...")
            return
        }
        Log.d("ListenViewModel", "Is last word: , id: $setId")
        repository.updateSetFinishedStatus(setId)
        _isCompleted.postValue(true)
    }


}

