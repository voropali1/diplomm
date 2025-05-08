package com.example.myapplication2.ui.cards

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CardModeViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _words = MutableLiveData<List<Word>>()
    val words: LiveData<List<Word>> = _words
    private val _currentStudySet = MutableLiveData<StudySet>()
    val currentStudySet: LiveData<StudySet> get() = _currentStudySet

    fun loadWords(rawString: String) {
        val parsedWords = rawString.lines().mapNotNull { line ->
            val parts = line.split(" - ")
            if (parts.size == 2) {
                val word = Word(parts[0].trim(), parts[1].trim())
                word.isMarked = sharedPreferences.getBoolean("word_marked_${_words.value?.indexOf(word) ?: 0}", false)
                word
            } else null
        }
        _words.value = parsedWords
    }

    fun setCurrentStudySet(studySet: StudySet) {
        _currentStudySet.value = studySet
    }

    // Метод для получения текущего сета
    fun getCurrentStudySet(): StudySet? {
        return _currentStudySet.value
    }
}
