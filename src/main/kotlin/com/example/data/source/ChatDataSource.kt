package com.example.data.source

import com.example.data.model.chat.Chat

interface ChatDataSource {
    suspend fun getAllChats(): List<Chat>

    suspend fun createChat(chat: Chat): Boolean

    suspend fun updateChat(chat: Chat): Boolean
    suspend fun removeChat(chatId: String): Boolean
}