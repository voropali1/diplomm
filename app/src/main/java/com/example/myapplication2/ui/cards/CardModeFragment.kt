package com.example.myapplication2.ui.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication2.adapters.CardsAdapter
import com.example.myapplication2.databinding.ActivityCardModeBinding
import com.example.myapplication2.model.StudySet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardModeFragment : Fragment() {

    private var _binding: ActivityCardModeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CardModeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCardModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedSet = arguments?.getSerializable("studySet") as? StudySet

        receivedSet?.let {
            viewModel.setCurrentStudySet(it)
        }

        binding.cardsViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val allItems = binding.cardsViewPager.adapter?.itemCount ?: return
                    binding.cardAmountTV.text = "${position + 1}/$allItems"
                }
            }
        )

        val wordsString = arguments?.getString("wordsString")
        wordsString?.let {
            viewModel.loadWords(it)
        }

        val studySet = arguments?.getSerializable("studySet") as? StudySet
        val languageFrom = studySet?.language_from ?: "en"
        val languageTo = studySet?.language_to ?: "en"

        viewModel.words.observe(viewLifecycleOwner) { wordList ->
            binding.cardsViewPager.adapter = CardsAdapter(wordList, requireContext(), languageFrom, languageTo)
        }
    }
}

