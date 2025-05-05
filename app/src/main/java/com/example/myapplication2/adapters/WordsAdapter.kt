package com.langamy.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.R
import com.example.myapplication2.model.Word

class WordsAdapter : RecyclerView.Adapter<WordsAdapter.WordsHolder>() {

    private val wordsList = mutableListOf<Word>()

    inner class WordsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val termEditText: EditText = itemView.findViewById(R.id.term_TV)
        val translationEditText: EditText = itemView.findViewById(R.id.translation_TV)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.remove_list_word_item_btn)

        fun bind(word: Word) {
            termEditText.setText(word.term)
            translationEditText.setText(word.translation)

            termEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    wordsList[adapterPosition].term = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            translationEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    wordsList[adapterPosition].translation = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            deleteBtn.setOnClickListener {
                removeWord(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_words_item, parent, false)
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

    // Новый метод для установки слов
    fun setWords(words: List<Word>) {
        wordsList.clear()  // Очистим текущий список
        wordsList.addAll(words)  // Добавляем новые слова
        notifyDataSetChanged()  // Обновляем адаптер
    }


}


