package com.example.dove.data.model

data class Chat(
    val chatId: String? = "",
    val userA: User = User(),
    val userB: User = User(),
    val chatMessages: List<Message>? = listOf()
) {
}