package com.example.myapplication2.ui.learn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication2.base.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor() : ViewModel() {

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
        // Преобразуем список Word в список Question
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

    fun loadNextQuestion() {
        if (currentIndex < questionList.size) {
            _currentQuestion.value = questionList[currentIndex]
            currentIndex++
        } else {
            // Можно добавить обработку окончания викторины
            _currentQuestion.value = null
        }
    }

    fun checkAnswer(selected: String): Boolean {
        return selected == _currentQuestion.value?.correctAnswer
    }
}

