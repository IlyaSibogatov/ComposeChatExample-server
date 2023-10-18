package com.example.data.chat

import com.example.data.model.chat.Chat
import com.example.data.model.chat.ChatDTO
import com.example.data.source.ChatDataSource

class ChatController(
    private val chatDataSource: ChatDataSource,
) {

    suspend fun getAllChats(page: Int, limit: Int): List<Chat> = chatDataSource.getAllChats(page, limit)

    suspend fun createChat(chat: ChatDTO): Chat? {
        val chatEntity = Chat(
            name = chat.name,
            password = chat.password,
            timestamp = System.currentTimeMillis(),
            owner = chat.owner,
            ownerId = chat.ownerId,
        )
        chatDataSource.createChat(chatEntity).let {
            return it
        }
    }

    suspend fun updateChat(chat: ChatDTO): Chat? {
        val chatEntity = Chat(
            name = chat.name,
            password = chat.password,
            timestamp = System.currentTimeMillis(),
            owner = chat.owner,
            ownerId = chat.ownerId,
        )
        chatDataSource.updateChat(chatEntity).let {
            return it
        }
    }

    suspend fun removeChat(chatId: String): Boolean {
        return chatDataSource.removeChat(chatId)
    }
}