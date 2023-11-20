package com.example.data.source

import com.example.data.model.sources.MediaDescription

interface MediaSource {

    suspend fun getMediaDescription(uuid: String, mediaId: String, type: String): MediaDescription?

    suspend fun addPhoto(uuid: String, description: String, isAvatar: Boolean): String

    suspend fun addVideo(uuid: String, name: String, description: String): String
}