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
                        status = HttpStatusCode.OK.value,
                    )
                )
            } ?: call.respond(
                DefaultResponse(
                    msg = "NaN",
                    status = HttpStatusCode.NoContent.value,
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
                        status = HttpStatusCode.OK.value,
                    )
                )
            } ?: call.respond(
                DefaultResponse(
                    msg = "NaN",
                    status = HttpStatusCode.NoContent.value,
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
                    status = HttpStatusCode.OK.value,
                )
            )
            else call.respond(
                DefaultResponse(
                    msg = "Logout failed",
                    status = HttpStatusCode.NoContent.value,
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
    post(path = "/addFriend") {
        val selfId = call.parameters["selfId"]
        val friendId = call.parameters["userId"]
        userController.addToFriends(selfId!!, friendId!!).let {
            if (it) call.respond(
                DefaultResponse(
                    msg = "Add success",
                    status = HttpStatusCode.OK.value,
                )
            )
            else call.respond(
                DefaultResponse(
                    msg = "Add failed",
                    status = HttpStatusCode.NoContent.value,
                )
            )
        }
    }
    get(path = "/getFollowerFriends") {
        val uid = call.parameters["uid"]
        val type = call.parameters["type"]
        userController.getFollowerFriends(uid!!, type!!).let {
            call.respond(
                HttpStatusCode.OK,
                it
            )
        }
    }
    post(path = "/friendship") {
        val selfId = call.parameters["selfId"]
        val userId = call.parameters["userId"]
        val action = call.parameters["accept"].toBoolean()

        userController.friendshipAccept(selfId!!, userId!!, action)
        call.respond(
            DefaultResponse(
                msg = "Action success",
                status = HttpStatusCode.OK.value,
            )
        )
    }
}