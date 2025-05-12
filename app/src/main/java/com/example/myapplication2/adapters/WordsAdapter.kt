package com.example.myapplication2.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.R
import com.example.myapplication2.model.Word
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class WordsAdapter : RecyclerView.Adapter<WordsAdapter.WordsHolder>() {

    var fromLanguage: String = "en"
        set(value) {
            field = value
            updateLanguage()
        }

    var toLanguage: String = "cz"
        set(value) {
            field = value
            updateLanguage()
        }

    private val wordsList = mutableListOf<Word>()
    private var translator: Translator = createTranslator()
    private val conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    inner class WordsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val termEditText: EditText = itemView.findViewById(R.id.term_TV)
        private val translationEditText: EditText = itemView.findViewById(R.id.translation_TV)
        private val deleteBtn: ImageView = itemView.findViewById(R.id.remove_list_word_item_btn)
        private val translationSupport: TextView = itemView.findViewById(R.id.translation_support)
        private val translateTermButton: ImageView = itemView.findViewById(R.id.ivTranslateTerm)

        fun bind(word: Word) {
            termEditText.setText(word.term)
            translationEditText.setText(word.translation)

            termEditText.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        wordsList[adapterPosition].term = s.toString()
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                }
            )

            translationEditText.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        wordsList[adapterPosition].translation = s.toString()
                        translationSupport.visibility = View.INVISIBLE
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                }
            )

            translationEditText.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    translateWordAndDisplayIt(termEditText.text.toString(), translationSupport)
                }
            }

            deleteBtn.setOnClickListener {
                removeWord(adapterPosition)
            }

            translationSupport.setOnClickListener {
                val supportText = translationSupport.text
                if (supportText.isNotBlank()) {
                    translationEditText.setText(supportText)
                    translationSupport.visibility = View.INVISIBLE
                    translationEditText.setSelection(translationEditText.text.length)
                }
            }

            translateTermButton.setOnClickListener {
                translateWordAndDisplayIt(termEditText.text.toString(), termEditText)
                translateWordAndDisplayIt(translationEditText.text.toString(), translationEditText)
            }
        }

        private fun translateWordAndDisplayIt(word: String, tvSupport: TextView) {
            if (word.isNotEmpty()) {
                translateWord(word) { translatedText ->
                    tvSupport.text = translatedText
                    tvSupport.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_words_item, parent, false)
        return WordsHolder(view)
    }

    override fun onBindViewHolder(holder: WordsHolder, position: Int) {
        holder.bind(wordsList[position])
    }

    override fun getItemCount(): Int = wordsList.size

    fun addWord(word: Word) {
        wordsList.add(word)
        notifyItemInserted(wordsList.size - 1)
    }

    fun removeWord(position: Int) {
        if (position in wordsList.indices) {
            wordsList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, wordsList.size)
        }
    }

    fun getWords(): List<Word> = wordsList.toList()

    fun setWords(words: List<Word>) {
        wordsList.clear()
        wordsList.addAll(words)
        notifyDataSetChanged()
    }

    private fun updateLanguage() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(fromLanguage)
            .setTargetLanguage(toLanguage)
            .build()
        translator = Translation.getClient(options)
        translator.downloadModelIfNeeded()
    }

    private fun translateWord(word: String, onSuccess: (String) -> Unit) {
        translator.translate(word)
            .addOnSuccessListener { translatedText ->
                onSuccess(translatedText)
            }
    }

    private fun createTranslator(): Translator {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(fromLanguage)
            .setTargetLanguage(toLanguage)
            .build()

        return Translation.getClient(options).apply {
            downloadModelIfNeeded()
        }
    }
}


