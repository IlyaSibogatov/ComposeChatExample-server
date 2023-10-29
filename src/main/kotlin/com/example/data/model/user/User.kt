package com.example.data.model.user

import com.example.data.model.NotificationType
import com.example.data.model.sources.PhotoItem
import com.example.data.model.sources.VideoItem
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    var username: String,
    var password: String,
    var avatarId: String,
    var selfInfo: String = "",
    var onlineStatus: Boolean,
    var lastActionTime: Long,
    val timestamp: Long,
    var friends: MutableList<String>,
    var followers: MutableList<String>,
    var friendshipRequests: MutableList<FriendShipRequest>,
    var notifications: MutableList<UserNotification>,
    var listPhotos: MutableList<PhotoItem>,
    var listVideos: MutableList<VideoItem>,
    var tokenFcm: String? = null,
    var deviceId: String? = null,
    var deviceType: String? = null,
    @BsonId
    val id: String = ObjectId().toString(),
)

@Serializable
data class UserFromId(
    val id: String,
    val avatarId: String,
    val username: String,
    val selfInfo: String,
    var onlineStatus: Boolean,
    var lastActionTime: Long,
    val friends: MutableList<Friend>,
    var listPhotos: MutableList<PhotoItem>,
    var listVideos: MutableList<VideoItem>,
    var followers: MutableList<String>,
    var friendshipRequests: MutableList<FriendShipRequest>,
)

@Serializable
data class UserChatInfo(
    val uuid: String,
    val username: String,
)

@Serializable
data class Friend(
    val id: String,
    val username: String,
    var onlineStatus: Boolean,
)

@Serializable
data class UserNotification(
    var type: NotificationType,
    val senderId: String,
    val senderName: String,
    val id: String,
)

@Serializable
data class FriendShipRequest(
    val uuid: String,
    @BsonId
    val id: String = ObjectId().toString(),
)