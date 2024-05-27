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

    fun getChatName(currentUserId: String): String {
        return if (user1Id == currentUserId) {
            return user2Id ?: ""
        } else {
            return user1Id ?: ""
        }
    }
}