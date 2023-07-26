package com.example.routes

import com.example.data.model.user.UserDTO
import com.example.data.response.DefaultResponse
import com.example.data.users.UserController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signup(userController: UserController) {
    post(path = "/signup") {
        val userCredentials = call.receive<UserDTO>()
        userController.regUser(userCredentials).let {
            it?.let {
                call.respond(
                    DefaultResponse(
                        msg = it,
                        status = 200,
                    )
                )
            } ?: call.respond(
                DefaultResponse(
                    msg = "NaN",
                    status = 204,
                )
            )
        }
    }

    post(path = "/login") {
        val userCredentials = call.receive<UserDTO>()
        userController.login(userCredentials).let {
            it?.let {
                call.respond(
                    DefaultResponse(
                        msg = it,
                        status = 200,
                    )
                )
            } ?: call.respond(
                DefaultResponse(
                    msg = "NaN",
                    status = 204,
                )
            )
        }
    }
    post(path = "/logout") {
        val uid = call.parameters["uid"]
        userController.logout(uid!!).let {
            if (it) call.respond(
                DefaultResponse(
                    msg = "Logout success",
                    status = 200,
                )
            )
            else call.respond(
                DefaultResponse(
                    msg = "Logout failed",
                    status = 204,
                )
            )
        }
    }
    get(path = "/getUser") {
        val uid = call.parameters["uid"]
        userController.getUserById(uid!!)?.let {
            call.respond(
                HttpStatusCode.OK,
                it,
            )
        } ?: call.respond(
            HttpStatusCode.NoContent,
            "User not found"
        )
    }
}