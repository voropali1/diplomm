package com.example.myapplication2.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel


class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData("This is dashboard Fragment")
    val text: LiveData<String> = _text
}