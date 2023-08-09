package com.example.data.source

import com.example.data.model.chat.Chat
import com.example.data.model.chat.Message
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class ChatDataSourceImpl(
    private val db: CoroutineDatabase,
) : ChatDataSource {

    private val chats = db.getCollection<Chat>()

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

    override suspend fun updateChat(chat: Chat): Boolean {
        chats.find(Chat::owner eq chat.owner).first()?.let {
            val updatedChat = Chat(
                id =  it.id,
                name = chat.name,
                password = chat.password,
                owner = it.owner,
                timestamp = it.timestamp,
            )
            chats.updateOne(Chat::id eq it.id, updatedChat)
            return true
        } ?: return false
    }

    override suspend fun removeChat(chatId: String): Boolean {
        val chat = chats.find(Chat::id eq chatId).first()
        val messagesList = db.getCollection<Message>(collectionName = chatId)
        chat?.let {
            chats.deleteOne(Chat::id eq chatId)
            messagesList.drop()
            return true
        } ?: return false
    }
}