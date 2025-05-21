package com.example.myapplication2.ui.speech

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
class SpeechStageViewModel @Inject constructor(
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

    fun setCurrentStudySet(studySet: StudySet) {
        _currentStudySet.value = studySet
    }


    fun getExpectedTerm(): String {
        return _currentWord.value?.term ?: ""
    }


    fun nextWord() {
        if (currentIndex + 1 < wordList.size) {
            currentIndex++
            _currentWord.value = wordList[currentIndex]
        } else {
            viewModelScope.launch {
                modeCompleted()
            }
        }
    }
    fun getCurrentStudySet(): StudySet? {
        return _currentStudySet.value
    }

    fun isLastWord(): Boolean {
        return currentIndex == wordList.size - 1
    }

    fun isLastWordTrue() {
        viewModelScope.launch {
            modeCompleted()
        }
    }

    private suspend fun modeCompleted() {
        val setId = _currentStudySet.value?.id ?: return
        repository.updateSetFinishedStatus(setId)
        _isCompleted.postValue(true)
    }
}


