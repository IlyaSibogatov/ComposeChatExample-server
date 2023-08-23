package com.example.data.source

import com.example.data.model.user.*
import com.example.utils.Constants.FOLLOWERS
import com.example.utils.Constants.FRIENDS
import com.example.utils.Constants.FRIENDSHIPS_REQUESTS
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.mindrot.jbcrypt.BCrypt

class AuthDataSourceImpl(
    private val db: CoroutineDatabase,
) : AuthDataSource {

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
            } else null
        } else null
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
}
