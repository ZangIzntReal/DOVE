package com.example.dove.data.model

data class Chat(
    val id: String? = "",
    val user1Id: String? = "",
    val user2Id: String? = "",
    val chatMessages: List<Message>? = mutableListOf()
) {
    fun addMessage(message: Message) {
        val currentMessages = chatMessages?.toMutableList() ?: mutableListOf()
        currentMessages.add(message)
    }
}