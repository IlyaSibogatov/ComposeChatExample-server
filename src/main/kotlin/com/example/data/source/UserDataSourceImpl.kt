package com.example.data.source

import com.example.data.model.user.*
import com.example.utils.Constants.FOLLOWERS
import com.example.utils.Constants.FRIENDS
import com.example.utils.Constants.FRIENDSHIPS_REQUESTS
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.mindrot.jbcrypt.BCrypt

class UserDataSourceImpl(
    private val db: CoroutineDatabase,
) : UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun regUser(userCredentials: UserDTO): String? {
        return if (users.find(User::username eq userCredentials.username).toList().isEmpty()) {
            users.insertOne(
                User(
                    username = userCredentials.username,
                    password = userCredentials.hashedPassword(),
                    selfInfo = "",
                    onlineStatus = true,
                    lastActionTime = System.currentTimeMillis(),
                    timestamp = System.currentTimeMillis(),
                    friends = mutableListOf(),
                    followers = mutableListOf(),
                    friendshipRequests = mutableListOf(),
                )
            )
            users.find(User::username eq userCredentials.username).first()?.id
        } else null
    }

    override suspend fun login(userCredentials: UserDTO): String? {
        val user = users.find(User::username eq userCredentials.username).first()
        return if (user != null) {
            if (BCrypt.checkpw(userCredentials.password, user.password)) {
                user.onlineStatus = true
                user.lastActionTime = System.currentTimeMillis()
                users.updateOne(User::username eq user.username, user)
                user.id
            } else ""
        } else ""
    }

    override suspend fun logout(uid: String): Boolean {
        val user = users.find(User::id eq uid).first()
        user?.let {
            it.onlineStatus = false
            it.lastActionTime = System.currentTimeMillis()
            users.updateOne(User::id eq uid, it)
            return true
        } ?: return false
    }

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
        val userAccount = users.find(User::id eq userId).first()
        return if (userAccount == null) false
        else {
            userAccount.followers.add(selfId)
            userAccount.friendshipRequests.add(selfId)
            users.updateOne(User::id eq userId, userAccount)
            true
        }
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
            users.updateOne(User::id eq selfId, myAccount)
            users.updateOne(User::id eq userId, userAccount)
        }
    }

    override suspend fun getFollowerFriends(uid: String, type: String): List<Friend> {
        val list = mutableListOf<Friend>()
        users.find(User::id eq uid).first()?.let {
            when (type) {
                FRIENDS -> {
                    it.friends.forEach { uid ->
                        list.add(getFriend(uid)!!)
                    }
                }

                FOLLOWERS -> {
                    it.followers.forEach { uid ->
                        list.add(getFriend(uid)!!)
                    }
                }

                FRIENDSHIPS_REQUESTS -> {
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
        user?.let {
            it.username = newInfo.username
            it.selfInfo = newInfo.selfInfo
            it.lastActionTime = System.currentTimeMillis()
            users.updateOne(User::id eq newInfo.id, it)
            return true
        } ?: return false
    }
}
