package com.example.myapplication2.ui.listen

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication2.model.Word
import com.example.myapplication2.databinding.FragmentAudioStageBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ListenFragment : Fragment() {

    private var _binding: FragmentAudioStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListenViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var tts: TextToSpeech
    private var currentLanguageTag: String? = null
    private var com = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioStageBinding.inflate(inflater, container, false)

        val receivedSet = arguments?.getSerializable("studySet") as? StudySet
        val wordList = arguments?.getSerializable("words") as? ArrayList<Word>
        val isFullSet = arguments?.getBoolean("isFullSet") ?: false

        if (receivedSet == null) {
            Log.d("ListenFragment", "NUUUULLLL")
        }
        receivedSet?.let {
            currentLanguageTag = it.language_from
            viewModel.setCurrentStudySet(it)
        }

        wordList?.let { viewModel.setWords(it) }

        // Инициализация TTS
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                currentLanguageTag?.let { langTag ->
                    val locale = Locale.forLanguageTag(langTag)
                    val result = tts.setLanguage(locale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(requireContext(), "Язык $langTag не поддерживается", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Ошибка инициализации TTS", Toast.LENGTH_SHORT).show()
            }
        }

        // Когда появляется новое слово
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            if (word != null) {
                speakWord(word.term, normal = true)
            }
            if (word != null) {
                binding.translationHint.text = word.translation
            }
        }




        // Нормальная озвучка
        binding.speakTermIB.setOnClickListener {
            viewModel.currentWord.value?.term?.let { speakWord(it, normal = true) }
        }

        // Замедленная озвучка
        binding.speakTermSlowIB.setOnClickListener {
            viewModel.currentWord.value?.term?.let { speakWord(it, normal = false) }
        }

        // Проверка
        binding.nextPageBTN.setOnClickListener {
            // Получаем ответ из поля ввода
            val answer = binding.definitionAudiostageET.text.toString()

            // Проверяем правильность ответа
            val isCorrect = viewModel.checkAnswer(answer)

            // Получаем текущий сет и флаг завершенности
            val studySet = viewModel.getCurrentStudySet()
            Log.d("ListenFragment", "isCompleted: ${viewModel.isCompleted.value}, isFullSet: $isFullSet, studySet.isFinished: ${studySet?.isFinished}")

            // Логируем, является ли текущее слово последним
            val isLastWord = viewModel.isLastWord()
            Log.d("ListenFragment", "Is last word: $isLastWord")

            // Очистка текстового поля после проверки
            binding.definitionAudiostageET.text.clear()
        }


        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            Log.d("ListenFragment", "isCompleted: $completed, isFullSet: $isFullSet")
            if (completed && isFullSet) {
                // Проверяем, был ли сет уже завершён
                val studySet = viewModel.getCurrentStudySet() // Получаем текущий сет
                Log.d("ListenFragment", "studySet: ${studySet?.id}, isFinished: ${studySet?.isFinished}")
                if (studySet != null && !studySet.isFinished) {
                    // Обновляем статус завершённости сета
                    profileViewModel.updateCompletedSets(studySet)
                    // Обновляем флаг завершенности сета
                    studySet.isFinished = true
                    Log.d("ListenFragment", "studySet is now finished: ${studySet.isFinished}")
                }
            }
        }


        // Пропустить (не могу прослушать)
        binding.cannotSpeakBTN.setOnClickListener {
            // Устанавливаем флаг завершенности сета, если это последнее слово
            if (viewModel.isLastWord()) {
                viewModel.isLastWordTrue()
            }
            viewModel.nextWord()
        }

        return binding.root
    }

    private fun speakWord(word: String, normal: Boolean) {
        if (word.isNotBlank()) {
            tts.setSpeechRate(if (normal) 1.0f else 0.5f)
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tts.stop()
        tts.shutdown()
    }
}

