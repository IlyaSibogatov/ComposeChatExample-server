package com.example.data.source

import com.example.data.model.user.User
import com.example.data.model.user.UserDTO
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.mindrot.jbcrypt.BCrypt

class UserDataSourceImpl(
    private val db: CoroutineDatabase
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
                    timestamp = System.currentTimeMillis()
                )
            )
            users.find(User::username eq userCredentials.username).first()?.id
        } else null
    }

    override suspend fun login(userCredentials: UserDTO): String? {
        val user = users.find(User::username eq userCredentials.username).first()
        return if (user != null) {
            if ( BCrypt.checkpw(userCredentials.password, user.password)) {
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

    override suspend fun getUserById(uid: String): User? {
        return users.find(
            User::id eq uid
        ).first()
    }
}