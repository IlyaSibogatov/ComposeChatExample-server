package com.example.data.source

import com.example.data.model.user.User
import com.example.data.model.user.UserDTO
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSourceImpl(
    private val db: CoroutineDatabase
) : UserDataSource {

    private val users = db.getCollection<User>()

    override suspend fun regUser(userCredentials: UserDTO): Boolean {
        return if (users.find(User::username eq userCredentials.username).toList().isEmpty()) {
            users.insertOne(
                User(
                    username = userCredentials.username,
                    password = userCredentials.password,
                    timestamp = System.currentTimeMillis()
                )
            )
            true
        } else false
    }

    override suspend fun login(userCredentials: UserDTO): Boolean =
        users.find(
            User::username eq userCredentials.username,
            User::password eq userCredentials.password,
        ).toList().isNotEmpty()
}