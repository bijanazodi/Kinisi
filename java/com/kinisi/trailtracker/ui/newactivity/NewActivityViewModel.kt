package com.kinisi.trailtracker.ui.newactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewActivityViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is new activity Fragment"
    }
    val text: LiveData<String> = _text
}