package com.example.myapplication2.ui.speech

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication2.databinding.FragmentSpeechStageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpeechStageFragment : Fragment() {

    private var _binding: FragmentSpeechStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SpeechStageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpeechStageBinding.inflate(inflater, container, false)

        // Показ перевода
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.translationTV.text = word.translation
        }

        // Кнопка "Говорить"
        binding.voiceBtn.setOnClickListener {
            // Тут ты подключишь распознавание речи
            // Для отладки можно вставить имитацию:
            val fakeRecognizedText = "sample" // ← Подставь сюда то, что распозналось
            val expected = viewModel.getExpectedTerm()

            if (fakeRecognizedText.equals(expected, ignoreCase = true)) {
                Toast.makeText(requireContext(), "Правильно!", Toast.LENGTH_SHORT).show()
                viewModel.nextWord()
            } else {
                Toast.makeText(requireContext(), "Неправильно. Ожидалось: $expected", Toast.LENGTH_SHORT).show()
            }
        }

        // Кнопка "Не могу говорить"
        binding.cannotSpeakBTN.setOnClickListener {
            viewModel.nextWord()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

