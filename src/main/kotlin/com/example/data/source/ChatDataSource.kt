package com.example.data.source

import com.example.data.model.chat.Chat

interface ChatDataSource {
    suspend fun getAllChats(page: Int, limit: Int): List<Chat>

    suspend fun createChat(chat: Chat): Chat?

    suspend fun updateChat(chat: Chat): Chat?

    suspend fun removeChat(chatId: String): Boolean
}