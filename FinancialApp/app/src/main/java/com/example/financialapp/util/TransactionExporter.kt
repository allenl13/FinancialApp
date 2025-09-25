package com.example.financialapp.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.financialapp.data.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TransactionExporter {

    suspend fun exportToCsv(
        context: Context,
        transactions: List<Transaction>,
        fileName: String? = null
    ): Uri = withContext(Dispatchers.IO) {
        val safeName = fileName ?: defaultFileName()
        val csvString = buildCsv(transactions)
        val csvBytes = csvString.toByteArray(Charset.forName("UTF-8"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, safeName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(collection, values) ?: error("Failed to create file")
            resolver.openOutputStream(uri).use { out ->
                requireNotNull(out) { "Failed to open output stream" }

                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                out.write(csvBytes)
                out.flush()
            }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            uri
        } else {
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
            val file = File(dir, safeName)
            file.outputStream().use { out ->
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                out.write(csvBytes)
                out.flush()
            }
            Uri.fromFile(file)
        }
    }

    private fun defaultFileName(): String {
        val now = LocalDateTime.now()
        val stamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.US))
        return "transactions_$stamp.csv"
    }

    private fun buildCsv(list: List<Transaction>): String = buildString {
        appendLine("date,amount,category,note")
        list.forEach { t ->
            appendLine("${escape(t.date)},${t.amount},${escape(t.category)},${escape(t.note ?: "")}")
        }
    }

    private fun escape(s: String): String {
        val needsQuote = s.contains('"') || s.contains(',') || s.contains('\n') || s.contains('\r')
        val esc = s.replace("\"", "\"\"")
        return if (needsQuote) "\"$esc\"" else esc
    }
}
