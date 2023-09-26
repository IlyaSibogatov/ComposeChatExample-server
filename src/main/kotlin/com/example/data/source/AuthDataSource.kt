package com.example.data.source

import com.example.data.model.user.UserDTO

interface AuthDataSource {

    suspend fun regUser(userCredentials: UserDTO): String

    suspend fun login(userCredentials: UserDTO): String?

    suspend fun logout(uid: String): Boolean

    suspend fun changePass(current: String, new: String, uuid: String): String

    suspend fun deleteAcc(uuid: String)

}