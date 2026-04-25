package com.example.mikeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class MyKeyboardService : InputMethodService() {

    override fun onCreateInputView(): View {
        return TextView(this).apply {
            text = "😀 😁 😂 🤣 😃 😄 😅 😆"
            textSize = 30f
            setPadding(20, 40, 20, 40)
            setBackgroundColor(0xFF222222.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                currentInputConnection?.commitText("😀", 1)
            }
        }
    }

    override fun onEvaluateFullscreenMode() = false
    override fun onEvaluateInputViewShown() = true
}
