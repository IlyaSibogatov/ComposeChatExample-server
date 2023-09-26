package com.example.data.source

import com.example.data.model.chat.Chat
import com.example.data.model.chat.Message
import com.example.data.model.chat.MessageHistory
import com.example.data.model.user.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class ChatDataSourceImpl(
    private val db: CoroutineDatabase,
) : ChatDataSource {

    private val chats = db.getCollection<Chat>()
    private val messageHistory = db.getCollection<MessageHistory>()
    private val users = db.getCollection<User>()

    override suspend fun getAllChats(): List<Chat> {
        val list = chats.find()
            .descendingSort(Chat::timestamp).toList()
        list.map { chat ->
            chat.owner = users.find(User::id eq chat.ownerId).first()!!.username
        }
        return list
    }

    override suspend fun createChat(chat: Chat): Boolean {
        chats.find(Chat::ownerId eq chat.ownerId).first().let {
            if (it == null) {
                chats.insertOne(chat)
                messageHistory.insertOne(
                    MessageHistory(
                        name = chat.id,
                        users = listOf(chat.ownerId),
                        messages = listOf()
                    )
                )
                return true
            } else return false
        }
    }

    override suspend fun updateChat(chat: Chat): Boolean {
        chats.find(Chat::ownerId eq chat.ownerId).first()?.let {
            val updatedChat = Chat(
                id = it.id,
                name = chat.name,
                password = chat.password ?: "",
                owner = it.owner,
                ownerId = it.ownerId,
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
            messageHistory.deleteOne(MessageHistory::name eq chatId)
            messagesList.drop()
            return true
        } ?: return false
    }
}