package com.example.data.source

import com.example.data.model.chat.Message

interface MessageDataSource {

    suspend fun getAllMessages(chatId: String): List<Message>?

    suspend fun insertMessage(chatId: String, msg: Message)
}