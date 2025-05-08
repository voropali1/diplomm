package com.example.myapplication2.adapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.R
import com.example.myapplication2.databinding.ItemCardBinding
import com.example.myapplication2.model.Word
import java.util.Locale
import kotlin.math.roundToInt

class CardsAdapter(
    private val words: List<Word>,
    private val context: Context,
    private val languageFrom: String,  // Язык термина
    private val languageTo: String     // Язык перевода
) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val flippedStates = BooleanArray(words.size) { false }

    init {
        tts = TextToSpeech(context, this)
    }

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view as CardView
        val cardText: TextView = view.findViewById(R.id.card_text)
        val volumeBtn: ImageButton = view.findViewById(R.id.volume_up_IB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val word = words[position]
        val isFlipped = flippedStates[position]

        val padding = (context.resources.getDimension(R.dimen.cardHorizontalPadding) +
                context.resources.getDimension(R.dimen.horizontalContentPadding)).roundToInt()
        holder.cardView.updateLayoutParams<RecyclerView.LayoutParams> {
            this.updateMargins(left = padding, right = padding)
        }

        holder.cardText.text = if (isFlipped) word.translation else word.term

        // Переворот карточки
        holder.cardView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val flipOut = ObjectAnimator.ofFloat(holder.cardView, "rotationY", 0f, 90f)
            val flipIn = ObjectAnimator.ofFloat(holder.cardView, "rotationY", -90f, 0f)

            flipOut.duration = 150
            flipIn.duration = 150

            flipOut.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val newPos = holder.adapterPosition
                    if (newPos == RecyclerView.NO_POSITION) return

                    flippedStates[newPos] = !flippedStates[newPos]
                    val isNowFlipped = flippedStates[newPos]
                    holder.cardText.text = if (isNowFlipped) word.translation else word.term

                    flipIn.start()
                }
            })

            flipOut.start()
        }

        // Озвучка текста
        holder.volumeBtn.setOnClickListener {
            val isFlippedNow = flippedStates[holder.adapterPosition]
            val textToSpeak = if (isFlippedNow) word.translation else word.term
            val langTag = if (isFlippedNow) languageTo else languageFrom

            if (isTtsInitialized) {
                val locale = Locale.forLanguageTag(langTag)
                val result = tts?.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "Язык $langTag не поддерживается", Toast.LENGTH_SHORT).show()
                } else {
                    tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    override fun getItemCount(): Int = words.size

    override fun onInit(status: Int) {
        isTtsInitialized = status == TextToSpeech.SUCCESS
        tts?.setSpeechRate(0.7f)
        if (!isTtsInitialized) {
            Toast.makeText(context, "Ошибка инициализации TTS", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopTTS() {
        tts?.stop()
        tts?.shutdown()
    }

    fun onDestroy() {
        stopTTS()
    }
}


