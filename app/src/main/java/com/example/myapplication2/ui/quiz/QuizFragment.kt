package com.example.myapplication2.ui.quiz

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication2.model.Word
import com.example.myapplication2.databinding.FragmentQuizStageBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class QuizFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentQuizStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()
    private var wordList: List<Word> = emptyList()

    private var tts: TextToSpeech? = null
    private var language: Locale = Locale.US // Начальный язык для озвучки, можно динамически менять

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация TextToSpeech
        tts = TextToSpeech(requireContext(), this)

        arguments?.let {
            wordList = (it.getSerializable("words") as? ArrayList<Word>) ?: emptyList()
        }

        if (wordList.isNotEmpty()) {
            viewModel.setQuestionsFromWords(wordList)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Устанавливаем язык для TTS
            tts?.language = language
            tts?.setSpeechRate(0.7f) // Устанавливаем скорость речи, чтобы была медленной
        } else {
            Toast.makeText(requireContext(), "Ошибка инициализации TTS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun speakTerm(term: String) {
        speakText(term)
    }

    private fun speakAnswer(answer: String) {
        speakText(answer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizStageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Наблюдаем за текущим вопросом
        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            binding.termTV.text = question.term
            speakTerm(question.term) // Озвучиваем текущий термин

            // Перемешиваем варианты ответа
            val options = question.options.shuffled()
            binding.answer1.text = options[0]
            binding.answer2.text = options[1]
            binding.answer3.text = options[2]
            binding.answer4.text = options[3]
        }

        // Логика для кнопки озвучки термина
        binding.volumeUpIB.setOnClickListener {
            val currentTerm = viewModel.currentQuestion.value?.term
            currentTerm?.let { term ->
                speakTerm(term) // Озвучиваем термин
            }
        }

        // Слушатели для каждого из вариантов ответа
        val clickListener = View.OnClickListener { v ->
            val selected = (v as TextView).text.toString()
            val isCorrect = viewModel.checkAnswer(selected)

            // Показываем правильность ответа
            Toast.makeText(
                requireContext(),
                if (isCorrect) "✅ Правильно!" else "❌ Неправильно",
                Toast.LENGTH_SHORT
            ).show()

            // Добавляем задержку перед переходом к следующему вопросу
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.loadNextQuestion()
            }, 1000) // Задержка 1 секунда
        }

        // Привязываем слушатели к каждому из ответов
        binding.answer1.setOnClickListener(clickListener)
        binding.answer2.setOnClickListener(clickListener)
        binding.answer3.setOnClickListener(clickListener)
        binding.answer4.setOnClickListener(clickListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tts?.shutdown() // Завершаем работу с TTS
    }
}






