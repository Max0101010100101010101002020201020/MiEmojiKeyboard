package com.example.mikeyboard

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mikeyboard.data.CustomEmojiRepository
import com.example.mikeyboard.data.EmojiCategory
import com.example.mikeyboard.ui.CustomEmojiAdapter
import com.example.mikeyboard.ui.EmojiGridAdapter

class MyKeyboardService : InputMethodService() {

    private lateinit var repository: CustomEmojiRepository
    private var currentTab = 0
    private lateinit var emojiRecyclerView: RecyclerView
    private lateinit var tabLayout: LinearLayout
    private var rootView: View? = null

    override fun onCreate() {
        super.onCreate()
        repository = CustomEmojiRepository(this)
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)
        rootView = view
        emojiRecyclerView = view.findViewById(R.id.emojiRecyclerView)
        tabLayout = view.findViewById(R.id.tabLayout)

        setupTabs()
        setupActionButtons(view)
        loadEmojiTab(0)

        return view
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        if (currentTab == EmojiCategory.values().size) loadCustomEmojiTab()
    }

    private fun setupTabs() {
        tabLayout.removeAllViews()
        val tabNames = EmojiCategory.values().map { it.label } + listOf("📁")

        tabNames.forEachIndexed { index, name ->
            val tab = TextView(this).apply {
                text = name
                textSize = 18f
                setPadding(20, 14, 20, 14)
                setTextColor(if (index == 0) 0xFF6C63FF.toInt() else 0xFF888888.toInt())
                setOnClickListener {
                    it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    switchTab(index)
                }
            }
            tabLayout.addView(tab)
        }
    }

    private fun switchTab(index: Int) {
        currentTab = index
        for (i in 0 until tabLayout.childCount) {
            val tab = tabLayout.getChildAt(i) as? TextView
            tab?.setTextColor(if (i == index) 0xFF6C63FF.toInt() else 0xFF888888.toInt())
        }
        if (index < EmojiCategory.values().size) loadEmojiTab(index)
        else loadCustomEmojiTab()
    }

    private fun loadEmojiTab(categoryIndex: Int) {
        val emojis = EmojiCategory.values()[categoryIndex].emojis
        emojiRecyclerView.layoutManager = GridLayoutManager(this, 8)
        emojiRecyclerView.adapter = EmojiGridAdapter(emojis) { emoji ->
            currentInputConnection?.commitText(emoji, 1)
        }
        rootView?.findViewById<TextView>(R.id.emptyHint)?.visibility = View.GONE
    }

    private fun loadCustomEmojiTab() {
        val customEmojis = repository.getAllCustomEmojis()
        emojiRecyclerView.layoutManager = GridLayoutManager(this, 4)
        emojiRecyclerView.adapter = CustomEmojiAdapter(
            context = this,
            emojis = customEmojis,
            onEmojiClick = { _ ->
                Toast.makeText(this, "Emoji copiado al portapapeles", Toast.LENGTH_SHORT).show()
            },
            onEmojiDelete = { id ->
                repository.deleteCustomEmoji(id)
                loadCustomEmojiTab()
            }
        )
        rootView?.findViewById<TextView>(R.id.emptyHint)?.visibility =
            if (customEmojis.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupActionButtons(view: View) {
        view.findViewById<ImageButton>(R.id.backspaceBtn)?.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val ic = currentInputConnection ?: return@setOnClickListener
            if (ic.getSelectedText(0).isNullOrEmpty()) ic.deleteSurroundingText(1, 0)
            else ic.commitText("", 1)
        }

        view.findViewById<ImageButton>(R.id.addEmojiBtn)?.setOnClickListener {
            val intent = Intent(this, EmojiPickerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        view.findViewById<ImageButton>(R.id.switchKeyboardBtn)?.setOnClickListener {
            switchToNextInputMethod(false)
        }

        view.findViewById<ImageButton>(R.id.pasteBtn)?.setOnClickListener {
            val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cb.primaryClip?.getItemAt(0)?.coerceToText(this)?.let { text ->
                currentInputConnection?.commitText(text, 1)
            }
        }
    }
}
