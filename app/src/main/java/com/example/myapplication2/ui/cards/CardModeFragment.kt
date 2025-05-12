package com.example.myapplication2.ui.cards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication2.adapters.CardsAdapter
import com.example.myapplication2.databinding.ActivityCardModeBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardModeFragment : Fragment() {

    private var _binding: ActivityCardModeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CardModeViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCardModeBinding.inflate(inflater, container, false)
        val receivedSet = arguments?.getSerializable("studySet") as? StudySet
        val isFullSet = arguments?.getBoolean("isFullSet") ?: false

        receivedSet?.let {
            viewModel.setCurrentStudySet(it)
        }


        binding.cardsViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val allItems = binding.cardsViewPager.adapter?.itemCount ?: return
                    binding.cardAmountTV.text = "${position + 1}/$allItems"

                    if (position == allItems - 1) {
                        // Вызов метода onCardsModeCompleted()
                        Log.d("CardModeFragment", "Last card reached, calling onCardsModeCompleted")
                        viewModel.onCardsModeCompleted()
                    }
                }
            }
        )

        viewModel.isCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed && isFullSet) {
                // Проверяем, был ли сет уже завершён

                val studySet = viewModel.getCurrentStudySet() // Получаем текущий сет

                Log.d("CardModeFragment", "isFullSet: $isFullSet, isFinished before: ${studySet?.isFinished}")
                if (studySet != null && !studySet.isFinished) {
                    // Обновляем статус завершённости сета
                    profileViewModel.updateCompletedSets(studySet) // Обновляем статус завершённости сета
                    studySet.isFinished = true // Обновляем флаг завершённости сета
                    // Здесь можно обновить флаг completed в вашем объекте StudySet, если это нужно
                    // Например, можно обновить базу данных или репозиторий
                    viewModel.onCardsModeCompleted()
                    Log.d("CardModeFragment", "isFinished after: ${studySet.isFinished}")
                    // Обновление в репозитории
                }
            }
        }


        val wordsString = arguments?.getString("wordsString")
        wordsString?.let {
            viewModel.loadWords(it)
        }

        val studySet = arguments?.getSerializable("studySet") as? StudySet
        val languageFrom = studySet?.language_from ?: "en"
        val languageTo = studySet?.language_to ?: "en"

        viewModel.words.observe(viewLifecycleOwner) { wordList ->
            binding.cardsViewPager.adapter =
                CardsAdapter(wordList, requireContext(), languageFrom, languageTo)
        }


        return binding.root
    }
}

