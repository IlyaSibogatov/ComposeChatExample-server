package com.example.data.source

import com.example.data.model.user.Friend
import com.example.data.model.user.NewUserInfo
import com.example.data.model.user.UserDTO
import com.example.data.model.user.UserFromId

interface AuthDataSource {

    suspend fun regUser(userCredentials: UserDTO): String?

    suspend fun login(userCredentials: UserDTO): String?

    suspend fun logout(uid: String): Boolean

}