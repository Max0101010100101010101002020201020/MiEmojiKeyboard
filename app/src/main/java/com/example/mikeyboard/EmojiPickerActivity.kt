package com.example.mikeyboard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class EmojiPickerActivity : Activity() {

    private var selectedUri: Uri? = null
    private lateinit var preview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF000000.toInt())
            setPadding(32, 48, 32, 32)
        }

        TextView(this).apply {
            text = "📷 Agregar Emoji"
            textSize = 22f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 0, 0, 24)
            root.addView(this)
        }

        preview = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                gravity = android.view.Gravity.CENTER_HORIZONTAL
                bottomMargin = 24
            }
            setBackgroundColor(0xFF1C1C1C.toInt())
            root.addView(this)
        }

        Button(this).apply {
            text = "Seleccionar imagen"
            setBackgroundColor(0xFF333333.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1001)
            }
            root.addView(this)
        }

        Button(this).apply {
            text = "✅ Guardar emoji"
            setBackgroundColor(0xFF2A5C2A.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = 16 }
            setOnClickListener { saveEmoji() }
            root.addView(this)
        }

        // Mostrar emojis guardados
        TextView(this).apply {
            text = "Mis emojis (mantén para borrar):"
            textSize = 14f
            setTextColor(0xFF888888.toInt())
            setPadding(0, 32, 0, 8)
            root.addView(this)
        }

        val grid = GridLayout(this).apply {
            columnCount = 5
            root.addView(this)
        }
        loadSavedEmojis(grid)

        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            selectedUri = data?.data
            selectedUri?.let {
                val bmp = MediaStore.Images.Media.getBitmap(contentResolver, it)
                preview.setImageBitmap(bmp)
            }
        }
    }

    private fun saveEmoji() {
        val uri = selectedUri ?: run {
            Toast.makeText(this, "Primero selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }
        val bmp = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val scaled = Bitmap.createScaledBitmap(bmp, 128, 128, true)
        val dir = File(filesDir, "emojis").also { it.mkdirs() }
        val file = File(dir, "${UUID.randomUUID()}.png")
        FileOutputStream(file).use { scaled.compress(Bitmap.CompressFormat.PNG, 100, it) }
        Toast.makeText(this, "✅ Emoji guardado", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun loadSavedEmojis(grid: GridLayout) {
        val dir = File(filesDir, "emojis")
        if (!dir.exists()) return
        dir.listFiles()?.forEach { file ->
            val img = ImageView(this).apply {
                val bmp = BitmapFactory.decodeFile(file.absolutePath)
                setImageBitmap(bmp)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 120
                    height = 120
                    setMargins(8, 8, 8, 8)
                }
                setOnLongClickListener {
                    file.delete()
                    grid.removeView(this)
                    true
                }
            }
            grid.addView(img)
        }
    }
}
