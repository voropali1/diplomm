package com.example.myapplication2.ui.quiz

import android.annotation.SuppressLint
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
class QuizViewModel @Inject constructor(private val repository: StudySetRepository,) : ViewModel() {

    private val _isCompleted = MutableLiveData<Boolean>()
    val isCompleted: LiveData<Boolean> get() = _isCompleted

    private val _currentStudySet = MutableLiveData<StudySet>()
    val currentStudySet: LiveData<StudySet> get() = _currentStudySet

    data class Question(
        val term: String,
        val correctAnswer: String,
        val options: List<String>
    )

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question> get() = _currentQuestion

    private var questionList: List<Question> = emptyList()
    private var currentIndex = 0

    fun setQuestionsFromWords(words: List<Word>) {

        questionList = words.map { word ->
            val incorrectAnswers = words
                .filter { it != word }
                .shuffled()
                .take(3)
                .map { it.translation }

            val allOptions = (incorrectAnswers + word.translation).shuffled()

            Question(
                term = word.term,
                correctAnswer = word.translation,
                options = allOptions
            )
        }.shuffled()

        currentIndex = 0
        loadNextQuestion()
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun loadNextQuestion() {
        if (currentIndex < questionList.size) {
            _currentQuestion.value = questionList[currentIndex]
            currentIndex++
        } else {
            viewModelScope.launch {
                modeCompleted()
            }
        }
    }

    fun checkAnswer(selected: String): Boolean {
        return selected == _currentQuestion.value?.correctAnswer
    }

    fun setCurrentStudySet(studySet: StudySet) {
        _currentStudySet.value = studySet
    }


    fun getCurrentStudySet(): StudySet? {
        return _currentStudySet.value
    }

    private suspend fun modeCompleted() {
        val setId = _currentStudySet.value?.id ?: return
        repository.updateSetFinishedStatus(setId)
        _isCompleted.postValue(true)
    }
}



