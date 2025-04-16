package com.example.myapplication2.base

import java.io.Serializable
import java.util.UUID

data class Word(
    var term: String,
    var translation: String,
    var firstStage: Boolean = false,
    var secondStage: Boolean = false,
    var thirdStage: Boolean = false,
    var forthStage: Boolean = false,
    var isMarked: Boolean = false,
    var showKeyboard: Boolean = false,
    val id: String = UUID.randomUUID().toString()
) : Serializable
