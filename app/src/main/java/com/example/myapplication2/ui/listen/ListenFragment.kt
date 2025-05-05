package com.example.myapplication2.ui.listen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication2.model.Word
import com.example.myapplication2.databinding.FragmentAudioStageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListenFragment : Fragment() {

    private var _binding: FragmentAudioStageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioStageBinding.inflate(inflater, container, false)

        // Пример логики: получение слов
        val wordList = arguments?.getSerializable("words") as? ArrayList<Word>
        // viewModel.setWords(wordList) — если хочешь динамически

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
