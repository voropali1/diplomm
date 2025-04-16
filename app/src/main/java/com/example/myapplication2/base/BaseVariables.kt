package com.example.myapplication2.base

import com.example.myapplication2.R
import java.util.concurrent.TimeUnit

object BaseVariables {

    const val STUDY_SET_ID_MESSAGE = "study_set_id"
    const val STUDY_SET_MESSAGE = "study_set"
    const val WORDS_MESSAGE = "words"
    const val MARKED_WORDS_MESSAGE = "marked_words"
    const val OTHER_WORDS_MESSAGE = "other_words"
    const val QUESTION_AMOUNT_MESSAGE = "question_amount"
    const val TYPE_OF_QUESTIONS_MESSAGE = "type_of_questions"
    const val TITLE_MESSAGE = "title"
    const val AMOUNT_OF_CORRECT_ANSWERS_MESSAGE = "amount_of_correct_answers"
    const val DICTATION_ID_MESSAGE = "dictation_id"
    const val RANDOM_DICTATION_MESSAGE = "random_dictation"
    const val DICTATION_CODE_MESSAGE = "dictation_code"
    const val DICTATION_TYPE_OF_QUESTIONS_MESSAGE = "type_of_questions"
    const val USER_ANSWERS_MESSAGE = "user_answers"
    const val FROM_LANG_MESSAGE = "from_lang"
    const val TO_LANG_MESSAGE = "to_lang"
    const val FRAGMENT_POSITION_MESSAGE = "fragment_position"
    const val MARKED_MESSAGE = "marked"
    const val AMOUNT_OF_WORDS_MESSAGE = "amount_of_words"
    const val DICTATION_MESSAGE = "dictation"

    const val TRANSLATION_TERM = "translation_term"

    const val HELP_CREATE_STUDYSETS_FRAGMENT = "help_create_studysets_fragment"
    const val HELP_STUDY_STUDYSETS_FRAGMENT = "help_studysets_fragment"
    const val HELP_SPECIFIC_STUDYSET = "help_specific_studyset"
    const val HELP_MAKE_DICTATION = "help_make_dictation"

    const val AD_ID = "ca-app-pub-1867610337047797~1889776259"
    const val REWARDED_VIDEO_TEST = "ca-app-pub-3940256099942544/5224354917"
    const val REWARDED_VIDEO = "ca-app-pub-1867610337047797/8527051382"

    const val HOST_URL = "http://vlad12.pythonanywhere.com/"
    const val LOCAL_URL = "http://192.168.1.108:8080/"

    const val GET_DICTATION_HOST_URL = "${HOST_URL}get/dictation/"
    const val STUDY_SET_HOST_URL = "${HOST_URL}studyset/"

    val LANGUAGES = listOf(
        "English", "Russian", "French", "Spanish", "German", "Ukrainian",
        "Italian", "Chinese", "Hungarian", "Norwegian", "Arabic", "Malay", "Portuguese",
        "Czech", "Dutch", "Swedish", "Japanese", "Polish", "Korean", "Turkish",
        "Azerbaijan", "Albanian", "Armenian", "Macedonian", "Belarusian", "Welsh",
        "Greek", "Georgian", "Slovakian", "Slovenian", "Icelandic", "Kazakh",
        "Uzbek", "Croatian", "Latin"
    )

    val LANGUAGES_SHORT = listOf(
        "en", "ru", "fr", "sp", "de", "uk",
        "it", "zh", "hu", "no", "ar", "ms", "pt", "cs",
        "nl", "sv", "ja", "pl", "ko", "tr",
        "az", "sq", "hy", "mk", "be", "cy", "el", "ka", "sk", "sl",
        "is", "kk", "uz", "hr", "la"
    )

    //val emojiList = listOf(
    //    R.drawable.anim_brows, R.drawable.anim_rich,
    //    R.drawable.anim_smart, R.drawable.anim_happy
    //)
//
    //val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    //    .connectTimeout(1, TimeUnit.MINUTES)
    //    .readTimeout(30, TimeUnit.SECONDS)
    //    .writeTimeout(15, TimeUnit.SECONDS)
    //    .build()
//
    //val retrofit: Retrofit = Retrofit.Builder()
    //    .baseUrl(HOST_URL)
    //    .client(okHttpClient)
    //    .addConverterFactory(GsonConverterFactory.create())
    //    .build()
//
    //fun getShareDictationText(code: String): String {
    //    return "Code : $code \n $GET_DICTATION_HOST_URL$code/"
    //}
//
    //fun getShareStudySetText(id: String): String {
    //    return "$STUDY_SET_HOST_URL$id/"
    //}
//
    //fun showKeyboard(editText: EditText) {
    //    editText.post {
    //        editText.requestFocus()
    //        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    //        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    //    }
    //}
//
    //fun hideKeyboard(activity: Activity) {
    //    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //    val view = activity.currentFocus ?: View(activity)
    //    imm.hideSoftInputFromWindow(view.windowToken, 0)
    //}
//
    //fun showKeyboard(activity: Activity) {
    //    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    //    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    //}
//
    //fun generateThreeRandomAnswer(word: Word, words: MutableList<Word>): ArrayList<String> {
    //    words.remove(word)
    //    val answers = arrayListOf<String>()
    //    val random = Random()
    //    repeat(3) {
    //        val randomIndex = random.nextInt(words.size)
    //        answers.add(words[randomIndex].translation)
    //        words.removeAt(randomIndex)
    //    }
    //    return answers
    //}
//
    //fun checkNetworkConnection(context: Context): Boolean {
    //    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
    //        connectivityManager.activeNetworkInfo?.isConnected == true
    //    } else {
    //        val networks = connectivityManager.allNetworks
    //        networks.any {
    //            val nc = connectivityManager.getNetworkCapabilities(it)
    //            nc?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    //        }
    //    }
    //}
//
    //fun convertJSONArrayToArrayOfWords(jsonString: String): ArrayList<Word> {
    //    val wordArrayList = arrayListOf<Word>()
    //    try {
    //        val jsonArray = JSONArray(jsonString)
    //        for (i in 0 until jsonArray.length()) {
    //            val jsonObject = jsonArray.getJSONObject(i)
    //            wordArrayList.add(Word(jsonObject.getString("term"), jsonObject.getString("translation")))
    //        }
    //    } catch (e: JSONException) {
    //        e.printStackTrace()
    //    }
    //    return wordArrayList
    //}
}
