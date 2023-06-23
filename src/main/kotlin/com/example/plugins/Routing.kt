package com.example.plugins

import com.example.data.chat.ChatController
import com.example.data.room.RoomController
import com.example.data.users.UserController
import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    val chatController by inject<ChatController>()
    val userController by inject<UserController>()
    install(Routing) {
        chats(chatController)
        chatSocketRoute(roomController)
        messages(roomController)
        signup(userController)
    }
}
