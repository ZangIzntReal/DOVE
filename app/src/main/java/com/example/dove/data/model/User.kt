package com.example.dove.data.model

data class User(
    val userid: String? = "",
    var status : String? = "",
    var imageUrl : String? = "",
    var username: String? = "",
    val email : String? ="",
    var contacts: List<Contact>? = mutableListOf(),
    var chats: List<Chat>? = mutableListOf(),
    var exitChat: List<String>? = mutableListOf()
) {

    public fun addNewChat(chat: Chat) {
        val newChats = chats!!.toMutableList()
        newChats.add(chat)
        chats = newChats
    }

    public fun addExitChat(userId: String?) {
        val newExitChats = exitChat!!.toMutableList()
        newExitChats.add(userId!!)
        exitChat = newExitChats
    }

    public fun addNewContact(contact: Contact) {
        val newContacts = contacts!!.toMutableList()
        newContacts.add(contact)
        contacts = newContacts
    }
}
