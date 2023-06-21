package com.example.routes

import com.example.data.model.user.UserDTO
import com.example.data.response.DefaultResponse
import com.example.data.users.UserController
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signup(userController: UserController) {
    post(path = "/signup") {
        val userCredentials = call.receive<UserDTO>()
        userController.regUser(userCredentials).let {
            if (it) call.respond(
                DefaultResponse(
                    msg = "Registration successful",
                    status = 200,
                )
            )
            else call.respond(
                DefaultResponse(
                    msg = "User with same name already exist",
                    status = 204,
                )
            )
        }
    }
    post(path = "/login") {
        val userCredentials = call.receive<UserDTO>()
        userController.login(userCredentials).let {
            if (it) call.respond(
                DefaultResponse(
                    msg = "Login success",
                    status = 200,
                )
            )
            else call.respond(
                DefaultResponse(
                    msg = "Login failed",
                    status = 204,
                )
            )
        }
    }
}