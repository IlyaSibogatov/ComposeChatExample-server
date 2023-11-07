package com.example.data.source

import com.example.data.model.sources.PhotoItem
import com.example.data.model.sources.VideoItem
import com.example.data.model.user.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MediaSourceImpl(
    db: CoroutineDatabase,
) : MediaSource {

    private val users = db.getCollection<User>()

    override suspend fun addPhoto(uuid: String, description: String, isAvatar: Boolean): String {
        val user = users.find(User::id eq uuid).first()
        user?.let {
            val photo = PhotoItem(
                description = description
            )
            it.listPhotos.add(0, photo)
            if (isAvatar) it.avatarId = photo.id
            users.updateOne(User::id eq uuid, it)
            return photo.id
        } ?: return ""
    }

    override suspend fun addVideo(uuid: String, name: String, description: String): String {
        val user = users.find(User::id eq uuid).first()
        user?.let {
            val video = VideoItem(
                name = name,
                description = description,
            )
            it.listVideos.add(0, video)
            users.updateOne(User::id eq uuid, it)
            return video.id
        } ?: return ""
    }
}