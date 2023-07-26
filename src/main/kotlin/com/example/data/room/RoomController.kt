package com.example.data.room

import com.example.data.model.chat.Member
import com.example.data.model.chat.Message
import com.example.data.source.MessageDataSource
import com.example.utils.customexceptions.MemberAlreadyExistsException
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()


    fun onJoin(
        username: String,
        userId: String,
        sessionId: String,
        socket: WebSocketSession,
        chatId: String,
    ) {
        if (members.containsKey(username)) throw MemberAlreadyExistsException()
        members[username] = Member(
            username = username,
            userId = userId,
            sessionId = sessionId,
            socket = socket,
            chatId = chatId
        )
    }

    suspend fun sendMessage(
        senderUsername: String,
        senderId: String,
        message: String,
    ) {
        val fromChat = members.values.find { it.username == senderUsername }?.chatId

        val messageEntity = Message(
            message = message,
            username = senderUsername,
            userId = senderId,
            timestamp = System.currentTimeMillis(),
            wasEdit = false,
        )
        val parsedMessage = Json.encodeToString(messageEntity)

        members.values.forEach { member ->
            if (member.username == senderUsername) {
                when {
                    (message.startsWith(REMOVE_MESSAGE_ROUTE)) -> {
                        messageDataSource.removeMessage(
                            chatId = member.chatId,
                            id = message.replace(REMOVE_MESSAGE_ROUTE, EMPTY_CHAR)
                        )
                    }
                    (message.startsWith(EDIT_MESSAGE_ROUTE)) -> {
                        val splittedMessage = message.split(SPLITTER)
                        messageDataSource.editMessage(
                            chatId = member.chatId,
                            id = splittedMessage[0].replace(EDIT_MESSAGE_ROUTE, EMPTY_CHAR),
                            msg = splittedMessage[1]
                        )
                    }
                    else -> {
                        messageDataSource.insertMessage(member.chatId, msg = messageEntity)
                    }
                }
            }
            if (member.chatId == fromChat)
                member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(chatId: String): List<Message> =
        messageDataSource.getAllMessages(chatId) ?: emptyList()

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) members.remove(username)
    }

    companion object {
        const val EDIT_MESSAGE_ROUTE = "update_message_with_id="
        const val REMOVE_MESSAGE_ROUTE = "remove_message_with_id="
        const val SPLITTER = "/"
        const val EMPTY_CHAR = ""
    }
}