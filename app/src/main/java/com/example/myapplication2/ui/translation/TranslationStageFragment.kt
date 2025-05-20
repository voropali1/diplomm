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
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class TranslationStageFragment : Fragment() {

    private var _binding: FragmentTermDefinitionStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TranslationStageViewModel by viewModels()

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
            currentLanguageTag = it.language_from
        }

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                currentLanguageTag?.let { langTag ->
                    val locale = Locale.forLanguageTag(langTag)
                    val result = tts.setLanguage(locale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(
                            requireContext(),
                            "Language $langTag is not supported",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "TTS initialization error", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.termTV.text = word.term
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

            val text = if (isCorrect) "Correct!" else "Incorrect!"
            val drawableRes = if (isCorrect) R.drawable.anim_happy else R.drawable.anim_drop

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


