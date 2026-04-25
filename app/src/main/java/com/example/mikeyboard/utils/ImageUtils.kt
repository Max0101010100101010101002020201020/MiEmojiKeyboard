package com.example.mikeyboard.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

object ImageUtils {

    private const val TARGET_SIZE = 128

    fun resizeImage(context: Context, uri: Uri): Bitmap {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }

        options.inSampleSize = calculateInSampleSize(options, TARGET_SIZE, TARGET_SIZE)
        options.inJustDecodeBounds = false

        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: throw IllegalStateException("No se pudo leer la imagen")

        return Bitmap.createScaledBitmap(bitmap, TARGET_SIZE, TARGET_SIZE, true)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqW: Int, reqH: Int): Int {
        val (h, w) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (h > reqH || w > reqW) {
            val halfH = h / 2
            val halfW = w / 2
            while (halfH / inSampleSize >= reqH && halfW / inSampleSize >= reqW) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
