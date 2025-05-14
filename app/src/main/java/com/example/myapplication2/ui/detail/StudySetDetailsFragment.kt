package com.example.myapplication2.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication2.R
import com.example.myapplication2.adapters.SpecificStudySetAdapter
import com.example.myapplication2.databinding.ActivitySpecificStudysetBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import com.example.myapplication2.repository.StudySetRepository
import com.example.myapplication2.utils.getTabletLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudySetDetailsFragment : Fragment() {

    private var _binding: ActivitySpecificStudysetBinding? = null
    private val binding get() = _binding!!
    private var studySet: StudySet? = null
    private lateinit var wordsAdapter: SpecificStudySetAdapter
    private lateinit var allWords: List<Word>
    private var currentSet: StudySet? = null
    private var isStudyMarked: Boolean = false

    @Inject lateinit var repository: StudySetRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivitySpecificStudysetBinding.inflate(inflater, container, false)

        arguments?.let {
            studySet = it.getSerializable("studySet") as? StudySet
            currentSet = studySet
            studySet?.let { set ->
                Log.d("StudySetDetailsFragment", "Received words string: ${set.words}")

                val wordsList = parseWordsFromString(set.words)
                wordsAdapter = SpecificStudySetAdapter(set, repository, wordsList, lifecycleScope)
                loadMarkedFlags(wordsList, parseWordsFromString(set.marked_words))
                allWords = wordsList

                //wordsAdapter = SpecificStudySetAdapter(allWords, sharedPreferences)
                binding.wordsRecyclerview.layoutManager = requireContext().getTabletLayoutManager()
                binding.wordsRecyclerview.adapter = wordsAdapter
            }
        }

        binding.termBtn.setOnClickListener {
            val wordsToStudy = if (isStudyMarked) {
                allWords.filter { it.isMarked }
            } else {
                allWords
            }

            val word = wordsToStudy.firstOrNull()
            if (word != null) {
                val bundle = Bundle().apply {
                    putSerializable("words", ArrayList(wordsToStudy))
                    putSerializable("studySet", currentSet)
                    putBoolean("isFullSet", !isStudyMarked)
                }

                findNavController().navigate(R.id.definitionTermStageFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Нет слов в этом сете", Toast.LENGTH_SHORT).show()
            }
        }
        binding.translationBtn.setOnClickListener {
            val wordsToStudy = if (isStudyMarked) {
                allWords.filter { it.isMarked }
            } else {
                allWords
            }

            val word = wordsToStudy.firstOrNull()
            if (word != null) {
                val bundle = Bundle().apply {
                    putSerializable("words", ArrayList(wordsToStudy))
                    putSerializable("studySet", currentSet)
                    putBoolean("isFullSet", !isStudyMarked)
                }

                findNavController().navigate(R.id.definitionTranslationStageFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Нет слов в этом сете", Toast.LENGTH_SHORT).show()
            }
        }


        binding.cardsBtn.setOnClickListener {
            // Фильтрация слов в зависимости от флага isStudyMarked
            val wordsToStudy = if (isStudyMarked) {
                allWords.filter { it.isMarked }
            } else {
                allWords
            }

            // Преобразуем отфильтрованные слова в строку
            val wordsString = wordsToStudy.joinToString("\n") { "${it.term} - ${it.translation}" }

            // Передаем в Bundle
            val bundle = Bundle().apply {
                putString("wordsString", wordsString)
                putSerializable("studySet", currentSet)
                putBoolean("isFullSet", !isStudyMarked)
            }

            // Навигация в CardModeFragment
            findNavController().navigate(R.id.action_studySetDetailsFragment_to_cardModeFragment, bundle)
        }


        binding.quizBtn.setOnClickListener {
            // Фильтрация слов в зависимости от флага isStudyMarked
            val wordsToStudy = if (isStudyMarked) {
                allWords.filter { it.isMarked }
            } else {
                allWords
            }

            // Проверка, достаточно ли слов для начала викторины
            if (wordsToStudy.size < 4) {
                Toast.makeText(requireContext(), "At least 4 words are required to start the quiz", Toast.LENGTH_SHORT).show()
            } else {
                // Передаем отфильтрованные слова в Bundle
                val bundle = Bundle().apply {
                    putSerializable("words", ArrayList(wordsToStudy))
                    putSerializable("studySet", currentSet)
                    putBoolean("isFullSet", !isStudyMarked)
                }

                // Навигация в QuizFragment
                findNavController().navigate(R.id.quizFragment, bundle)
            }
        }



        binding.listenBtn.setOnClickListener {
            val wordsToStudy = if (isStudyMarked) {
                allWords.filter { it.isMarked }
            } else {
                allWords
            }

            val bundle = Bundle().apply {
                putSerializable("words", ArrayList(wordsToStudy)) // Передаем отфильтрованные слова
                putSerializable("studySet", currentSet)
                putBoolean("isFullSet", !isStudyMarked)
            }

            findNavController().navigate(R.id.listenFragment, bundle)
        }


        binding.speakBtn.setOnClickListener {
            val wordsToStudy = if (isStudyMarked) {
                allWords.filter { it.isMarked }
            } else {
                allWords
            }

            if (wordsToStudy.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putSerializable("words", ArrayList(wordsToStudy)) // Передаем отфильтрованные слова
                    putSerializable("studySet", currentSet) // Передаем StudySet
                    putBoolean("isFullSet", !isStudyMarked)
                }
                findNavController().navigate(R.id.speechFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Нет слов в этом сете", Toast.LENGTH_SHORT).show()
            }
        }

        binding.studyAllMBTN.setOnClickListener {
            isStudyMarked = false
            binding.studyAllMBTN.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
            binding.studyMarkedMBTN.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.studyAllMBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.studyMarkedMBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
            wordsAdapter.updateData(allWords)
        }

        binding.studyMarkedMBTN.setOnClickListener {
            isStudyMarked = true
            binding.studyMarkedMBTN.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
            binding.studyAllMBTN.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.studyMarkedMBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.studyAllMBTN.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))

            val markedWords = allWords.filter { it.isMarked }

            if (markedWords.isEmpty()) {
                Toast.makeText(requireContext(), "No marked words", Toast.LENGTH_SHORT).show()
            } else {
                wordsAdapter.updateData(markedWords)
            }
        }


        return binding.root
    }

    private fun parseWordsFromString(wordsString: String): List<Word> {
        return wordsString.lines().mapNotNull { line ->
            val parts = line.split(" - ")
            if (parts.size == 2) {
                val first = parts[0].trim()
                val second = parts[1].trim()
                Log.d("StudySetDetailsFragment", "Parsed word pair: $first - $second")
                Word(first, second)
            } else {
                Log.w("StudySetDetailsFragment", "Skipping invalid line: $line")
                null
            }
        }
    }

    private fun loadMarkedFlags(wordsList: List<Word>, markedWordsList: List<Word>) {
      val terms = markedWordsList.map { it.term }
      val translations =  markedWordsList.map { it.translation }
        for (word in wordsList) {
            val isMarked = word.term in terms && word.translation in translations
            word.isMarked = isMarked
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_specific_study_set, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_study_set -> {
                studySet?.let { set ->
                    val bundle = Bundle().apply {
                        putSerializable("studySet", studySet) // studySet уже есть у тебя
                    }
                    findNavController().navigate(R.id.create_study_set, bundle)
                }
                true
            }
            R.id.share -> {
                Toast.makeText(requireContext(), "Поделиться сетом", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

