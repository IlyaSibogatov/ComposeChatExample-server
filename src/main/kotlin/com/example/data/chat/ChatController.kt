package com.example.data.chat

import com.example.data.model.chat.Chat
import com.example.data.model.chat.ChatDTO
import com.example.data.source.ChatDataSource

class ChatController(
    private val chatDataSource: ChatDataSource
) {

    suspend fun getAllChats(): List<Chat> = chatDataSource.getAllChats()

    suspend fun createChat(chat: ChatDTO): Boolean {
        val chatEntity = Chat(
            name = chat.name,
            password = chat.password,
            owner = chat.owner,
            timestamp = System.currentTimeMillis(),
        )
        chatDataSource.createChat(chatEntity).let {
            return it
        }
    }
}