package com.example.di

import com.example.data.chat.ChatController
import com.example.data.room.RoomController
import com.example.data.source.*
import com.example.data.users.UserController
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("chat_db")
    }
    single<UserDataSource> {
        UserDataSourceImpl(get())
    }
    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }
    single<ChatDataSource> {
        ChatDataSourceImpl(get())
    }
    single {
        UserController(get())
    }
    single {
        RoomController(get())
    }
    single {
        ChatController(get())
    }
}