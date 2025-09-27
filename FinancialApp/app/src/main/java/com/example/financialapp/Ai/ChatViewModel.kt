package com.example.financialapp.Ai

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financialapp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy { mutableStateListOf<MessageModel>() }

    val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )


    fun sendMessage(question: String) {
        if (question.isBlank()) return

        viewModelScope.launch {
            // user message
            messageList.add(MessageModel(question, "user"))
            // placeholder
            messageList.add(MessageModel("Typing...", "model"))

            try {
                val history = messageList
                    .filter { it.role == "user" || it.role == "model" }
                    .map { m ->
                        content(role = m.role) { text(m.message) }
                    }

                val chat = generativeModel.startChat(history = history)

                val response = chat.sendMessage(question)
                val text = response.text.orEmpty().ifBlank { "…no text returned…" }

                // remove the typing bubble if it exists
                if (messageList.isNotEmpty() && messageList.last().message == "Typing...") {
                    messageList.removeAt(messageList.lastIndex)
                }
                messageList.add(MessageModel(text, "model"))
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Gemini error", e)
                if (messageList.isNotEmpty() && messageList.last().message == "Typing...") {
                    messageList.removeAt(messageList.lastIndex)
                }
                messageList.add(
                    MessageModel(
                        "Error: ${e.message ?: "Unknown error"}",
                        "model"
                    )
                )
            }
        }
    }
}
