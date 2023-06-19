package com.example.data.source

import com.example.data.model.Chat

interface ChatDataSource {
    suspend fun getAllChats(): List<Chat>

    suspend fun createChat(chat: Chat): Boolean
}