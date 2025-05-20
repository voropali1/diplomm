package com.example.myapplication2.ui.term

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication2.databinding.FragmentTermDefinitionStageBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.utils.getEmoji
import com.example.myapplication2.utils.getEmojiMessage
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class TermStageFragment : Fragment() {

    private var _binding: FragmentTermDefinitionStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TermStageViewModel by viewModels()

    private lateinit var tts: TextToSpeech
    private var currentLanguageTag: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermDefinitionStageBinding.inflate(inflater, container, false)

        val receivedSet = arguments?.getSerializable("studySet") as? StudySet
        receivedSet?.let {
            viewModel.setCurrentStudySet(it)
            currentLanguageTag = it.language_to
        }

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                currentLanguageTag?.let { langTag ->
                    val locale = Locale.forLanguageTag(langTag)
                    val result = tts.setLanguage(locale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        showToast("Language $langTag is not supported")
                    }
                }
            } else {
                showToast("TTS initialization error")
            }
        }

        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.termTV.text = word.translation
        }

        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                findNavController().popBackStack()
            }
        }

        binding.volumeUpIB.setOnClickListener {
            val wordToSpeak = binding.termTV.text.toString()
            speakWord(wordToSpeak)

        }

        binding.checkAnswerBtn.setOnClickListener {
            val answer = binding.answerET.text.toString()
            val isCorrect = viewModel.checkAnswer(answer)

            val text = getEmojiMessage(isCorrect)
            val drawableRes = getEmoji(isCorrect)

            binding.emojiTextTV.text = text
            binding.emojiIV.setImageResource(drawableRes)
            val animation = binding.emojiIV.drawable as? Animatable

            binding.emojiContainer.visibility = View.VISIBLE
            animation?.start()

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

        return binding.root
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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


