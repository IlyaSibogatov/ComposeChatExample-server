package com.example.data.source

import com.example.data.model.user.User
import com.example.data.model.user.UserDTO
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.mindrot.jbcrypt.BCrypt

class AuthDataSourceImpl(
    private val db: CoroutineDatabase,
) : AuthDataSource {

    private val users = db.getCollection<User>()

    override suspend fun regUser(userCredentials: UserDTO): String {
        return if (users.find(User::username eq userCredentials.username).first() == null) {
            users.insertOne(
                User(
                    username = userCredentials.username,
                    password = userCredentials.hashedPassword(),
                    onlineStatus = true,
                    lastActionTime = System.currentTimeMillis(),
                    timestamp = System.currentTimeMillis(),
                    friends = mutableListOf(),
                    followers = mutableListOf(),
                    friendshipRequests = mutableListOf(),
                    notifications = mutableListOf(),
                )
            )
            users.find(User::username eq userCredentials.username).first()!!.id
        } else "username_exist"
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

    override suspend fun changePass(current: String, new: String, uuid: String): String {
        val user = users.find(User::id eq uuid).first()
        user?.let {
            var msg = ""
            when {
                !BCrypt.checkpw(current, user.password) -> msg = "current with old not same"
                BCrypt.checkpw(new, user.password) -> msg = "new with old same"
                else -> {
                    user.password = BCrypt.hashpw(new, BCrypt.gensalt())
                    users.updateOne(User::id eq uuid, user)
                    msg = "pass changed"
                }
            }
            return msg
        } ?: return "user not find"
    }
}
