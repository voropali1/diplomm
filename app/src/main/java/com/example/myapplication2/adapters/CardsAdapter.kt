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
import com.example.myapplication2.model.Word
import java.util.Locale
import kotlin.math.roundToInt

class CardsAdapter(
    private val words: List<Word>,
    private val context: Context
) : RecyclerView.Adapter<CardsAdapter.CardViewHolder>(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
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
                    holder.cardText.text = if (flippedStates[newPos]) word.translation else word.term

                    flipIn.start()
                }
            })

            flipOut.start()
        }

        holder.volumeBtn.setOnClickListener {
            tts?.speak(word.term, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }




    override fun getItemCount(): Int = words.size

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        } else {
            Toast.makeText(context, "Ошибка инициализации TTS", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopTTS() {
        tts?.shutdown()
    }

    fun onDestroy() {
        stopTTS()
    }
}

