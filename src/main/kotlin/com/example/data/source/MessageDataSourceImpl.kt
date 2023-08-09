package com.example.data.source

import com.example.data.model.chat.Message
import com.example.data.model.chat.MessageHistory
import com.example.data.model.user.User
import com.example.data.model.user.UserChatInfo
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MessageDataSourceImpl(
    private val db: CoroutineDatabase
) : MessageDataSource {

    private var messageHistory = db.getCollection<MessageHistory>()
    private val users = db.getCollection<User>()


    override suspend fun addFollower(chatId: String, uid: String) {
        val currentHistory = messageHistory.find(MessageHistory::name eq chatId).first()
        val users = currentHistory?.users?.toMutableList() ?: mutableListOf()
        if (!users.contains(uid)) {
            users.add(uid)
            currentHistory?.let {
                it.users = users
                messageHistory.updateOne(MessageHistory::name eq chatId, currentHistory)
            }
        }
    }

    override suspend fun getAllMessages(chatId: String): List<Message> {
        val currentHistory = messageHistory.find(MessageHistory::name eq chatId).first()
        val messages = currentHistory?.messages
        return messages?.sortedByDescending { it.timestamp } ?: emptyList()
    }

    override suspend fun getFollowers(chatId: String): List<UserChatInfo> {
        val chatUsers = messageHistory.find(MessageHistory::name eq chatId).first()?.users
        val followers = chatUsers?.map { it ->
            users.find(User::id eq it).first().let { user ->
                UserChatInfo(
                    user?.id ?: "",
                    user?.username ?: "",
                )
            }
        }
        return followers ?: emptyList()
    }

    override suspend fun insertMessage(chatId: String, msg: Message) {
        val currentHistory = messageHistory.find(MessageHistory::name eq chatId).first()
        var messages = currentHistory?.messages
        messages = messages?.plus(msg) ?: emptyList<Message>().plus(msg)
        currentHistory?.messages = messages
        if (currentHistory != null) {
            messageHistory.updateOne(MessageHistory::name eq chatId, currentHistory)
        }
    }

    override suspend fun editMessage(chatId: String, id: String, msg: String) {
        val currentHistory = messageHistory.find(MessageHistory::name eq chatId).first()
        val messages = currentHistory?.messages
        messages?.find { it.id == id }?.let {
            it.wasEdit = it.message != msg
            it.message = msg
        }
        if (messages != null) {
            currentHistory.messages = messages
        }
        if (currentHistory != null) {
            messageHistory.updateOne(MessageHistory::name eq chatId, currentHistory)
        }
    }

    override suspend fun removeMessage(chatId: String, id: String) {
        val currentHistory = messageHistory.find(MessageHistory::name eq chatId).first()
        val messageForRemove = currentHistory?.messages?.find { it.id == id }
        val messages = currentHistory?.messages?.toMutableList()
        messages?.remove(messageForRemove)
        currentHistory?.messages = messages?.toList() ?: listOf()
        if (currentHistory != null) {
            messageHistory.updateOne(MessageHistory::name eq chatId, currentHistory)
        }
    }
}