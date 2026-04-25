package com.example.mikeyboard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.example.mikeyboard.data.CustomEmojiRepository
import com.example.mikeyboard.utils.ImageUtils
import java.io.File
import java.io.FileOutputStream

class EmojiPickerActivity : Activity() {

    companion object {
        const val PICK_IMAGE_REQUEST = 1001
    }

    private lateinit var repository: CustomEmojiRepository
    private lateinit var previewImage: ImageView
    private lateinit var emojiNameInput: EditText
    private lateinit var saveButton: Button
    private var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji_picker)
        repository = CustomEmojiRepository(this)

        previewImage = findViewById(R.id.previewImage)
        emojiNameInput = findViewById(R.id.emojiNameInput)
        saveButton = findViewById(R.id.saveEmojiButton)

        findViewById<Button>(R.id.pickImageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        saveButton.setOnClickListener { saveEmoji() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val uri: Uri = data?.data ?: return
            val bitmap = ImageUtils.resizeImage(this, uri)
            selectedBitmap = bitmap
            previewImage.setImageBitmap(bitmap)
            saveButton.visibility = View.VISIBLE
        }
    }

    private fun saveEmoji() {
        val bitmap = selectedBitmap ?: run {
            Toast.makeText(this, "Primero selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }
        val name = emojiNameInput.text.toString().ifBlank { "Mi Emoji" }

        // Save bitmap to a temp file, then let repository handle the copy
        val tempFile = File(cacheDir, "temp_emoji.png")
        FileOutputStream(tempFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        repository.saveCustomEmoji(name, tempFile.absolutePath)
        Toast.makeText(this, "✅ Emoji guardado: $name", Toast.LENGTH_SHORT).show()
        finish()
    }
}
