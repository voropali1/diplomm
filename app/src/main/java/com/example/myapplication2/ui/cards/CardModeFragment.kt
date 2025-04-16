package com.example.myapplication2.ui.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication2.R
import com.example.myapplication2.adapters.CardsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardModeFragment : Fragment() {

    private val viewModel: CardModeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_card_mode, container, false)
        val viewPager = view.findViewById<ViewPager2>(R.id.cards_view_pager)

        val wordsString = arguments?.getString("wordsString")
        wordsString?.let {
            viewModel.loadWords(it)
        }

        viewModel.words.observe(viewLifecycleOwner) { wordList ->
            viewPager.adapter = CardsAdapter(wordList, requireContext())
        }

        return view
    }
}

