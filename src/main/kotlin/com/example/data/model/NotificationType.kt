package com.example.data.model

enum class NotificationType(val value: String) {
    REQUEST_FRIENDSHIP("request_friendship"),

    ACCEPTED_FRIENDSHIP("accepted_friendship"),
    DECLINED_FRIENDSHIP("declined_friendship"),

    USER_ACCEPT_FRIENDSHIP("user_accepted_friendship"),
    USER_DECLINED_FRIENDSHIP("user_declined_friendship"),
}