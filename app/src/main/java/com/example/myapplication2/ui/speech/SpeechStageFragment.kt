package com.example.myapplication2.ui.speech

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication2.constants.BaseVariables
import com.example.myapplication2.databinding.FragmentSpeechStageBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.utils.getEmoji
import com.example.myapplication2.utils.getEmojiMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpeechStageFragment : Fragment() {

    private var _binding: FragmentSpeechStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SpeechStageViewModel by viewModels()

    private val speechRecognizerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val recognizedText = matches?.firstOrNull()
                val expected = viewModel.getExpectedTerm()

                if (recognizedText != null) {
                    val isCorrect = recognizedText.equals(expected, ignoreCase = true)

                    showEmojiResult(isCorrect)

                    if (isCorrect) {
                        binding.voiceBtn.isEnabled = false
                        binding.cannotSpeakBTN.isEnabled = false

                        view?.postDelayed({
                            _binding?.let {
                                viewModel.nextWord()
                                it.voiceBtn.isEnabled = true
                                it.cannotSpeakBTN.isEnabled = true
                            }
                        }, 1500)
                    }
                }

            }
        }

    private fun showEmojiResult(isCorrect: Boolean) {
        val text = getEmojiMessage(isCorrect)
        val drawableRes = getEmoji(isCorrect)

        binding.emojiTextTV.text = text
        binding.emojiIV.setImageResource(drawableRes)
        val animation = binding.emojiIV.drawable as? Animatable

        binding.emojiContainer.visibility = View.VISIBLE
        animation?.start()

        binding.emojiContainer.postDelayed({
            binding.emojiContainer.visibility = View.GONE
        }, 1000)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeechStageBinding.inflate(inflater, container, false)

        val receivedSet = arguments?.getSerializable("studySet") as? StudySet
        receivedSet?.let {
            viewModel.setCurrentStudySet(it)
        }

        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.translationTV.text = word.term
        }

        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                findNavController().popBackStack()
            }
        }

        binding.voiceBtn.setOnClickListener {
            val currentLanguage = viewModel.currentStudySet.value?.language_from
            //Toast.makeText(
            //    requireContext(),
            //    "Current language: $currentLanguage",
            //    Toast.LENGTH_SHORT
            //).show()
            Log.d("LANG_CHECK", "Lang from study set: ${currentLanguage}")
            val language = BaseVariables.LANGUAGES_BCP47[currentLanguage]

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the word...")
                Log.d("LANG_CHECK", "Lang from study set: ${currentLanguage}")
            }

            try {
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Speech recognition is not available",
                    Toast.LENGTH_SHORT
                ).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

