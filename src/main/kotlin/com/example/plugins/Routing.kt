package com.example.plugins

import com.example.data.chat.ChatController
import com.example.data.room.RoomController
import com.example.routes.chatSocketRoute
import com.example.routes.chats
import com.example.routes.getAllMessages
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    val chatController by inject<ChatController>()
    install(Routing) {
        chats(chatController)
        chatSocketRoute(roomController)
        getAllMessages(roomController)
    }
}
