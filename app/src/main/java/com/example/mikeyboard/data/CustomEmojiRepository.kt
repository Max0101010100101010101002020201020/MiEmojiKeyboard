package com.example.mikeyboard.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.UUID

class CustomEmojiRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("custom_emojis", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val EMOJIS_KEY = "emojis_list"

    fun getAllCustomEmojis(): List<CustomEmoji> {
        val json = prefs.getString(EMOJIS_KEY, "[]") ?: "[]"
        val type = object : TypeToken<List<CustomEmoji>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveCustomEmoji(name: String, sourceFilePath: String): CustomEmoji {
        val id = UUID.randomUUID().toString()
        val destFile = File(context.filesDir, "emojis/$id.png")
        destFile.parentFile?.mkdirs()
        File(sourceFilePath).copyTo(destFile, overwrite = true)

        val emoji = CustomEmoji(id = id, name = name, filePath = destFile.absolutePath)
        val current = getAllCustomEmojis().toMutableList()
        current.add(emoji)
        saveAll(current)
        return emoji
    }

    fun deleteCustomEmoji(id: String) {
        val current = getAllCustomEmojis().toMutableList()
        val emoji = current.find { it.id == id }
        emoji?.let {
            File(it.filePath).delete()
            current.remove(it)
        }
        saveAll(current)
    }

    private fun saveAll(emojis: List<CustomEmoji>) {
        prefs.edit().putString(EMOJIS_KEY, gson.toJson(emojis)).apply()
    }
}
