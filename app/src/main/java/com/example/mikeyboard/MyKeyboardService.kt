package com.example.mikeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mikeyboard.data.EmojiCategory
import com.example.mikeyboard.ui.EmojiGridAdapter

class MyKeyboardService : InputMethodService() {

    override fun onCreateInputView(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF1A1A2E.toInt())
        }

        // Tab bar
        val scrollTabs = HorizontalScrollView(this)
        val tabBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val recycler = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400
            )
        }

        // Cargar primera categoría
        loadCategory(recycler, 0)

        // Crear tabs
        EmojiCategory.values().forEachIndexed { index, cat ->
            val tab = TextView(this).apply {
                text = cat.label
                textSize = 20f
                setPadding(24, 16, 24, 16)
                setOnClickListener { loadCategory(recycler, index) }
            }
            tabBar.addView(tab)
        }

        scrollTabs.addView(tabBar)
        root.addView(scrollTabs)
        root.addView(recycler)

        return root
    }

    private fun loadCategory(recycler: RecyclerView, index: Int) {
        val emojis = EmojiCategory.values()[index].emojis
        recycler.layoutManager = GridLayoutManager(this, 8)
        recycler.adapter = EmojiGridAdapter(emojis) { emoji ->
            currentInputConnection?.commitText(emoji, 1)
        }
    }
}
