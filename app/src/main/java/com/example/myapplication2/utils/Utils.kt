package com.example.myapplication2.utils

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication2.R

fun Context.isTablet() = resources.getBoolean(R.bool.isTablet)

fun Context.getTabletLayoutManager() = if (isTablet()) {
    GridLayoutManager(this, 3)
} else {
    LinearLayoutManager(this)
}

fun getEmojiMessage(isCorrect: Boolean): String {
    return if (isCorrect) "Correct!" else "Incorrect!"
}

fun getEmoji(isCorrect: Boolean): Int{
    return if (isCorrect) R.drawable.anim_happy else R.drawable.anim_drop
}