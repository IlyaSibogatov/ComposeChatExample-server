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

    override suspend fun regUser(userCredentials: UserDTO): Boolean {
        return if (users.find(User::username eq userCredentials.username).toList().isEmpty()) {
            users.insertOne(
                User(
                    username = userCredentials.username,
                    password = userCredentials.hashedPassword(),
                    timestamp = System.currentTimeMillis()
                )
            )
            true
        } else false
    }

    override suspend fun login(userCredentials: UserDTO): Boolean {
        val foundedUsers = users.find(
            User::username eq userCredentials.username
        ).toList()
        return if (foundedUsers.isNotEmpty()) {
            BCrypt.checkpw(userCredentials.password, foundedUsers.first().password)
        } else false
    }
}