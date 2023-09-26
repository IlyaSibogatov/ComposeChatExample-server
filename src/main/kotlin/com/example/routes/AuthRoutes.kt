package com.example.routes

import com.example.data.controllers.AuthController
import com.example.data.model.user.UserDTO
import com.example.data.response.DefaultResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.signup(authController: AuthController) {
    post(path = "/signup") {
        val userCredentials = call.receive<UserDTO>()
        authController.regUser(userCredentials).let {
            it.let {
                call.respond(
                    DefaultResponse(
                        msg = it,
                        status = HttpStatusCode.OK.value,
                    )
                )
            }
        }
    }

    post(path = "/login") {
        val userCredentials = call.receive<UserDTO>()
        authController.login(userCredentials).let {
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
        authController.logout(uid!!).let {
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
    post(path = "/change_pass") {
        val uuid = call.parameters["uuid"]
        val current = call.parameters["current"]
        val new = call.parameters["new"]
        call.respond(
            authController.changePass(current!!, new!!, uuid!!)
        )
    }
    post(path = "/delete_acc") {
        val uuid = call.parameters["uuid"]
        authController.deleteAcc(uuid!!)
        val file = File("static/uploads_avatars/$uuid.jpeg")
        file.delete()
        call.respond(
            DefaultResponse(
                msg = "Account was deleted",
                status = HttpStatusCode.OK.value
            )
        )
    }
}