package com.example.mikeyboard.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mikeyboard.R

class EmojiGridAdapter(
    private val emojis: List<String>,
    private val onEmojiClick: (String) -> Unit
) : RecyclerView.Adapter<EmojiGridAdapter.EmojiViewHolder>() {

    inner class EmojiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emojiText: TextView = view.findViewById(R.id.emojiText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emoji, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        val emoji = emojis[position]
        holder.emojiText.text = emoji
        holder.itemView.setOnClickListener { onEmojiClick(emoji) }
    }

    override fun getItemCount() = emojis.size
}
