package com.example.myapplication2.ui.studyset.create

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.*
import com.example.myapplication2.R
import com.example.myapplication2.constants.BaseVariables
import com.example.myapplication2.model.Word
import com.example.myapplication2.databinding.FragmentCreateStudySetBinding
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.utils.getTabletLayoutManager
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.myapplication2.adapters.WordsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class CreateStudySetFragment : Fragment(R.layout.fragment_create_study_set) {

  private var _binding: FragmentCreateStudySetBinding? = null
  private val binding get() = _binding!!
  private val viewModel: CreateStudySetViewModel by viewModels()
  private lateinit var wordsAdapter: WordsAdapter

  private var languageFrom = "en"
  private var languageTo = "cz"
  private var isEditMode = false
  private var existingSet: StudySet? = null
  private val recognizer by lazy { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

  private val cropImage = registerForActivityResult(CropImageContract()) { result ->
    if (result.isSuccessful) {
      // Use the cropped image URI.
      val croppedImageUri = result.uriContent
      val image: InputImage
      try {
        image = InputImage.fromFilePath(requireContext(), croppedImageUri!!)
        recognizer.process(image)
          .addOnSuccessListener { visionText ->
            val allWords = ArrayList<String>()
            val words = StringBuilder()
            for (block in visionText.textBlocks) {
              for (line in block.lines) {
                var editLine = line.text
                if (editLine.contains("/")) {
                  val index = editLine.indexOf("/")
                  val transcription = editLine.substring(index)
                  editLine = editLine.replace(transcription, "")
                  allWords.add(editLine)
                } else {
                  allWords.add(editLine)
                }
              }
            }
            for (word in allWords) {
              words.append(word).append("\n")
            }
            binding.resultEt.setText(words.toString())
          }
          .addOnFailureListener { _ -> }
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _binding = FragmentCreateStudySetBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
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
      Toast.makeText(requireContext(), languageFrom + languageTo, Toast.LENGTH_SHORT).show()
    }

    binding.scanDocumentBtn.setOnClickListener {
      if (checkCameraPermission()) {
        pickCamera()
      } else {
        requestCameraPermission()
      }
    }

    binding.commitWordsBtn.setOnClickListener {
      if (binding.resultEt.text.toString().isNotEmpty()) {
        requireActivity().window.setFlags(
          WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
          WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        binding.progressBar!!.root.visibility = View.VISIBLE
        manyTranslate(binding.resultEt.text.toString())
      } else {
        Toast.makeText(context, "Result is empty", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun manyTranslate(words: String) {
    val stringsToBeTranslated = words.replace("\\r?\\n".toRegex(), ";")
      .replace("\\[".toRegex(), "").replace("]".toRegex(), "")
    val terms = stringsToBeTranslated.split(";").toTypedArray()

    // Create an English-German translator:
    val options = TranslatorOptions.Builder()
      .setSourceLanguage(languageFrom)
      .setTargetLanguage(languageTo)
      .build()
    val translator = Translation.getClient(options)

    val conditions = DownloadConditions.Builder()
      .requireWifi()
      .build()
    translator.downloadModelIfNeeded(conditions)
      .addOnSuccessListener {
        terms.forEachIndexed { index, term ->
          translator.translate(term)
            .addOnSuccessListener { translatedText ->
              wordsAdapter.addWord(Word(term, translatedText, isMarked = false))


              if (index == terms.lastIndex) {
                onLastWordTranslated(translator)
              }
            }
        }
      }
  }

  private fun onLastWordTranslated(englishGermanTranslator: Translator) {
    englishGermanTranslator.close()
    binding.wordsRecyclerview.scrollToPosition(wordsAdapter.itemCount - 1)
    binding.progressBar!!.root.visibility = View.GONE
    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
  }

  private fun saveStudySet() {
    val name = binding.titleEdittext.text.toString().trim()
    // Проверка на пустое имя и отсутствие слов
    if (name.isEmpty() || wordsAdapter.itemCount == 0) {
      Toast.makeText(requireContext(), "Enter a valid name and add some words", Toast.LENGTH_SHORT).show()
      return
    }

    // Проверка на пустые поля term и translation для каждого слова
    for (word in wordsAdapter.getWords()) {
      if (word.term.isEmpty() || word.translation.isEmpty()) {
        Toast.makeText(requireContext(), "Please fill in all fields for each word", Toast.LENGTH_SHORT).show()
        return
      }
    }

    val wordsString =
      wordsAdapter.getWords().joinToString("\n") { "${it.term} - ${it.translation}" }

    val studySet = if (isEditMode) {
      existingSet?.apply {
        this.name = name
        this.words = wordsString
        this.language_to = languageTo
        this.language_from = languageFrom
        this.amount_of_words = wordsAdapter.itemCount
      }
    } else {
      StudySet(
        creator = "User",
        name = name,
        words = wordsString,
        marked_words = "",
        language_to = languageTo,
        language_from = languageFrom,
        amount_of_words = wordsAdapter.itemCount
      )
    }

    lifecycleScope.launch {
      try {
        if (isEditMode) {
          viewModel.updateStudySet(studySet!!)
        } else {
          viewModel.addStudySet(studySet!!)
        }

        val action =
          CreateStudySetFragmentDirections.actionCreateStudySetFragmentToStudySetDetailsFragment(
            studySet!!
          )
        findNavController().navigate(action)
      } catch (e: Exception) {
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
    val adapter =
      ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, BaseVariables.LANGUAGES)
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

    binding.languageFormSpinner.onItemSelectedListener =
      object : AdapterView.OnItemSelectedListener {
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

  private fun pickCamera() {
    cropImage.launch(
      CropImageContractOptions(
        uri = null,
        cropImageOptions = CropImageOptions(guidelines = CropImageView.Guidelines.ON),
      )
    )
  }

  private fun checkCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
      requireActivity(),
      Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
  }

  private fun requestCameraPermission() {
    requestPermissions(
      arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
      CAMERA_REQUEST_CODE
    )
  }

  //handle permission result
  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<String>,
    grantResults: IntArray,
  ) {
    when (requestCode) {
      CAMERA_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
        val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        val writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
        if (cameraAccepted && writeStorageAccepted) {
          pickCamera()
        } else {
          Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  companion object {
    private const val CAMERA_REQUEST_CODE = 200
  }
}