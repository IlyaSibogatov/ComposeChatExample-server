package com.example.data.source

interface MediaSource {

    suspend fun addPhoto(uuid: String, description: String, isAvatar: Boolean): String

    suspend fun addVideo(uuid: String, name: String, description: String): String
}