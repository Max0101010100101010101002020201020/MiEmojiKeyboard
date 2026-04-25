package com.example.mikeyboard.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mikeyboard.R
import com.example.mikeyboard.data.CustomEmoji
import java.io.File

class CustomEmojiAdapter(
    private val context: Context,
    private val emojis: List<CustomEmoji>,
    private val onEmojiClick: (String) -> Unit,
    private val onEmojiDelete: (String) -> Unit
) : RecyclerView.Adapter<CustomEmojiAdapter.CustomEmojiVH>() {

    inner class CustomEmojiVH(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.customEmojiImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomEmojiVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_custom_emoji, parent, false)
        return CustomEmojiVH(view)
    }

    override fun onBindViewHolder(holder: CustomEmojiVH, position: Int) {
        val emoji = emojis[position]
        val file = File(emoji.filePath)
        if (file.exists()) {
            val bmp = BitmapFactory.decodeFile(file.absolutePath)
            holder.imageView.setImageBitmap(bmp)
        }
        holder.itemView.setOnClickListener { onEmojiClick(emoji.filePath) }
        holder.itemView.setOnLongClickListener {
            onEmojiDelete(emoji.id)
            true
        }
    }

    override fun getItemCount() = emojis.size
}
