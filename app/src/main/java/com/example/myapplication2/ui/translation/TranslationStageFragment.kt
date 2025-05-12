package com.example.myapplication2.ui.translation

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication2.R
import com.example.myapplication2.databinding.FragmentTermDefinitionStageBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class TranslationStageFragment : Fragment() {

    private var _binding: FragmentTermDefinitionStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TranslationStageViewModel by viewModels()

    private lateinit var tts: TextToSpeech
    private var currentLanguageTag: String? = null
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermDefinitionStageBinding.inflate(inflater, container, false)

        // Получаем сет и язык
        val receivedSet = arguments?.getSerializable("studySet") as? StudySet
        val isFullSet = arguments?.getBoolean("isFullSet") ?: false
        receivedSet?.let {
            viewModel.setCurrentStudySet(it)
            currentLanguageTag = it.language_from // <-- язык, с которого идёт перевод
        }

        // Инициализируем TextToSpeech
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

        // Отображение текущего слова (term)
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.termTV.text = word.term
        }

        // Наблюдение за завершением режима
        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed && isFullSet) {
                // Проверяем, был ли сет уже завершён
                val studySet = viewModel.getCurrentStudySet() // Получаем текущий сет
                if (studySet != null && !studySet.isFinished) {
                    // Обновляем статус завершённости сета
                    profileViewModel.updateCompletedSets(true)
                    // Обновляем флаг завершённости в объекте StudySet
                    studySet.isFinished = true
                }
                findNavController().popBackStack()
            }
        }

        // Озвучка по кнопке
        binding.volumeUpIB.setOnClickListener {
            val wordToSpeak = binding.termTV.text.toString()
            speakWord(wordToSpeak)
            Toast.makeText(requireContext(), "Используется язык: $currentLanguageTag", Toast.LENGTH_SHORT).show()
        }

        // Проверка по кнопке
        binding.checkAnswerBtn.setOnClickListener {
            val answer = binding.answerET.text.toString()
            val isCorrect = viewModel.checkAnswer(answer)

            val text = if (isCorrect) "Correct!" else "Incorrect!"
            val drawableRes = if (isCorrect) R.drawable.anim_happy else R.drawable.anim_drop

            binding.emojiTextTV.text = text
            binding.emojiIV.setImageResource(drawableRes)
            val animation = binding.emojiIV.drawable as? Animatable

            binding.emojiContainer.visibility = View.VISIBLE
            animation?.start()

            // скрыть emoji через 1000 мс
            binding.emojiContainer.postDelayed({
                _binding?.emojiContainer?.visibility = View.GONE
            }, 1000)

            if (isCorrect) {
                binding.checkAnswerBtn.isEnabled = false

                view?.postDelayed({
                    _binding?.let { binding ->
                        viewModel.nextWord()
                        binding.checkAnswerBtn.isEnabled = true
                        binding.answerET.text.clear()
                    }
                }, 800)
            }
        }

        // Проверка по Enter
        binding.answerET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val answer = binding.answerET.text.toString()
                viewModel.checkAnswer(answer)
                binding.answerET.text.clear()
                true
            } else false
        }

        return binding.root
    }

    private fun speakWord(word: String) {
        if (word.isNotBlank()) {
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.root?.removeCallbacks(null)
        _binding = null
        tts.stop()
        tts.shutdown()
    }
}


