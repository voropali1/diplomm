package com.example.myapplication2.ui.speech

import android.app.Activity
import android.content.Intent
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
import com.example.myapplication2.databinding.FragmentSpeechStageBinding
import com.example.myapplication2.model.StudySet
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
                    if (recognizedText.equals(expected, ignoreCase = true)) {
                        Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show()
                        viewModel.nextWord()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Incorrect. Expected: $expected\nYou said: $recognizedText",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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
            binding.translationTV.text = word.translation
        }

        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                findNavController().popBackStack()
            }
        }

        binding.voiceBtn.setOnClickListener {
            val currentLanguage = viewModel.currentStudySet.value?.language_to
            Toast.makeText(
                requireContext(),
                "Current language: $currentLanguage",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("LANG_CHECK", "Lang from study set: ${currentLanguage}")



            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the word...")
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

