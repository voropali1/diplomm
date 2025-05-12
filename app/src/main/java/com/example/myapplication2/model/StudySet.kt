package com.example.myapplication2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "study_set_table")
class StudySet : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var creator: String? = null
    var name: String? = null
    var words: String
    var marked_words: String = ""
    var language_to: String? = null
    var language_from: String? = null
    var amount_of_words: Int

    @ColumnInfo(defaultValue = "false")
    var isFinished: Boolean = false

    constructor(
        creator: String?,
        name: String?,
        words: String,
        marked_words: String?,
        language_to: String?,
        language_from: String?,
        amount_of_words: Int,
        isFinished: Boolean = false
    ) {
        this.creator = creator
        this.name = name
        this.words = words
        this.marked_words = marked_words ?: ""// 👈 Добавляем и инициализируем
        this.language_to = language_to
        this.language_from = language_from
        this.amount_of_words = amount_of_words
        this.isFinished = isFinished
    }

    @Ignore
    constructor(words: String, amount_of_words: Int) {
        this.words = words
        this.amount_of_words = amount_of_words
    }

    @Ignore
    constructor(id: Int, words: String, amount_of_words: Int) {
        this.words = words
        this.id = id
        this.amount_of_words = amount_of_words
    }

    @Ignore
    constructor(
        id: Int,
        name: String?,
        words: String,
        marked_words: String?,
        language_to: String?,
        language_from: String?,
        amount_of_words: Int,
        isFinished: Boolean
    ) {
        this.id = id
        this.name = name
        this.words = words
        this.marked_words = marked_words ?: ""
        this.language_to = language_to
        this.language_from = language_from
        this.amount_of_words = amount_of_words
        this.isFinished = isFinished
    }
}
