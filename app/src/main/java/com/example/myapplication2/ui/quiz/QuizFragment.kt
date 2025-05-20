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
import androidx.navigation.fragment.findNavController
import com.example.myapplication2.databinding.FragmentQuizStageBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class QuizFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentQuizStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()
    private var wordList: List<Word> = emptyList()

    private var tts: TextToSpeech? = null
    private var language: Locale = Locale.US

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(requireContext(), this)
        val receivedSet = arguments?.getSerializable("studySet") as? StudySet

        arguments?.let {
            wordList = (it.getSerializable("words") as? ArrayList<Word>) ?: emptyList()
        }

        receivedSet?.let {
            viewModel.setCurrentStudySet(it)
        }

        if (wordList.isNotEmpty()) {
            viewModel.setQuestionsFromWords(wordList)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = language
            tts?.setSpeechRate(0.7f)
        } else {
            Toast.makeText(requireContext(), "Failed to initialize Text-to-Speech", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun speakTerm(term: String) {
        speakText(term)
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

        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            binding.termTV.text = question.term
            //speakTerm(question.term)

            val options = question.options.shuffled()
            binding.answer1.text = options[0]
            binding.answer2.text = options[1]
            binding.answer3.text = options[2]
            binding.answer4.text = options[3]
        }

        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                findNavController().navigateUp()
            }
        }

        binding.volumeUpIB.setOnClickListener {
            val currentTerm = viewModel.currentQuestion.value?.term
            currentTerm?.let { term ->
                speakTerm(term)
            }
        }

        val clickListener = View.OnClickListener { v ->
            val selected = (v as TextView).text.toString()
            val isCorrect = viewModel.checkAnswer(selected)

            Toast.makeText(
                requireContext(),
                if (isCorrect) "✅ Correct!" else "❌ Incorrect",
                Toast.LENGTH_SHORT
            ).show()


            if (isCorrect) {
                Handler(Looper.getMainLooper()).postDelayed({ viewModel.loadNextQuestion() }, 1000)
            }
        }

        binding.answer1.setOnClickListener(clickListener)
        binding.answer2.setOnClickListener(clickListener)
        binding.answer3.setOnClickListener(clickListener)
        binding.answer4.setOnClickListener(clickListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tts?.shutdown()
    }
}