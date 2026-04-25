package com.example.mikeyboard

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mikeyboard.data.EmojiCategory
import com.example.mikeyboard.ui.EmojiGridAdapter

class MyKeyboardService : InputMethodService() {

    private val BG = 0xFF0A0A0A.toInt()
    private val SURFACE = 0xFF1A1A1A.toInt()
    private val KEY = 0xFF2A2A2A.toInt()
    private val ACCENT = 0xFF555555.toInt()
    private val TEXT = 0xFFEEEEEE.toInt()
    private val HINT = 0xFF888888.toInt()

    private var mode = "emoji" // "emoji", "letters", "numbers"
    private var emojiTab = 0
    private lateinit var contentFrame: FrameLayout

    override fun onCreateInputView(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
        }

        // ── Top bar ──────────────────────────────────────
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(SURFACE)
            setPadding(8, 6, 8, 6)
        }

        fun iconBtn(label: String, action: () -> Unit) = TextView(this).apply {
            text = label
            textSize = 18f
            setTextColor(TEXT)
            setPadding(18, 10, 18, 10)
            setBackgroundColor(KEY)
            setOnClickListener { it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP); action() }
        }

        topBar.addView(iconBtn("😀") { switchMode("emoji") })
        topBar.addView(iconBtn("ABC") { switchMode("letters") })
        topBar.addView(iconBtn("123") { switchMode("numbers") })
        topBar.addView(View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 1, 1f)
        })
        topBar.addView(iconBtn("＋") {
            val i = Intent(this@MyKeyboardService, EmojiPickerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        })
        topBar.addView(iconBtn("⌫") {
            currentInputConnection?.deleteSurroundingText(1, 0)
        })

        // ── Content ───────────────────────────────────────
        contentFrame = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        root.addView(topBar)
        root.addView(contentFrame)

        showEmojis()
        return root
    }

    private fun switchMode(m: String) {
        mode = m
        contentFrame.removeAllViews()
        when (m) {
            "emoji" -> showEmojis()
            "letters" -> showLetters()
            "numbers" -> showNumbers()
        }
    }

    // ── EMOJI MODE ────────────────────────────────────────
    private fun showEmojis() {
        val wrap = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        // Tab bar
        val scroll = HorizontalScrollView(this).apply { setBackgroundColor(SURFACE) }
        val tabs = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }

        val recycler = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 320)
            setBackgroundColor(BG)
        }

        EmojiCategory.values().forEachIndexed { i, cat ->
            val tab = TextView(this).apply {
                text = cat.label
                textSize = 20f
                setPadding(20, 12, 20, 12)
                setTextColor(if (i == 0) TEXT else HINT)
                setOnClickListener {
                    emojiTab = i
                    loadEmojiTab(recycler, i)
                    for (j in 0 until tabs.childCount)
                        (tabs.getChildAt(j) as TextView).setTextColor(if (j == i) TEXT else HINT)
                }
            }
            tabs.addView(tab)
        }

        scroll.addView(tabs)
        loadEmojiTab(recycler, 0)

        wrap.addView(scroll)
        wrap.addView(recycler)
        contentFrame.addView(wrap)
    }

    private fun loadEmojiTab(recycler: RecyclerView, index: Int) {
        val emojis = EmojiCategory.values()[index].emojis
        recycler.layoutManager = GridLayoutManager(this, 8)
        recycler.adapter = EmojiGridAdapter(emojis) { emoji ->
            currentInputConnection?.commitText(emoji, 1)
        }
    }

    // ── LETTERS MODE ──────────────────────────────────────
    private fun showLetters() {
        val rows = listOf(
            listOf("q","w","e","r","t","y","u","i","o","p"),
            listOf("a","s","d","f","g","h","j","k","l"),
            listOf("z","x","c","v","b","n","m")
        )
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
            setPadding(4, 8, 4, 8)
        }

        rows.forEach { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                gravity = android.view.Gravity.CENTER_HORIZONTAL
                setPadding(0, 4, 0, 4)
            }
            row.forEach { letter ->
                TextView(this).apply {
                    text = letter
                    textSize = 18f
                    setTextColor(TEXT)
                    setBackgroundColor(KEY)
                    gravity = android.view.Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        setMargins(3, 2, 3, 2)
                    }
                    setPadding(4, 16, 4, 16)
                    setOnClickListener {
                        it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        currentInputConnection?.commitText(letter, 1)
                    }
                    rowLayout.addView(this)
                }
            }
            wrap.addView(rowLayout)
        }

        // Space + Enter row
        val bottomRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4, 4, 4, 4)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        fun wideBtn(label: String, weight: Float, action: () -> Unit) =
            TextView(this).apply {
                text = label
                textSize = 15f
                setTextColor(TEXT)
                setBackgroundColor(ACCENT)
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, weight).apply {
                    setMargins(3, 2, 3, 2)
                }
                setPadding(4, 16, 4, 16)
                setOnClickListener {
                    it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    action()
                }
            }

        bottomRow.addView(wideBtn("⇧", 1.5f) {})
        bottomRow.addView(wideBtn("espacio", 4f) {
            currentInputConnection?.commitText(" ", 1)
        })
        bottomRow.addView(wideBtn("↵", 1.5f) {
            currentInputConnection?.sendKeyEvent(
                android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
        })

        wrap.addView(bottomRow)
        contentFrame.addView(wrap)
    }

    // ── NUMBERS MODE ──────────────────────────────────────
    private fun showNumbers() {
        val rows = listOf(
            listOf("1","2","3","4","5","6","7","8","9","0"),
            listOf("-","/",":",";","(",")","\$","&","@","\""),
            listOf(".",",","?","!","'","+","=","_","#","%")
        )
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
            setPadding(4, 8, 4, 8)
        }

        rows.forEach { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                setPadding(0, 4, 0, 4)
            }
            row.forEach { sym ->
                TextView(this).apply {
                    text = sym
                    textSize = 18f
                    setTextColor(TEXT)
                    setBackgroundColor(KEY)
                    gravity = android.view.Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                        setMargins(3, 2, 3, 2)
                    }
                    setPadding(4, 16, 4, 16)
                    setOnClickListener {
                        it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        currentInputConnection?.commitText(sym, 1)
                    }
                    rowLayout.addView(this)
                }
            }
            wrap.addView(rowLayout)
        }

        contentFrame.addView(wrap)
    }

    override fun onEvaluateFullscreenMode() = false
    override fun onEvaluateInputViewShown() = true
}
