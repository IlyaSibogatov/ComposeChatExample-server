package com.example.data.room

import com.example.data.model.Message
import com.example.data.source.MessageDataSource
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    private lateinit var chatId: String

    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession,
        chatId: String,
    ) {
        if (members.containsKey(username)) throw MemberAlreadyExistsException()
        members[username] = Member(
            username = username,
            sessionId = sessionId,
            socket = socket,
            chatId = chatId
        )
        this.chatId = chatId
    }

    suspend fun sendMessage(
        senderUsername: String,
        message: String,
    ) {
        val fromChat = members.values.find { it.username == senderUsername }?.chatId

        val messageEntity = Message(
            message = message,
            username = senderUsername,
            timestamp = System.currentTimeMillis(),
        )
        val parsedMessage = Json.encodeToString(messageEntity)

        members.values.forEach { member ->
            if (member.username == messageEntity.username)
                messageDataSource.insertMessage(member.chatId, msg = messageEntity)
            if (member.chatId == fromChat)
                member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> = messageDataSource.getAllMessages(chatId) ?: emptyList()

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) members.remove(username)
    }
}