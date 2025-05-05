package com.example.myapplication2.ui.studyset.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication2.R
import com.example.myapplication2.constants.BaseVariables
import com.example.myapplication2.model.Word
import com.example.myapplication2.databinding.FragmentCreateStudySetBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.utils.getTabletLayoutManager
import com.langamy.adapters.WordsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreateStudySetFragment : Fragment(R.layout.fragment_create_study_set) {

    private var _binding: FragmentCreateStudySetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateStudySetViewModel by viewModels()
    private lateinit var wordsAdapter: WordsAdapter

    private var languageFrom = "en"
    private var languageTo = "ru"
    private var isEditMode = false
    private var existingSet: StudySet? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateStudySetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Убедитесь, что меню будет инфлейтиться
        setHasOptionsMenu(true)

        wordsAdapter = WordsAdapter()
        binding.wordsRecyclerview.apply {
            layoutManager = requireContext().getTabletLayoutManager()
            adapter = wordsAdapter
        }

        setupLanguageSpinners()

        val receivedSet = arguments?.getSerializable("studySet") as? StudySet
        if (receivedSet != null) {
            isEditMode = true
            existingSet = receivedSet
            fillFieldsForEditing(receivedSet)
        }

        binding.addWordBtn.setOnClickListener {
            wordsAdapter.addWord(Word("", ""))
            binding.wordsRecyclerview.scrollToPosition(wordsAdapter.itemCount - 1)
        }
    }

    private fun saveStudySet() {
        val name = binding.titleEdittext.text.toString().trim()
        if (name.isEmpty() || wordsAdapter.itemCount == 0) {
            Toast.makeText(requireContext(), "Enter", Toast.LENGTH_SHORT).show()
            return
        }

        val wordsString = wordsAdapter.getWords().joinToString("\n") { "${it.term} - ${it.translation}" }

        // Если мы редактируем существующий сет, обновляем его
        val studySet = if (isEditMode) {
            // Обновляем существующий сет
            existingSet?.apply {
                this.name = name
                this.words = wordsString
                this.language_to = languageTo
                this.language_from = languageFrom
                this.amount_of_words = wordsAdapter.itemCount
            }
        } else {
            // Если это новый сет, создаём новый объект
            StudySet(
                creator = "User",
                name = name,
                words = wordsString,
                marked_words = existingSet?.marked_words ?: "",
                language_to = languageTo,
                language_from = languageFrom,
                amount_of_words = wordsAdapter.itemCount
            )
        }

        Log.d("CreateStudySetFragment", "Saving study set: $studySet")

        // Сохраняем в ViewModel
        lifecycleScope.launch {
            try {
                // Логируем начало операции
                Log.d("CreateStudySetFragment", "Starting to save study set. Edit mode: $isEditMode")

                if (isEditMode) {
                    // Логируем обновление существующего набора
                    Log.d("CreateStudySetFragment", "Updating study set")
                    viewModel.updateStudySet(studySet!!) // Обновление набора в базе
                } else {
                    // Логируем создание нового набора
                    Log.d("CreateStudySetFragment", "Adding new study set")
                    viewModel.addStudySet(studySet!!) // Создание нового набора
                }

                val action = CreateStudySetFragmentDirections.actionCreateStudySetFragmentToStudySetDetailsFragment(studySet!!)
                findNavController().navigate(action)

            } catch (e: Exception) {
                // Логируем ошибку
                Log.e("CreateStudySetFragment", "Error while saving study set", e)
            }
        }




    }


    private fun fillFieldsForEditing(set: StudySet) {
        binding.titleEdittext.setText(set.name)

        val words = set.words
            .split("\n")
            .mapNotNull {
                val parts = it.split(" - ")
                if (parts.size == 2) Word(parts[0], parts[1]) else null
            }

        wordsAdapter.setWords(words)

        setSpinnerByShort(binding.languageFormSpinner, set.language_from.toString())
        setSpinnerByShort(binding.languageToSpinner, set.language_to.toString())

        languageFrom = set.language_from.toString()
        languageTo = set.language_to.toString()
    }

    private fun setSpinnerByShort(spinner: Spinner, langShort: String) {
        val index = BaseVariables.LANGUAGES_SHORT.indexOf(langShort)
        if (index != -1) spinner.setSelection(index)
    }

    private fun setupLanguageSpinners() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, BaseVariables.LANGUAGES)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.languageFormSpinner.adapter = adapter
        binding.languageToSpinner.adapter = adapter

        val defaultFromIndex = BaseVariables.LANGUAGES.indexOf("English")
        val defaultToIndex = BaseVariables.LANGUAGES.indexOf("Czech")

        if (defaultFromIndex != -1) {
            binding.languageFormSpinner.setSelection(defaultFromIndex)
            languageFrom = BaseVariables.LANGUAGES_SHORT[defaultFromIndex]
        }
        if (defaultToIndex != -1) {
            binding.languageToSpinner.setSelection(defaultToIndex)
            languageTo = BaseVariables.LANGUAGES_SHORT[defaultToIndex]
        }

        binding.languageFormSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                languageFrom = BaseVariables.LANGUAGES_SHORT[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.languageToSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                languageTo = BaseVariables.LANGUAGES_SHORT[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_create_study_set, menu)
        menu.findItem(R.id.submitWords).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.submitWords -> {
                saveStudySet()
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









//@AndroidEntryPoint
//class CreateStudySetFragment : Fragment(R.layout.fragment_create_study_set) {
//
//    private var _binding: FragmentCreateStudySetBinding? = null
//    private val binding get() = _binding!!
//    private val viewModel: CreateStudySetViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentCreateStudySetBinding.inflate(inflater, container, false)
//
//        // Изменяем на ID из XML
//        binding.commitWordsBtn.setOnClickListener { // commitWordsBtn вместо saveStudySetBtn
//            val name = binding.titleEdittext.text.toString() // title_edittext вместо studySetNameEt
//            val words = binding.resultEt.text.toString() // resultEt вместо wordsEt
//            val languageFrom = binding.languageFormSpinner.selectedItem.toString() // Это Spinner!
//            val languageTo = binding.languageToSpinner.selectedItem.toString() // Это Spinner!
//
//            if (name.isNotEmpty() && words.isNotEmpty()) {
//                val studySet = StudySet(null, name, words, languageTo, languageFrom, words.split("\n").size)
//                viewModel.addStudySet(studySet)
//                findNavController().navigateUp()
//            }
//        }
//
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
//
//