package com.example.myapplication2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.R
import com.example.myapplication2.model.StudySet
import java.util.Locale


class StudySetsAdapter(
    private var itemsList: List<StudySet>,
    private val callback: Callback
) : RecyclerView.Adapter<StudySetsAdapter.StudySetsHolder>(), Filterable {

    private var originalList: List<StudySet> = itemsList
    private var filteredList: List<StudySet> = itemsList

    inner class StudySetsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.studyset_title)
        private val amountOfWords: TextView = itemView.findViewById(R.id.amount_of_words)
        private val delete: ImageButton = itemView.findViewById(R.id.delete_studyset_BTN)

        fun bind(item: StudySet) {
            title.text = item.name
            amountOfWords.text = itemView.context.getString(R.string.amount_of_words, item.amount_of_words.toString())

            // Обработка клика по элементу списка
            itemView.setOnClickListener {
                callback.onItemClicked(filteredList[adapterPosition]) // Отправляем выбранный StudySet
            }

            delete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    callback.onDeleteClicked(filteredList[adapterPosition], adapterPosition)
                }
            }
        }
    }

    interface Callback {
        fun onDeleteClicked(item: StudySet, position: Int)
        fun onItemClicked(item: StudySet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudySetsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.study_set_item, parent, false)
        return StudySetsHolder(view)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: StudySetsHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charSearch = charSequence.toString().lowercase(Locale.ROOT)
                filteredList = if (charSearch.isEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.name?.lowercase(Locale.ROOT)?.contains(charSearch) ?: false
                    }
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                filteredList = filterResults?.values as List<StudySet>
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newList: List<StudySet>) {
        val diffCallback = StudySetDiffCallback(filteredList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        originalList = newList
        filteredList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class StudySetDiffCallback(
        private val oldList: List<StudySet>,
        private val newList: List<StudySet>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
