package com.example.data.source

import com.example.data.model.chat.Message
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

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

    override suspend fun editMessage(chatId: String, id: String, msg: String) {
        messages = db.getCollection<Message>(collectionName = chatId)
        val message = messages.find(Message::id eq id).toList().first()
        message.apply {
            this.wasEdit = this.message != msg
            this.message = msg
        }
        messages.updateOne(Message::id eq id, message)
    }

    override suspend fun removeMessage(chatId: String, id: String) {
        messages = db.getCollection<Message>(collectionName = chatId)
        messages.deleteOne(Message::id eq id)
    }
}