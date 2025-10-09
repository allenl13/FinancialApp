package com.example.financialapp.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialapp.data.prefs.ProfilePrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel(context: Context) : ViewModel() {

    private val appContext = context.applicationContext
    private val prefs = ProfilePrefs(appContext)

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    init {

        viewModelScope.launch {
            prefs.imageUriFlow.collect { uriString ->
                _imageUri.value = uriString?.let { Uri.parse(it) }
            }
        }
    }


    fun onImageSelected(src: Uri) {
        viewModelScope.launch {
            val copied = copyToInternalStorage(appContext, src)
            if (copied != null) {
                _imageUri.value = copied
                prefs.saveImageUri(copied.toString())
            }
        }
    }


    private fun copyToInternalStorage(context: Context, src: Uri): Uri? {
        return try {
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val dest = File(context.filesDir, fileName)
            context.contentResolver.openInputStream(src)?.use { input ->
                FileOutputStream(dest).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(dest)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
