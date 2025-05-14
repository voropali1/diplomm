package com.example.myapplication2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.R
import com.example.myapplication2.model.StudySet
import com.example.myapplication2.model.Word
import com.example.myapplication2.repository.StudySetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpecificStudySetAdapter(
    val studySet: StudySet,
    val repository: StudySetRepository,
    private var mWords: List<Word>,
    private val coroutineScope: CoroutineScope,
) : RecyclerView.Adapter<SpecificStudySetAdapter.SpecificStudySetHolder>() {

    class SpecificStudySetHolder(view: View) : RecyclerView.ViewHolder(view) {
        val term: TextView = view.findViewById(R.id.term_TV)
        val translation: TextView = view.findViewById(R.id.translation_TV)
        val starBtn: ToggleButton = view.findViewById(R.id.starBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecificStudySetHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.studyset_word_item, parent, false)
        return SpecificStudySetHolder(view)
    }

    override fun onBindViewHolder(holder: SpecificStudySetHolder, position: Int) {
        val word = mWords[position]

        holder.term.text = word.term
        holder.translation.text = word.translation
        holder.starBtn.setOnCheckedChangeListener(null)
        holder.starBtn.isChecked = word.isMarked

        holder.starBtn.setOnCheckedChangeListener { _, isChecked ->
            word.isMarked = isChecked

            coroutineScope.launch(Dispatchers.IO) {
                val markedWords = if (isChecked) {
                     buildString {
                        append(studySet.marked_words)
                        append("\n${word.term} - ${word.translation}")
                    }
                } else {
                    studySet.marked_words.replace("\n${word.term} - ${word.translation}", "")
                }

                studySet.marked_words = markedWords.trim()
                repository.update(studySet)
            }
        }

    }

    override fun getItemCount(): Int = mWords.size

    fun updateData(newWords: List<Word>) {
        mWords = newWords
        notifyDataSetChanged()
    }
}
