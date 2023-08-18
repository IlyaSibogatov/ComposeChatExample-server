package com.example.routes

import com.example.data.chat.ChatController
import com.example.data.controllers.RoomController
import com.example.data.model.chat.ChatDTO
import com.example.data.response.DefaultResponse
import com.example.session.ChatSession
import com.example.utils.customexceptions.MemberAlreadyExistsException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.chatSocketRoute(roomController: RoomController) {
    webSocket(path = "/chat-socket") {
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        try {
            roomController.onJoin(
                username = session.username,
                userId = session.userId,
                sessionId = session.sessionId,
                socket = this,
                chatId = session.chatId
            )
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    roomController.sendMessage(
                        senderUsername = session.username,
                        senderId = session.userId,
                        message = frame.readText(),
                    )
                }
            }
        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.tryDisconnect(session.username)
        }
    }
}

fun Route.messages(roomController: RoomController) {
    get(path = "/messages") {
        val chatId = call.parameters["chatId"]
        call.respond(
            HttpStatusCode.OK,
            roomController.getAllMessages(chatId!!)
        )
    }
    get(path = "/followers") {
        val chatId = call.parameters["chatId"]
        call.respond(
            HttpStatusCode.OK,
            roomController.getFollowers(chatId!!)
        )
    }
}

fun Route.chats(chatController: ChatController) {
    get(path = "/chats") {
        call.respond(
            HttpStatusCode.OK,
            chatController.getAllChats()
        )
    }
    post(path = "/create") {
        val chat = call.receive<ChatDTO>()
        chatController.createChat(chat).let {
            if (it) call.respond(
                DefaultResponse(
                    msg = "Chat has been created",
                    status = HttpStatusCode.OK.value
                )
            )
            else call.respond(
                DefaultResponse(
                    msg = "You already have chat",
                    status = HttpStatusCode.NoContent.value
                )
            )
        }
    }
    post(path = "/update") {
        val chat = call.receive<ChatDTO>()
        chatController.updateChat(chat).let {
            if (it) call.respond(
                DefaultResponse(
                    msg = "Chat was updated",
                    status = HttpStatusCode.OK.value,
                )
            )
            else call.respond(
                DefaultResponse(
                    msg = "Chat update exception",
                    status = HttpStatusCode.NoContent.value,
                )
            )
        }
    }
    post(path = "/delete") {
        val chatId = call.parameters["chatId"]
        chatId?.let { chatid ->
            chatController.removeChat(chatid).let { repsonse ->
                if (repsonse) {
                    call.respond(
                        HttpStatusCode.OK,
                        true
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NoContent,
                        false
                    )
                }
            }
        } ?: call.respond(
            HttpStatusCode.NoContent,
            false
        )
    }
}