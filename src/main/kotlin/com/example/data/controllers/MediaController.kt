package com.example.data.controllers

import com.example.data.model.sources.MediaDescription
import com.example.data.source.MediaSource

class MediaController(
    private val mediaSource: MediaSource,
) {
    suspend fun getMediaDescription(uuid: String, mediaId: String, type: String): MediaDescription? =
        mediaSource.getMediaDescription(uuid, mediaId, type)
    suspend fun addPhoto(uuid: String, description: String, isAvatar: Boolean): String =
        mediaSource.addPhoto(uuid, description, isAvatar)

    suspend fun addVideo(uuid: String, name: String, description: String): String =
        mediaSource.addVideo(uuid, name, description)
}