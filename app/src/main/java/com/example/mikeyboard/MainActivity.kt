package com.example.mikeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 80, 40, 40)
        }

        TextView(this).apply {
            text = "😼 MiEmojiKeyboard"
            textSize = 24f
            layout.addView(this)
        }

        Button(this).apply {
            text = "1. Activar teclado en Ajustes"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
            layout.addView(this)
        }

        Button(this).apply {
            text = "2. Seleccionar como teclado activo"
            setOnClickListener {
                val im = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                im.showInputMethodPicker()
            }
            layout.addView(this)
        }

        setContentView(layout)
    }
}
