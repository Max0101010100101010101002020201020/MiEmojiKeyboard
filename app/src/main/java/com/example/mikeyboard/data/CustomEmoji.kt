package com.example.mikeyboard.data

data class CustomEmoji(
    val id: String,
    val name: String,
    val filePath: String,
    val createdAt: Long = System.currentTimeMillis()
)
