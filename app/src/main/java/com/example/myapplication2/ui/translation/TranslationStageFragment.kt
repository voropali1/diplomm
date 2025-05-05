package com.example.myapplication2.ui.translation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication2.databinding.FragmentTermDefinitionStageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TranslationStageFragment : Fragment() {

    private var _binding: FragmentTermDefinitionStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TranslationStageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermDefinitionStageBinding.inflate(inflater, container, false)

        // отображение слова (теперь TERM - английское слово)
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.termTV.text = word.term
        }

        // проверка по кнопке
        binding.checkAnswerBtn.setOnClickListener {
            val answer = binding.answerET.text.toString()
            viewModel.checkTranslationAnswer(answer)
            binding.answerET.text.clear()
        }

        // проверка по Enter
        binding.answerET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val answer = binding.answerET.text.toString()
                viewModel.checkTranslationAnswer(answer)
                binding.answerET.text.clear()
                true
            } else false
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
