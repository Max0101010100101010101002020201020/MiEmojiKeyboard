package com.example.mikeyboard

import android.inputmethodservice.InputMethodService
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mikeyboard.data.EmojiCategory
import com.example.mikeyboard.ui.EmojiGridAdapter
import android.content.Intent

class MyKeyboardService : InputMethodService() {

    private val BG      = 0xFF000000.toInt()
    private val KEY     = 0xFF1C1C1C.toInt()
    private val ACCENT  = 0xFF333333.toInt()
    private val TEXT    = 0xFFFFFFFF.toInt()
    private val HINT    = 0xFF666666.toInt()

    private var caps = false
    private lateinit var contentFrame: FrameLayout
    private lateinit var root: LinearLayout

    override fun onCreateInputView(): View {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
        }

        // ── MODE BAR ─────────────────────────────────────
        val modeBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(0xFF111111.toInt())
            setPadding(4, 4, 4, 4)
        }

        fun modeBtn(label: String, action: () -> Unit) = TextView(this).apply {
            text = label
            textSize = 15f
            setTextColor(TEXT)
            setBackgroundColor(KEY)
            gravity = android.view.Gravity.CENTER
            setPadding(20, 12, 20, 12)
            layoutParams = LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { setMargins(3,2,3,2) }
            setOnClickListener { performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP); action() }
        }

        modeBar.addView(modeBtn("😀") { showEmojis() })
        modeBar.addView(modeBtn("ABC") { showLetters() })
        modeBar.addView(modeBtn("123") { showNumbers() })
        modeBar.addView(modeBtn("📷") {
            val i = Intent(this@MyKeyboardService, EmojiPickerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        })
        modeBar.addView(modeBtn("⌫") {
            currentInputConnection?.deleteSurroundingText(1, 0)
        })

        contentFrame = FrameLayout(this)

        root.addView(modeBar)
        root.addView(contentFrame)

        showLetters()
        return root
    }

    // ── LETTERS ───────────────────────────────────────────
    private fun showLetters() {
        contentFrame.removeAllViews()
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
        }

        val rows = listOf(
            listOf("q","w","e","r","t","y","u","i","o","p"),
            listOf("a","s","d","f","g","h","j","k","l"),
            listOf("z","x","c","v","b","n","m")
        )

        rows.forEach { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER
                setPadding(2, 3, 2, 3)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            row.forEach { letter ->
                TextView(this).apply {
                    text = if (caps) letter.uppercase() else letter
                    textSize = 20f
                    setTextColor(TEXT)
                    setBackgroundColor(KEY)
                    gravity = android.view.Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, 120, 1f).apply {
                        setMargins(3, 3, 3, 3)
                    }
                    setOnClickListener {
                        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        val c = if (caps) letter.uppercase() else letter
                        currentInputConnection?.commitText(c, 1)
                    }
                    rowLayout.addView(this)
                }
            }
            wrap.addView(rowLayout)
        }

        // Bottom row
        val bottomRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            setPadding(2, 3, 2, 3)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        fun specialBtn(label: String, w: Float, color: Int = ACCENT, action: () -> Unit) =
            TextView(this).apply {
                text = label
                textSize = 16f
                setTextColor(TEXT)
                setBackgroundColor(color)
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, 120, w).apply { setMargins(3,3,3,3) }
                setOnClickListener {
                    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    action()
                }
            }

        bottomRow.addView(specialBtn("⇧", 1.5f) {
            caps = !caps
            showLetters()
        })
        bottomRow.addView(specialBtn("ESPACIO", 5f, 0xFF222222.toInt()) {
            currentInputConnection?.commitText(" ", 1)
        })
        bottomRow.addView(specialBtn("↵", 1.5f) {
            currentInputConnection?.sendKeyEvent(
                android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN,
                    android.view.KeyEvent.KEYCODE_ENTER))
        })

        wrap.addView(bottomRow)
        contentFrame.addView(wrap)
    }

    // ── NUMBERS ───────────────────────────────────────────
    private fun showNumbers() {
        contentFrame.removeAllViews()
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
        }

        val rows = listOf(
            listOf("1","2","3","4","5","6","7","8","9","0"),
            listOf("-","/",":",";","(",")","\$","&","@","\""),
            listOf(".",",","?","!","'","+","=","_","#","%")
        )

        rows.forEach { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER
                setPadding(2, 3, 2, 3)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            row.forEach { sym ->
                TextView(this).apply {
                    text = sym
                    textSize = 20f
                    setTextColor(TEXT)
                    setBackgroundColor(KEY)
                    gravity = android.view.Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, 120, 1f).apply {
                        setMargins(3, 3, 3, 3)
                    }
                    setOnClickListener {
                        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        currentInputConnection?.commitText(sym, 1)
                    }
                    rowLayout.addView(this)
                }
            }
            wrap.addView(rowLayout)
        }
        contentFrame.addView(wrap)
    }

    // ── EMOJIS ────────────────────────────────────────────
    private fun showEmojis() {
        contentFrame.removeAllViews()
        val wrap = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        val recycler = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 380)
            setBackgroundColor(BG)
        }

        val scroll = HorizontalScrollView(this).apply {
            setBackgroundColor(0xFF111111.toInt())
        }
        val tabs = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }

        EmojiCategory.values().forEachIndexed { i, cat ->
            TextView(this).apply {
                text = cat.label
                textSize = 22f
                setPadding(18, 10, 18, 10)
                setTextColor(if (i == 0) TEXT else HINT)
                setOnClickListener {
                    loadEmojiTab(recycler, i)
                    for (j in 0 until tabs.childCount)
                        (tabs.getChildAt(j) as TextView)
                            .setTextColor(if (j == i) TEXT else HINT)
                }
                tabs.addView(this)
            }
        }

        scroll.addView(tabs)
        loadEmojiTab(recycler, 0)

        wrap.addView(scroll)
        wrap.addView(recycler)
        contentFrame.addView(wrap)
    }

    private fun loadEmojiTab(recycler: RecyclerView, index: Int) {
        recycler.layoutManager = GridLayoutManager(this, 8)
        recycler.adapter = EmojiGridAdapter(EmojiCategory.values()[index].emojis) { emoji ->
            currentInputConnection?.commitText(emoji, 1)
        }
    }

    override fun onEvaluateFullscreenMode() = false
    override fun onEvaluateInputViewShown() = true
}
// este archivo ya tiene todo, solo agrega la funcion de emojis custom al showEmojis
