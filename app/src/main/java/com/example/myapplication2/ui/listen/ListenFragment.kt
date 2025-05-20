package com.example.myapplication2.ui.listen

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
import com.example.myapplication2.databinding.FragmentAudioStageBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import com.example.myapplication2.utils.getEmoji
import com.example.myapplication2.utils.getEmojiMessage
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ListenFragment : Fragment() {

    private var _binding: FragmentAudioStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListenViewModel by viewModels()
    private var ttsInitialized = false


    private lateinit var tts: TextToSpeech
    private var currentLanguageTag: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioStageBinding.inflate(inflater, container, false)

        val receivedSet = arguments?.getSerializable("studySet") as? StudySet
        val wordList = arguments?.getSerializable("words") as? ArrayList<Word>

        receivedSet?.let {
            currentLanguageTag = it.language_from
            viewModel.setCurrentStudySet(it)
        }

        wordList?.let { viewModel.setWords(it) }

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                currentLanguageTag?.let { langTag ->
                    val locale = Locale.forLanguageTag(langTag)
                    val result = tts.setLanguage(locale)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(requireContext(), "Language $langTag is not supported", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "TTS initialization failed", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.currentWord.observe(viewLifecycleOwner) { word ->

            if (word != null) {
                binding.translationHint.text = word.translation
            }
        }

        binding.speakTermIB.setOnClickListener {
            viewModel.currentWord.value?.term?.let { speakWord(it, normal = true) }
        }

        binding.speakTermSlowIB.setOnClickListener {
            viewModel.currentWord.value?.term?.let { speakWord(it, normal = false) }
        }

        binding.nextPageBTN.setOnClickListener {
            val answer = binding.definitionAudiostageET.text.toString()
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
                viewModel.nextWord()
            }, 1000)

            binding.definitionAudiostageET.text.clear()
        }

        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                findNavController().navigateUp()
            }
        }

        binding.cannotSpeakBTN.setOnClickListener {
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

