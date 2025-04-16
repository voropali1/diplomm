package com.example.myapplication2.adapters

import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.R
import com.example.myapplication2.base.Word
import javax.inject.Inject


class SpecificStudySetAdapter @Inject constructor(
    private var mWords: List<Word>,
    val sharedPreferences: SharedPreferences
) : RecyclerView.Adapter<SpecificStudySetAdapter.SpecificStudySetHolder>() {

    class SpecificStudySetHolder(view: View) : RecyclerView.ViewHolder(view) {
        val term: TextView = view.findViewById(R.id.term_TV)
        val translation: TextView = view.findViewById(R.id.translation_TV)
        val starBtn: ToggleButton = view.findViewById(R.id.starBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecificStudySetHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.studyset_word_item, parent, false)
        return SpecificStudySetHolder(view)
    }

    override fun onBindViewHolder(holder: SpecificStudySetHolder, position: Int) {
        val word = mWords[position]

        Log.d("SpecificStudySetAdapter", "Binding word: ${word.term} - ${word.translation}, isMarked: ${word.isMarked}")

        holder.term.text = word.term
        holder.translation.text = word.translation

        holder.starBtn.setOnCheckedChangeListener(null) // сбрасываем предыдущий listener
        holder.starBtn.isChecked = word.isMarked

        // Логируем текущее состояние звездочки
        Log.d("SpecificStudySetAdapter", "Initial star button state for ${word.term}: ${holder.starBtn.isChecked}")

        holder.starBtn.setOnCheckedChangeListener { _, isChecked ->
            word.isMarked = isChecked
            // Логируем изменение состояния
            Log.d("SpecificStudySetAdapter", "Star button clicked for ${word.term}, new state: $isChecked")

            // Сохраняем состояние звездочки в SharedPreferences с использованием уникального id
            val key = "word_marked_${word.term}_${word.translation}".hashCode().toString()
            sharedPreferences.edit().putBoolean(key, isChecked).apply()

            // Логируем результат сохранения
            Log.d("SpecificStudySetAdapter", "Saved star state for ${word.term} to SharedPreferences: $isChecked")
        }

    }


    override fun getItemCount(): Int = mWords.size

    fun updateData(newWords: List<Word>) {
        mWords = newWords
        notifyDataSetChanged()
    }

    fun getAllWords(): List<Word> = mWords
}
