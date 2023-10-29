package com.example.routes

import com.example.data.controllers.UserController
import com.example.data.model.user.NewUserInfo
import com.example.data.response.DefaultResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userController: UserController) {
    get(path = "/get_user") {
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
    post(path = "/add_friend") {
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
    get(path = "/get_follower_friends") {
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
    post(path = "/remove_friend") {
        val selfId = call.parameters["selfId"]
        val userId = call.parameters["userId"]
        val selfRemoving = call.parameters["selfRemoving"].toBoolean()

        userController.removeFromFriends(selfId!!, userId!!, selfRemoving)
        call.respond(
            DefaultResponse(
                msg = "friend removed",
                status = HttpStatusCode.OK.value
            )
        )
    }
    post(path = "/update_user") {
        val newInfo = call.receive<NewUserInfo>()
        userController.updateUser(newInfo).let {
            if (it) call.respond(
                DefaultResponse(
                    msg = "Update success",
                    status = 200,
                )
            )
            else call.respond(
                DefaultResponse(
                    msg = "Update failed",
                    status = 204,
                )
            )
        }
    }
    post(path = "/update_token") {
        val uuid = call.parameters["uuid"]
        val token = call.parameters["token"]
        val deviceId = call.parameters["deviceId"]
        val deviceType = call.parameters["deviceType"]
        userController.updateToken(uuid!!, token!!, deviceId!!, deviceType!!)
        call.respond(
            DefaultResponse(
                "",
                HttpStatusCode.OK.value
            )
        )
    }
    get(path = "/notifications") {
        val uuid = call.parameters["uuid"]
        userController.getNotifications(uuid!!)?.let {
            call.respond(
                HttpStatusCode.OK,
                it
            )
        } ?: run {
            call.respond(
                HttpStatusCode.NoContent,
                "List is empty"
            )
        }
    }
}