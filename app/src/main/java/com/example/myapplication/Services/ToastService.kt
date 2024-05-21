package com.example.myapplication.Services

import android.content.Context
import android.widget.Toast

class ToastService {


    companion object {
        private lateinit var context: Context

        fun setContext(con: Context) {
            context = con
        }

        fun makeToastMessage(message: String?, duration: Int){
            Toast.makeText(context, message, duration).show()
        }

    }
}