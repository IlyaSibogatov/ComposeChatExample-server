package com.example.data.source

import com.example.data.model.chat.Chat
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.util.idValue

class ChatDataSourceImpl(
    private val db: CoroutineDatabase
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
        chats.find(Chat::id eq chat.id).first()?.let {
            val updatedChat = Chat(
                id =  it.id,
                name = chat.name,
                password = chat.password,
                owner = it.owner,
                timestamp = it.timestamp,
            )
            chats.updateOne(Chat::id eq chat.id, updatedChat)
            return true
        } ?: return false
    }

    override suspend fun removeChat(chatId: String): Boolean {
        val chat = chats.find(Chat::id eq chatId).first()
        chat?.let {
            chats.deleteOne(Chat::id eq chatId)
            return true
        } ?: return false
    }
}