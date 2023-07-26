package com.example.data.source

import com.example.data.model.user.User
import com.example.data.model.user.UserDTO

interface UserDataSource {

    suspend fun regUser(userCredentials: UserDTO): String?

    suspend fun login(userCredentials: UserDTO): String?

    suspend fun logout(uid: String): Boolean

    suspend fun getUserById(uid: String): User?
}