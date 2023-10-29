package com.example.data.source

import com.example.data.model.NotificationModel
import com.example.data.model.NotificationType
import com.example.data.model.user.*
import com.example.firebase.sendMessage
import com.example.utils.Constants
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSourceImpl(
    db: CoroutineDatabase,
) : UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun getUserById(uid: String): UserFromId? {
        users.find(
            User::id eq uid
        ).first()?.let { user ->
            val friends = mutableListOf<Friend>()
            user.friends.forEach { userId ->
                getFriend(userId)?.let { user ->
                    friends.add(
                        Friend(
                            id = user.id,
                            username = user.username,
                            onlineStatus = user.onlineStatus,
                        )
                    )
                }
            }
            return UserFromId(
                id = user.id,
                avatarId = user.avatarId,
                username = user.username,
                selfInfo = user.selfInfo,
                onlineStatus = user.onlineStatus,
                lastActionTime = user.lastActionTime,
                friends = friends.sortedByDescending { it.onlineStatus }.take(5).toMutableList(),
                followers = user.followers,
                friendshipRequests = user.friendshipRequests,
                listPhotos = user.listPhotos,
                listVideos = user.listVideos,
            )
        }
        return null
    }

    override suspend fun friendshipRequest(selfId: String, userId: String): Boolean {
        var result = false
        val selfAccount = users.find(User::id eq selfId).first()
        val userAccount = users.find(User::id eq userId).first()
        val newRequest = FriendShipRequest(uuid = selfId)
        if (selfAccount != null && userAccount != null) {
            if (selfAccount.friendshipRequests.find { it.uuid == userId } != null) {
                val oldRequest = selfAccount.friendshipRequests.find { it.uuid == userId }
                selfAccount.followers.remove(userId)
                selfAccount.friendshipRequests.remove(
                    oldRequest
                )
                userAccount.friends.add(selfId)
                selfAccount.friends.add(userId)
                sendMessage(
                    NotificationModel(
                        title = "${selfAccount.username} accept your friendship request",
                        body = "Open app for looking it",
                        token = userAccount.tokenFcm ?: ""
                    ),
                    id = newRequest.id,
                    type = NotificationType.USER_ACCEPT_FRIENDSHIP,
                    senderId = selfId,
                    senderName = selfAccount.username,
                ).let {
                    userAccount.notifications.add(
                        0,
                        UserNotification(
                            type = NotificationType.USER_ACCEPT_FRIENDSHIP,
                            senderId = selfId,
                            senderName = selfAccount.username,
                            id = newRequest.id
                        )
                    )
                    selfAccount.notifications.find {
                        it.senderId == userId && it.type == NotificationType.REQUEST_FRIENDSHIP
                    }?.type = NotificationType.ACCEPTED_FRIENDSHIP
                }
            } else {
                if (!userAccount.followers.contains(selfId))
                    userAccount.followers.add(selfId)
                userAccount.friendshipRequests.add(FriendShipRequest(selfId))
                sendMessage(
                    NotificationModel(
                        title = "${selfAccount.username} send you friendship request",
                        body = "Open app for looking it",
                        token = userAccount.tokenFcm ?: ""
                    ),
                    id = newRequest.id,
                    type = NotificationType.REQUEST_FRIENDSHIP,
                    senderId = selfId,
                    senderName = selfAccount.username,
                ).let {
                    userAccount.notifications.add(
                        0,
                        UserNotification(
                            type = NotificationType.REQUEST_FRIENDSHIP,
                            senderId = selfId,
                            senderName = selfAccount.username,
                            id = newRequest.id
                        )
                    )
                }
            }
            users.updateOne(User::id eq selfId, selfAccount)
            users.updateOne(User::id eq userId, userAccount)
            result = true
        }
        return result
    }

    override suspend fun acceptFriendship(selfId: String, userId: String, accept: Boolean) {
        val myAccount = users.find(User::id eq selfId).first()
        val userAccount = users.find(User::id eq userId).first()
        if (myAccount != null && userAccount != null) {
            if (accept && !myAccount.friends.contains(userId)) {
                myAccount.friends.add(userId)
                userAccount.friends.add(selfId)
                val newId = ObjectId().toString()
                sendMessage(
                    NotificationModel(
                        title = "${myAccount.username} accept your friendship request",
                        body = "Open app for looking it",
                        token = userAccount.tokenFcm ?: ""
                    ),
                    id = newId,
                    type = NotificationType.USER_ACCEPT_FRIENDSHIP,
                    senderId = selfId,
                    senderName = myAccount.username,
                ).let {
                    userAccount.notifications.add(
                        0,
                        UserNotification(
                            type = NotificationType.USER_ACCEPT_FRIENDSHIP,
                            senderId = selfId,
                            senderName = myAccount.username,
                            id = newId,
                        )
                    )
                    myAccount.notifications.find {
                        it.senderId == userId && it.type == NotificationType.REQUEST_FRIENDSHIP
                    }?.type = NotificationType.ACCEPTED_FRIENDSHIP
                }
            }
            if (!accept && !myAccount.friends.contains(userId)) {
                val newId = BsonId().toString()
                sendMessage(
                    NotificationModel(
                        title = "${myAccount.username} declined your friendship request",
                        body = "Open app for looking it",
                        token = userAccount.tokenFcm ?: ""
                    ),
                    id = newId,
                    type = NotificationType.USER_DECLINED_FRIENDSHIP,
                    senderId = selfId,
                    senderName = myAccount.username,
                ).let {
                    userAccount.notifications.add(
                        0,
                        UserNotification(
                            type = NotificationType.USER_DECLINED_FRIENDSHIP,
                            senderId = selfId,
                            senderName = myAccount.username,
                            id = newId,
                        )
                    )
                    myAccount.notifications.find {
                        it.senderId == userId && it.type == NotificationType.REQUEST_FRIENDSHIP
                    }?.type = NotificationType.DECLINED_FRIENDSHIP
                }
            }
            myAccount.friendshipRequests.remove(
                myAccount.friendshipRequests.find { it.uuid == userId }
            )
            myAccount.followers.remove(userId)
            users.updateOne(User::id eq selfId, myAccount)
            users.updateOne(User::id eq userId, userAccount)
        }
    }

    override suspend fun removeFromFriends(selfId: String, userId: String, selfRemoving: Boolean) {
        val myAccount = users.find(User::id eq selfId).first()
        val userAccount = users.find(User::id eq userId).first()
        if (myAccount != null && userAccount != null) {
            myAccount.friends.remove(userId)
            userAccount.friends.remove(selfId)
            if (!selfRemoving && myAccount.followers.find { it == userId } == null) {
                myAccount.followers.add(userId)
            }
            users.updateOne(User::id eq selfId, myAccount)
            users.updateOne(User::id eq userId, userAccount)
        }
    }

    override suspend fun getFollowerFriends(uid: String, type: String): List<Friend> {
        val list = mutableListOf<Friend>()
        users.find(User::id eq uid).first()?.let {
            when (type) {
                Constants.FRIENDS -> {
                    it.friends.forEach { uid ->
                        list.add(getFriend(uid)!!)
                    }
                }

                Constants.FOLLOWERS -> {
                    it.followers.forEach { uid ->
                        list.add(getFriend(uid)!!)
                    }
                }

                Constants.FRIENDSHIPS_REQUESTS -> {
                    it.friendshipRequests.forEach { request ->
                        list.add(getFriend(request.uuid)!!)
                    }
                }
            }
        }
        return list
    }

    private suspend fun getFriend(id: String): Friend? {
        users.find(User::id eq id).first()?.let {
            return Friend(
                id = it.id,
                username = it.username,
                onlineStatus = it.onlineStatus,
            )
        } ?: return null
    }

    override suspend fun updateUser(newInfo: NewUserInfo): Boolean {
        val user = users.find(User::id eq newInfo.id).first()
        val userWithSameName = users.find(User::username eq newInfo.username).first()
        return if (userWithSameName == null && user != null || (userWithSameName?.id == user?.id)) {
            user?.let {
                it.username = newInfo.username
                it.selfInfo = newInfo.selfInfo
                it.lastActionTime = System.currentTimeMillis()
                users.updateOne(User::id eq newInfo.id, it)
            }
            true
        } else false
    }

    override suspend fun updateToken(uuid: String, newToken: String, deviceId: String, deviceType: String) {
        val user = users.find(User::id eq uuid).first()
        user?.let {
            it.tokenFcm = newToken
            it.deviceId = deviceId
            it.deviceType = deviceType
            users.updateOne(User::id eq uuid, it)
        }
    }

    override suspend fun getNotifications(uuid: String): List<UserNotification>? {
        var result: List<UserNotification>? = null
        val user = users.find(User::id eq uuid).first()
        user?.let {
            result = it.notifications
            return result
        } ?: return null
    }
}