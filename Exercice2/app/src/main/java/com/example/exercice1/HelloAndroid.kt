package com.example.exercice1

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class HelloAndroid : Activity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        textView.text = "Hello, Android"

        setContentView(textView)
    }
}