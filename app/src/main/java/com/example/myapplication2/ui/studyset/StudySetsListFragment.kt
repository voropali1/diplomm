package com.example.myapplication2.ui.studyset

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication2.R
import com.example.myapplication2.adapters.StudySetsAdapter
import com.example.myapplication2.databinding.FragmentHomeBinding
import com.example.myapplication2.databinding.FragmentStudySetsBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.ui.detail.StudySetDetailsFragment
import com.example.myapplication2.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StudySetsListFragment : Fragment() {

    private var _binding: FragmentStudySetsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudySetsListViewModel by viewModels()
    private lateinit var adapter: StudySetsAdapter
    private var fullList: List<StudySet> = emptyList() // Полный список сетов

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudySetsBinding.inflate(inflater, container, false)

        // Создаем адаптер с явным указанием типа для пустого списка
        adapter = StudySetsAdapter(
            itemsList = emptyList(),  // Указываем пустой список типа List<StudySet>
            callback = object : StudySetsAdapter.Callback {
                override fun onDeleteClicked(item: StudySet, position: Int) {
                    viewModel.deleteStudySet(item)
                }

                override fun onItemClicked(item: StudySet) {
                    val action = StudySetsListFragmentDirections
                        .actionToStudySetDetailsFragment(item)
                    findNavController().navigate(action)
                }
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@StudySetsListFragment.adapter
        }

        // Наблюдаем за всеми сетами
        viewModel.allStudySets.observe(viewLifecycleOwner) { studySets ->
            fullList = studySets ?: emptyList() // Сохраняем полный список
            updateFilteredList("") // Обновляем отображение

        }

        // Добавляем слушатель для поиска
        binding.searchDictationET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateFilteredList(s.toString()) // Фильтруем при изменении текста
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return binding.root
    }

    private fun updateFilteredList(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { it.name?.contains(query, ignoreCase = true) == true }
        }

        if (filteredList.isEmpty()) {
            binding.textPlaceholder.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.textPlaceholder.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            adapter.updateList(filteredList) // Обновляем адаптер с фильтрованным списком
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}







//@AndroidEntryPoint
//class StudySetsListFragment : Fragment(R.layout.fragment_study_sets) {
//
//    private var _binding: FragmentStudySetsBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: StudySetsListViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentStudySetsBinding.inflate(inflater, container, false)
//
//        // Наблюдение за изменениями списка сетов
//       // viewModel.allStudySets.observe(viewLifecycleOwner, Observer { studySets ->
//       //     // TODO: Подключить адаптер и обновить RecyclerView
//       // })
////
//       // binding.createStudySetBTN.setOnClickListener {
//       //     findNavController().navigate(R.id.action_studySetsFragment_to_createStudySetFragment)
//       // }
//
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

//package com.example.myapplication2.ui.studyset
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import com.example.myapplication2.databinding.FragmentNotificationsBinding
//import com.example.myapplication2.databinding.FragmentStudySetsBinding
//
///*
// TODO libs to study
// Connect Room
// Create Database for StudySet (DATABASE, DAO, ENTITY, REPOSITORY)
// Hilt for DI
// Kotlin Coroutines for Room, Retrofit (API requests)
// Kotlin Flow instead of LiveData
// Retrofit for translation api with Kotlin Coroutines (GSON for Json parsing)
// NEVER EVER GlobalScope for Kotlin coroutines
// */
//
///*
// TODO dev plan prephase
// 0) Read libraries
// 1) Setup HILT
// 2) Setup ROOM
// */
//
///* TODO dev plan phase 1
// Create Study Set (no auto translate) Separate Tab (think about it, maybe just a FloatingButton)
// StudySetsList Separate Tab
//    DetailStudySet
//        1) Delete, Edit, Study, FlashCards https://github.com/yuyakaido/CardStackView
//        2) List of all words (Delete word, Add a word to study set)
// Random words from any study set (Enter amount of words)
// ProfileTab: sign out
//*/
//
///* TODO dev plan phase 2
//  Translate API
//  Word Suggestion (asset many words starting with letter a, b) in Creation Flow
//  Scan document
//  Study flow (quiz, term-definition (ru-en), definition-term (en-ru), listening, speaking (SpeechToText))
//  SignIn via Firebase Google
//  Share StudySet
//  Study marked words
//  Onboarding
// */
//
//class StudySetsListFragment : Fragment() {
//
//    private var _binding: FragmentStudySetsBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val notificationsViewModel =
//            ViewModelProvider(this).get(StudySetsListViewModel::class.java)
//
//        _binding = FragmentStudySetsBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}