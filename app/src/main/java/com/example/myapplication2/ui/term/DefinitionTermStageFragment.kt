package com.example.myapplication2.ui.term

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication2.databinding.FragmentDefinitionTermStageBinding
import com.example.myapplication2.databinding.FragmentTermDefinitionStageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefinitionTermStageFragment : Fragment() {

    private var _binding: FragmentTermDefinitionStageBinding? = null

    private val binding get() = _binding!!


    private val viewModel: DefinitionTermStageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermDefinitionStageBinding.inflate(inflater, container, false)

        // отображение слова
        viewModel.currentWord.observe(viewLifecycleOwner) { word ->
            binding.termTV.text = word.translation
        }

        // проверка по кнопке
        binding.checkAnswerBtn.setOnClickListener {
            val answer = binding.answerET.text.toString()
            viewModel.checkAnswer(answer)
            binding.answerET.text.clear()
        }

        // проверка по Enter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
