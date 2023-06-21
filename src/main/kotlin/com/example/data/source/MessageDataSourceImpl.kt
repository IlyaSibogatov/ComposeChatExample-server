package com.example.data.source

import com.example.data.model.chat.Message
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageDataSourceImpl(
    private val db: CoroutineDatabase
) : MessageDataSource {


    private lateinit var messages: CoroutineCollection<Message>

    override suspend fun getAllMessages(chatId: String): List<Message> {
        messages = db.getCollection<Message>(collectionName = chatId)
        return messages.find()
            .descendingSort(Message::timestamp).toList()
    }

    override suspend fun insertMessage(chatId: String, msg: Message) {
        messages = db.getCollection<Message>(collectionName = chatId)
        messages.insertOne(msg)
    }
}