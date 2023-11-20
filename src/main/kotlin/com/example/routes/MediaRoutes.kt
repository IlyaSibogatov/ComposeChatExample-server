package com.example.routes

import com.example.data.controllers.MediaController
import com.example.data.response.DefaultResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.media(mediaController: MediaController) {
    get(path = "/media_description") {
        val uid = call.parameters["userId"]
        val mediaId = call.parameters["mediaId"]
        val type = call.parameters["type"]

        mediaController.getMediaDescription(uuid = uid!!, mediaId = mediaId!!, type = type!!)?.let {
            call.respond(
                HttpStatusCode.OK,
                it
            )
        } ?: call.respond(
            HttpStatusCode.NoContent,
            ""
        )
    }

    post(path = "/upload_video") {
        val uid = call.parameters["userId"]
        val name = call.parameters["name"]
        val description = call.parameters["description"]
        val multipart = call.receiveMultipart()
        try {
            mediaController.addVideo(uid!!, name!!, description!!).let {
                val staticFolder = File("static/")
                val uploadsFolder = File("static/uploads/")
                val videoFolder = File("static/uploads/upload_videos/")
                val userFolder = File("static/uploads/upload_videos/$uid/")

                if (!staticFolder.exists()) staticFolder.mkdir()
                if (!uploadsFolder.exists()) uploadsFolder.mkdir()
                if (!videoFolder.exists()) videoFolder.mkdir()
                if (!userFolder.exists()) userFolder.mkdir()

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            when (part.name) {
                                "video" -> {
                                    val file = File("static/uploads/upload_videos/$uid/$it.mp4")
                                    part.streamProvider().use { input ->
                                        file.outputStream().buffered().use {
                                            input.copyTo(it)
                                        }
                                    }
                                }

                                "thumbnail" -> {
                                    val file = File("static/uploads/upload_videos/$uid/$it.jpeg")
                                    part.streamProvider().use { input ->
                                        file.outputStream().buffered().use {
                                            input.copyTo(it)
                                        }
                                    }
                                }
                            }
                            part.dispose
                        }

                        else -> {}
                    }
                }
            }
            call.respond(
                DefaultResponse(
                    "",
                    HttpStatusCode.OK.value,
                )
            )
        } catch (e: Exception) {
            call.respond(
                DefaultResponse(
                    "",
                    HttpStatusCode.NoContent.value,
                )
            )
        }
    }
    post(path = "/upload_photo") {
        val uid = call.parameters["userId"]
        val description = call.parameters["description"]
        val isAvatar = call.parameters["isAvatar"].toBoolean()
        val multipart = call.receiveMultipart()
        try {
            mediaController.addPhoto(uid!!, description ?: "", isAvatar).let { photoId ->
                val staticFolder = File("static/")
                val uploadsFolder = File("static/uploads/")
                val photoFolder = File("static/uploads/upload_photos/")
                val userFolder = File("static/uploads/upload_photos/$uid/")

                if (!staticFolder.exists()) staticFolder.mkdir()
                if (!uploadsFolder.exists()) uploadsFolder.mkdir()
                if (!photoFolder.exists()) photoFolder.mkdir()
                if (!userFolder.exists()) userFolder.mkdir()

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val file = File("static/uploads/upload_photos/$uid/$photoId.jpeg")
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use {
                                input.copyTo(it)
                            }
                        }
                        part.dispose
                    }
                }
            }
            call.respond(
                DefaultResponse(
                    "",
                    HttpStatusCode.OK.value,
                )
            )
        } catch (e: Exception) {
            call.respond(
                DefaultResponse(
                    "",
                    HttpStatusCode.NoContent.value,
                )
            )
        }
    }
}