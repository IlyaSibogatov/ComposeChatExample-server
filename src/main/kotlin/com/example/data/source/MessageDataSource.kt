package com.example.data.source

import com.example.data.model.chat.Message

interface MessageDataSource {

    suspend fun getAllMessages(chatId: String): List<Message>?
    suspend fun insertMessage(chatId: String, msg: Message)
    suspend fun editMessage(chatId: String, id: String, msg: String)
    suspend fun removeMessage(chatId: String, id: String)
}