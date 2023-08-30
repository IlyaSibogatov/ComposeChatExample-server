package com.example.data.source

import com.example.data.model.user.Friend
import com.example.data.model.user.NewUserInfo
import com.example.data.model.user.User
import com.example.data.model.user.UserFromId
import com.example.utils.Constants
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSourceImpl(
    private val db: CoroutineDatabase,
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
                username = user.username,
                selfInfo = user.selfInfo,
                onlineStatus = user.onlineStatus,
                lastActionTime = user.lastActionTime,
                friends = friends.sortedByDescending { it.onlineStatus }.take(5),
                followers = user.followers,
                friendshipRequests = user.friendshipRequests,
            )
        }
        return null
    }

    override suspend fun friendshipRequest(selfId: String, userId: String): Boolean {
        var result = false
        val selfAccount = users.find(User::id eq selfId).first()
        val userAccount = users.find(User::id eq userId).first()
        if (selfAccount != null && userAccount != null) {
            if (selfAccount.friendshipRequests.contains(userId)) {
                selfAccount.friendshipRequests.remove(userId)
                selfAccount.friends.add(userId)
                userAccount.friends.add(selfId)
                users.updateOne(User::id eq selfId, selfAccount)
                users.updateOne(User::id eq userId, userAccount)
            } else {
                userAccount.followers.add(selfId)
                userAccount.friendshipRequests.add(selfId)
                users.updateOne(User::id eq userId, userAccount)
            }
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
            }
            myAccount.friendshipRequests.remove(userId)
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
            if (!selfRemoving) {
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
                    it.friendshipRequests.forEach { uid ->
                        list.add(getFriend(uid)!!)
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
}