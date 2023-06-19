package com.example.data.source

import com.example.data.model.Chat
import com.example.data.model.Message
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class ChatDataSourceImpl(
    private val db: CoroutineDatabase
) : ChatDataSource {

    private val chats = db.getCollection<Chat>()
    private val messageRoom = db.getCollection<Message>()

    override suspend fun getAllChats(): List<Chat> {
        return chats.find()
            .descendingSort(Chat::timestamp).toList()
    }

    override suspend fun createChat(chat: Chat): Boolean {
        return if (
            chats.find(Chat::owner eq chat.owner).toList().isEmpty()) {
            chats.insertOne(chat)
            db.createCollection(chat.id)
            true
        } else false
    }
}