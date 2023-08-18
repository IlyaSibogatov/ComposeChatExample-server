package com.example.plugins

import com.example.data.chat.ChatController
import com.example.data.controllers.RoomController
import com.example.data.controllers.AuthController
import com.example.data.controllers.UserController
import com.example.routes.*
import com.example.routes.signup
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    val chatController by inject<ChatController>()
    val authController by inject<AuthController>()
    val userController by inject<UserController>()
    install(Routing) {
        static("/") {
            staticRootFolder = File("static/")
            static("/images"){
                files("uploads_avatars/")
            }
        }
        signup(authController)
        userRoutes(userController)
        chatSocketRoute(roomController)
        chats(chatController)
        messages(roomController)
    }
}
